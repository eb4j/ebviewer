package io.github.eb4j.ebview.dictionary.pdic;

import com.ibm.icu.charset.CharsetICU;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * @author wak (Apache-2.0)
 * @author Hiroshi Miura
 */
class PdicInfoCache {
    private final boolean mFix;
    private final int mBlockSize;
    private final RandomAccessFile mFile;
    private final int mStart;
    private final int mSize;
    private final WeakHashMap<Integer, WeakReference<byte[]>> mMap = new WeakHashMap<>();
    private byte[] mFixedBuffer;

    PdicInfoCache(final RandomAccessFile file, final int start, final int size) {
        mFile = file;
        mStart = start;
        mSize = size;
        if (mSize < 1024 * 512) {
            mFix = true;
            mBlockSize = mSize;
        } else {
            mFix = false;
            mBlockSize = 1024;
        }
    }

    byte[] getSegment(final int segment) {
        byte[] segmentdata = null;

        if (mFix) {
            if (mFixedBuffer == null) {
                mFixedBuffer = new byte[mSize];
                try {
                    mFile.seek(mStart);
                    if (mFile.read(mFixedBuffer, 0, mSize) >= 0) {
                        return mFixedBuffer;
                    }
                } catch (IOException ignored) {
                }
            }
        }

        WeakReference<byte[]> ref = mMap.get(segment);
        if (ref != null) {
            segmentdata = ref.get();
        }
        if (segmentdata == null) {
            segmentdata = new byte[mBlockSize];
            try {
                mFile.seek(mStart + (long) segment * mBlockSize);
                int len = mFile.read(segmentdata, 0, mBlockSize);
                if (len == mBlockSize || len == mSize % mBlockSize) {
                    mMap.put(segment, new WeakReference<>(segmentdata));
                } else {
                    return null;
                }
            } catch (IOException e) {
                return null;
            }
        }
        return segmentdata;
    }


    public int getShort(final int ptr) {
        int segment = ptr / mBlockSize;
        int address = ptr % mBlockSize;
        byte[] segmentdata = getSegment(segment++);

        int dat = 0;
        if (segmentdata != null) {
            int b = 0;
            b = segmentdata[address++];
            b &= 0xFF;
            dat |= b;

            if (address >= mBlockSize) {
                address %= mBlockSize;
                segmentdata = getSegment(segment);
            }
            b = segmentdata[address];
            b &= 0xFF;
            dat |= (b << 8);
        }
        return dat;
    }

    public int getInt(final int ptr) {
        int segment = ptr / mBlockSize;
        int address = ptr % mBlockSize;
        byte[] segmentdata = getSegment(segment++);

        int dat = 0;
        if (segmentdata != null) {
            int b = 0;
            b = segmentdata[address++];
            b &= 0xFF;
            dat |= b;
            if (address >= mBlockSize) {
                address %= mBlockSize;
                segmentdata = getSegment(segment++);
            }
            b = segmentdata[address++];
            b &= 0xFF;
            dat |= (b << 8);
            if (address >= mBlockSize) {
                address %= mBlockSize;
                segmentdata = getSegment(segment++);
            }
            b = segmentdata[address++];
            b &= 0xFF;
            dat |= (b << 16);
            if (address >= mBlockSize) {
                address %= mBlockSize;
                segmentdata = getSegment(segment);
            }
            b = segmentdata[address];
            b &= 0x7F;
            dat |= (b << 24);
        }
        return dat;
    }

    @SuppressWarnings("finalparameters")
    private static int compareArrayAsUnsigned(byte[] aa, int pa, int la, byte[] ab, int pb, int lb) {
        while (la-- > 0) {
            short sa = aa[pa++];
            if (lb-- > 0) {
                short sb = ab[pb++];
                if (sa != sb) {
                    sa &= 0xFF;
                    sb &= 0xFF;
                    return (sa - sb);
                }
            } else {
                return 1;
            }
        }
        if (lb > 0) {
            short sb = ab[pb];
            if (sb == 0x09) {        // 比較対象の'\t'は'\0'とみなす
                return 0;
            }
            return -1;
        }
        return 0;
    }

    /**
     *
     * @param aa
     * @param pa
     * @param la
     * @param ptr
     * @param len
     * @return
     */
    @SuppressWarnings("finalparameters")
    public int compare(final byte[] aa, final int pa, final int la, final int ptr, final int len) {
        int segment = ptr / mBlockSize;
        int address = ptr % mBlockSize;
        byte[] segmentdata = getSegment(segment++);

        if (segmentdata == null) {
            return -1;
        }

        if (len < 0) {
            return 1;
        }

        if (address + len < mBlockSize) {
            PdicInfo.decodetoCharBuffer(CharsetICU.forNameICU("BOCU-1"), segmentdata, address, len);
            return compareArrayAsUnsigned(aa, pa, la, segmentdata, address, len);
        } else {
            int lena = mBlockSize - address;
            int leno = Math.min(la, lena);
            int ret = compareArrayAsUnsigned(aa, pa, leno, segmentdata, address, lena);
            PdicInfo.decodetoCharBuffer(CharsetICU.forNameICU("BOCU-1"), segmentdata, address, lena);
            if (ret != 0) {
                return ret;
            }
            if (la < lena) {
                return -1;
            }
            address = 0;
            segmentdata = getSegment(segment);
            PdicInfo.decodetoCharBuffer(CharsetICU.forNameICU("BOCU-1"), segmentdata, address, len - lena);
            return compareArrayAsUnsigned(aa, pa + lena, la - lena, segmentdata, address, len - lena);
        }
    }


    /**
     * Create index of words.
     * @param blockbits
     * @param nindex
     * @param indexPtr
     * @return true when success, otherwise false.
     */
    public boolean createIndex(final int blockbits, final int nindex, final int[] indexPtr) {
        // インデックスの先頭から見出し語のポインタを拾っていく
        int blocksize = 64 * 1024;
        int[] params = new int[]{0, 0, nindex, blocksize, blockbits, 1, 0};

        boolean hasNext = true;
        for (int i = 0; hasNext; i++) {
            hasNext = countIndexWords(params, getSegment(i), indexPtr);
        }
        indexPtr[params[0]] = params[1] + blockbits; // ターミネータを入れておく
        return true;
    }

    private boolean countIndexWords(final int[] params, final byte[] buff, final int[] indexPtr) {
        int curidx = params[0];
        int curptr = params[1];
        int max = params[2];
        int buffmax = params[3];
        int blockbits = params[4];
        int found = params[5];
        int ignore = params[6];

        int i = 0;

        for (; i < buffmax && curidx < max; i++) {
            if (ignore > 0) {
                ignore--;
            } else if (found != 0) {
                int ptr = curptr + i + blockbits;  // ブロック番号サイズポインタを進める
                indexPtr[curidx++] = ptr;          // 見出し語部分のポインタを保存
                ignore = blockbits - 1;
                found = 0;
            } else if (buff[i] == 0) {
                found = 1;
            }
        }

        params[0] = curidx;
        params[1] = curptr + i;
        params[5] = found;
        params[6] = ignore;
        return curidx < max;
    }

}
