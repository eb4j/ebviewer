package io.github.eb4j.ebview.dictionary.pdic;

import com.ibm.icu.charset.CharsetICU;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.WeakHashMap;

/**
 * @author wak (Apache-2.0)
 * @author Hiroshi Miura
 */
@SuppressWarnings("membername")
class PdicInfo {
    protected File m_file;
    protected int m_bodyptr;
    protected PdicResult mSearchResult;

    protected int m_start;
    protected int m_size;
    protected int m_blockbits;
    protected int m_nindex;
    protected int m_blocksize;
    protected boolean m_match;
    protected int m_searchmax; // 最大検索件数
    protected String m_dicname; // 辞書名

    protected int[] mIndexPtr;

    protected Charset mMainCharset;
    protected Charset mPhoneCharset;
    protected WeakHashMap<String, ByteBuffer> mEncodeCache = new WeakHashMap<>();

    protected AnalyzeBlock mAnalyze;
    protected int mLastIndex = 0;
    protected PdicInfoCache mPdicInfoCache;

    private RandomAccessFile mSrcStream = null;

    @SuppressWarnings("avoidinlineconditionals")
    PdicInfo(final File file, final int start, final int size, final int nindex, final boolean blockbits,
             final int blocksize) {
        m_file = file;
        m_start = start;
        m_size = size;
        m_nindex = nindex;
        m_blockbits = (blockbits) ? 4 : 2;
        m_blocksize = blocksize;
        m_searchmax = 10;

        mSearchResult = new PdicResult();
        mPhoneCharset = CharsetICU.forNameICU("BOCU-1");
        mMainCharset = CharsetICU.forNameICU("BOCU-1");
        try {
            mSrcStream = new RandomAccessFile(m_file, "r");
            mAnalyze = new AnalyzeBlock();
            mPdicInfoCache = new PdicInfoCache(mSrcStream, m_start, m_size);
        } catch (FileNotFoundException ignored) {
        }
    }

    /**
     * byte配列の本文文字列をCharBufferに変換する
     */
    static public CharBuffer decodetoCharBuffer(Charset cs, byte[] array, int pos, int len) {
        return cs.decode(ByteBuffer.wrap(array, pos, len));
    }

    /**
     * 本文の文字列をByteBufferに変換する
     */
    static protected ByteBuffer encodetoByteBuffer(Charset cs, String str) {
        return cs.encode(str);
    }

    /**
     * インデックス領域を検索.
     *
     * @return index of block
     */
    public int searchIndexBlock(final String word) {
        int min = 0;
        int max = m_nindex - 1;

        ByteBuffer __word = mEncodeCache.get(word);
        if (__word == null) {
            __word = encodetoByteBuffer(mMainCharset, word);
            mEncodeCache.put(word, __word);
        }
        int limit = __word.limit();
        byte[] _word = new byte[limit];
        System.arraycopy(__word.array(), 0, _word, 0, limit);
        int _wordlen = _word.length;

        int[] indexPtr = mIndexPtr;
        int blockbits = m_blockbits;
        PdicInfoCache pdicInfoCache = mPdicInfoCache;

        for (int i = 0; i < 32; i++) {
            if ((max - min) <= 1) {
                return min;
            }
            final int look = (int) (((long) min + max) / 2);
            final int len = indexPtr[look + 1] - indexPtr[look] - blockbits;
            final int comp = pdicInfoCache.compare(_word, 0, _wordlen, indexPtr[look], len);
            if (comp < 0) {
                max = look;
            } else if (comp > 0) {
                min = look;
            } else {
                return look;
            }
        }
        return min;
    }

    /**
     * Read index blocks.
     *
     * @return true when successfully read block, otherwise false.
     */
    public boolean readIndexBlock(String indexcache) {
        if (mSrcStream != null) {
            m_bodyptr = m_start + m_size; // 本体位置=( index開始位置＋インデックスのサイズ)
            if (indexcache != null) {
                try (FileInputStream fis = new FileInputStream(indexcache)) {
                    byte[] buff = new byte[(m_nindex + 1) * 4];
                    int readlen = fis.read(buff);
                    if (readlen == buff.length) {
                        final int indexlen = m_nindex;
                        final int[] indexptr = mIndexPtr = new int[m_nindex + 1];
                        int ptr = 0;
                        for (int i = 0; i <= indexlen; i++) {
                            int b;
                            int dat;
                            b = buff[ptr++];
                            b &= 0xFF;
                            dat = b;
                            b = buff[ptr++];
                            b &= 0xFF;
                            dat |= (b << 8);
                            b = buff[ptr++];
                            b &= 0xFF;
                            dat |= (b << 16);
                            b = buff[ptr++];
                            b &= 0xFF;
                            dat |= (b << 24);
                            indexptr[i] = dat;
                        }
                        return true;
                    }
                } catch (IOException ignored) {
                }
            }

            // インデックスの先頭から見出し語のポインタを拾っていく
            final int nindex = m_nindex;
            final int[] indexPtr = mIndexPtr = new int[nindex + 1]; // インデックスポインタの配列確保
            if (mPdicInfoCache.createIndex(m_blockbits, nindex, indexPtr)) {
                byte[] buff = new byte[indexPtr.length * 4];
                int p = 0;
                for (int c = 0; c <= nindex; c++) {
                    int data = indexPtr[c];
                    buff[p++] = (byte) (data & 0xFF);
                    data >>= 8;
                    buff[p++] = (byte) (data & 0xFF);
                    data >>= 8;
                    buff[p++] = (byte) (data & 0xFF);
                    data >>= 8;
                    buff[p++] = (byte) (data & 0xFF);
                }
                if (indexcache != null) {
                    try (FileOutputStream fos = new FileOutputStream(indexcache)) {
                        fos.write(buff, 0, buff.length);
                    } catch (IOException ignored) {
                    }
                }
                return true;
            }
        }
        mIndexPtr = null;
        return false;
    }

    /**
     * num個目の見出し語の実体が入っているブロック番号を返す.
     */
    public int getBlockNo(int num) {
        int blkptr = mIndexPtr[num] - m_blockbits;
        mLastIndex = num;
        if (m_blockbits == 4) {
            return mPdicInfoCache.getInt(blkptr);
        } else {
            return mPdicInfoCache.getShort(blkptr);
        }
    }

    /**
     * 次の０までの長さを返す.
     *
     * @param array target byte array
     * @param pos start position
     * @return length of index.
     */
    static protected int getLengthToNextZero(final byte[] array, final int pos) {
        return ArrayUtils.indexOf(array, (byte) 0, pos) - pos;
        // int len = 0;
        // while (array[pos + len] != 0)
        //     len++;
        // return len;
    }

    boolean IsMatch() {
        return m_match;
    }

    public String GetFilename() {
        return m_file.getName();
    }

    public int GetSearchMax() {
        return m_searchmax;
    }

    public void SetSearchMax(int m) {
        m_searchmax = m;
    }

    public void SetDicName(String b) {
        m_dicname = b;
    }

    public String GetDicName() {
        return m_dicname;
    }

    // 単語を検索する
    public boolean searchWord(String _word) {
        // 検索結果クリア
        int cnt = 0;
        mSearchResult.clear();

        int ret = searchIndexBlock(_word);

        boolean match = false;

        boolean searchret = false;
        while (true) {
            // 最終ブロックは超えない
            if (ret < m_nindex) {
                // 該当ブロック読み出し
                int block = getBlockNo(ret++);
                byte[] pblk = readBlockData(block);
                if (pblk != null) {
                    mAnalyze.setBuffer(pblk);
                    mAnalyze.setSearch(_word);
                    searchret = mAnalyze.searchWord();
                    // 未発見でEOBの時のみもう一回、回る
                    if (!searchret && mAnalyze.mEob) {
                        continue;
                    }
                }
            }
            // 基本一回で抜ける
            break;
        }
        if (searchret) {
            // 前方一致するものだけ結果に入れる
            do {
                PdicElement res = mAnalyze.getRecord();
                if (res == null) {
                    break;
                }
                // 完全一致するかチェック
                if (res.mIndex.compareTo(_word) == 0) {
                    match = true;
                }
                mSearchResult.add(res);

                cnt++;
                // 取得最大件数超えたら打ち切り
            } while (cnt < m_searchmax && hasMoreResult(true));
        }
        return match;
    }

    // 前方一致する単語の有無を返す
    boolean searchPrefix(final String _word) {
        int ret = searchIndexBlock(_word);

        for (int blk = 0; blk < 2; blk++) {
            // 最終ブロックは超えない
            if (ret + blk >= m_nindex) {
                break;
            }
            int block = getBlockNo(ret + blk);

            // 該当ブロック読み出し
            byte[] pblk = readBlockData(block);

            if (pblk != null) {
                mAnalyze.setBuffer(pblk);
                mAnalyze.setSearch(_word);

                if (mAnalyze.searchWord()) {
                    return true;
                }
            }
        }
        return false;
    }

    PdicResult getResult() {
        return mSearchResult;
    }

    public PdicResult getMoreResult() {
        mSearchResult.clear();
        if (mAnalyze != null) {
            int cnt = 0;
            // 前方一致するものだけ結果に入れる
            while (cnt < m_searchmax && hasMoreResult(true)) {
                PdicElement res = mAnalyze.getRecord();
                if (res == null) {
                    break;
                }
                mSearchResult.add(res);
                cnt++;
            }
        }
        return mSearchResult;
    }

    public boolean hasMoreResult(final boolean incrementptr) {
        boolean result = mAnalyze.hasMoreResult(incrementptr);
        if (!result) {
            if (mAnalyze.isEob()) {    // EOBなら次のブロック読み出し
                int nextindex = mLastIndex + 1;
                // 最終ブロックは超えない
                if (nextindex < m_nindex) {
                    int block = getBlockNo(nextindex);

                    // 該当ブロック読み出し
                    byte[] pblk = readBlockData(block);

                    if (pblk != null) {
                        mAnalyze.setBuffer(pblk);
                        result = mAnalyze.hasMoreResult(incrementptr);
                    }
                }
            }
        }
        return result;
    }

    /**
     * データブロックを読み込み.
     *
     * @param blkno
     * @return 読み込まれたデータブロック
     */
    byte[] readBlockData(final int blkno) {
        byte[] buff = new byte[0x200];
        byte[] pbuf = buff;
        try {
            mSrcStream.seek(m_bodyptr + (long) blkno * m_blocksize);

            // 1ブロック分読込(１セクタ分先読み)
            if (mSrcStream.read(pbuf, 0, 0x200) < 0) {
                return null;
            }

            // 長さ取得
            int len = ((int) (pbuf[0])) & 0xFF;
            len |= (((int) (pbuf[1])) & 0xFF) << 8;

            // ブロック長判定
            if ((len & 0x8000) != 0) { // 32bit
                len &= 0x7FFF;
            }
            if (len > 0) {
                // ブロック不足分読込
                if (len * m_blocksize > 0x200) {
                    pbuf = new byte[m_blocksize * len];
                    System.arraycopy(buff, 0, pbuf, 0, 0x200);
                    if (mSrcStream.read(pbuf, 0x200, len * m_blocksize - 0x200) < 0) {
                        return null;
                    }
                }
            } else {
                pbuf = null;
            }
            return pbuf;
        } catch (IOException ignored) {
        }
        return null;
    }

    final class AnalyzeBlock {
        private byte[] mBuff;
        private boolean mLongfield;
        private byte[] mWord;
        private int mFoundPtr = -1;
        private int mNextPtr = -1;
        private final byte[] mCompbuff = new byte[1024];
        private int mCompLen = 0;
        private boolean mEob = false;

        public AnalyzeBlock() {
        }

        public void setBuffer(byte[] buff) {
            mBuff = buff;
            mLongfield = ((buff[1] & 0x80) != 0);
            ByteBuffer mBB = ByteBuffer.wrap(buff);
            mBB.order(ByteOrder.LITTLE_ENDIAN);
            mNextPtr = 2;
            mEob = false;
            mCompLen = 0;
        }

        public void setSearch(String word) {
            ByteBuffer __word = encodetoByteBuffer(mMainCharset, word);
            mEncodeCache.put(word, __word);
            mWord = new byte[__word.limit()];
            System.arraycopy(__word.array(), 0, mWord, 0, __word.limit());
        }

        public boolean isEob() {
            return mEob;
        }

        /**
         * ブロックデータの中から指定語を探す
         */
        public boolean searchWord() {
            final byte[] _word = mWord;
            final byte[] buff = mBuff;
            final boolean longfield = mLongfield;
            final byte[] compbuff = mCompbuff;
            final int wordlen = _word.length;

            mFoundPtr = -1;

            // 訳語データ読込
            int ptr = mNextPtr;
            mNextPtr = -1;
            while (true) {
                int flen = 0;
                int retptr = ptr;
                int b;

                b = buff[ptr++];
                flen |= (b & 0xFF);

                b = buff[ptr++];
                b <<= 8;
                flen |= (b & 0xFF00);

                if (longfield) {
                    b = buff[ptr++];
                    b <<= 16;
                    flen |= (b & 0xFF0000);

                    b = buff[ptr++];
                    b <<= 24;
                    flen |= (b & 0x7F000000);
                }
                if (flen == 0) {
                    mEob = true;
                    break;
                }
                int qtr = ptr;
                ptr += flen + 1;
                ptr++;


                // 圧縮長
                int complen = (int) buff[qtr++];
                complen &= 0xFF;

                // 見出し語属性 skip
                qtr++;

                // 見出し語圧縮位置保存
                while ((compbuff[complen++] = buff[qtr++]) != 0) ;

                // 見出し語の方が短ければ不一致
                if (complen < wordlen) {
                    continue;
                }


                // 前方一致で比較
                boolean equal = true;
                for (int i = 0; i < wordlen; i++) {

                    if (compbuff[i] != _word[i]) {
                        equal = false;
                        int cc = compbuff[i];
                        cc &= 0xFF;
                        int cw = _word[i];
                        cw &= 0xFF;
                        // 超えてたら打ち切る
                        if (cc > cw) {
                            return false;
                        }
                        break;
                    }
                }
                if (equal) {
                    mFoundPtr = retptr;
                    mNextPtr = ptr;
                    mCompLen = complen - 1;
                    return true;
                }
            }
            return false;
        }

        /**
         * 最後の検索結果の単語を返す
         *
         * @return
         */
        PdicElement getRecord() {
            if (mFoundPtr == -1) {
                return null;
            }
            final PdicElement res = new PdicElement();

            final byte[] compbuff = mCompbuff;
            res.mIndex = decodetoCharBuffer(mMainCharset, compbuff, 0, mCompLen).toString();
            // ver6対応 見出し語が、<検索インデックス><TAB><表示用文字列>の順に
            // 設定されていてるので、分割する。
            // それ以前のverではdispに空文字列を保持させる。

            final String indexstr = res.mIndex;
            final int tab = indexstr.indexOf('\t');
            if (tab == -1) {
                res.mDisp = "";
            } else {
                res.mIndex = indexstr.substring(0, tab);
                res.mDisp = indexstr.substring(tab + 1);
            }

            final byte[] buff = mBuff;
            final boolean longfield = mLongfield;
            byte attr = 0;

            // 訳語データ読込
            int ptr = mFoundPtr;

            if (longfield) {
                ptr += 4;
            } else {
                ptr += 2;
            }
            int qtr = ptr;

            // 圧縮長
            // int complen = buff[qtr++];
            // complen &= 0xFF;
            qtr++;

            // 見出し語属性 skip
            attr = buff[qtr++];

            while (buff[qtr++] != 0) {
                // 見出し語 skip
            }

            // 訳語
            if ((attr & 0x10) != 0) { // 拡張属性ありの時
                int trnslen = getLengthToNextZero(buff, qtr);
                res.mTrans = decodetoCharBuffer(mMainCharset, buff, qtr, trnslen).toString().replace("\r", "");
                qtr += trnslen; // 次のNULLまでスキップ

                // 拡張属性取得
                byte eatr;
                while (((eatr = buff[qtr++]) & 0x80) == 0) {
                    if ((eatr & (0x10 | 0x40)) == 0) { // バイナリOFF＆圧縮OFFの場合
                        if ((eatr & 0x0F) == 0x01) { // 用例
                            int len = getLengthToNextZero(buff, qtr);
                            res.mSample = decodetoCharBuffer(mMainCharset, buff, qtr, len).toString().replace("\r", "");
                            qtr += len; // 次のNULLまでスキップ
                        } else if ((eatr & 0x0F) == 0x02) { // 発音
                            int len = getLengthToNextZero(buff, qtr);
                            res.mPhone = decodetoCharBuffer(mPhoneCharset, buff, qtr, len).toString();
                            qtr += len; // 次のNULLまでスキップ
                        }
                    } else {
                        // バイナリ属性か圧縮属性が来たら打ち切り
                        break;
                    }
                }
            } else {
                // 残り全部が訳文
                res.mTrans = decodetoCharBuffer(mMainCharset, buff, qtr, mNextPtr - qtr).toString().replace("\r", "");
            }
            return res;
        }

        // 次の項目が検索語に前方一致するかチェックする
        public boolean hasMoreResult(final boolean incrementptr) {
            byte[] _word;
            final byte[] buff = mBuff;
            final boolean longfield = mLongfield;
            final byte[] compbuff = mCompbuff;

            // next search
            if (mFoundPtr == -1) {
                return false;
            }
            _word = mWord;

            int wordlen = _word.length;

            // 訳語データ読込
            int ptr = mNextPtr;

            int retptr = ptr;
            int flen;
            int b;

            b = buff[ptr++];
            flen = (b & 0xFF);

            b = buff[ptr++];
            b <<= 8;
            flen |= (b & 0xFF00);

            if (longfield) {
                b = buff[ptr++];
                b <<= 16;
                flen |= (b & 0xFF0000);

                b = buff[ptr++];
                b <<= 24;
                flen |= (b & 0x7F000000);
            }
            if (flen == 0) {
                mEob = true;
                return false;
            }
            int qtr = ptr;
            ptr += flen + 1;
            ptr++;

            // 圧縮長
            int complen = buff[qtr++];
            complen &= 0xFF;

            // 見出し語属性 skip
            qtr++;

            // 見出し語圧縮位置保存
            while ((compbuff[complen++] = buff[qtr++]) != 0) ;

            // 見出し語の方が短ければ不一致
            if (complen < wordlen) {
                return false;
            }

            // 前方一致で比較
            boolean equal = true;
            for (int i = 0; i < wordlen; i++) {
                if (compbuff[i] != _word[i]) {
                    equal = false;
                    int cc = compbuff[i];
                    cc &= 0xFF;
                    int cw = _word[i];
                    cw &= 0xFF;
                    // 超えてたら打ち切る
                    if (cc > cw) {
                        return false;
                    }
                    break;
                }
            }
            if (equal && incrementptr) {
                mFoundPtr = retptr;
                mNextPtr = ptr;
                mCompLen = complen - 1;
            }
            return equal;
        }
    }
}
