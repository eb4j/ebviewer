package io.github.eb4j.ebview.utils;

import java.text.BreakIterator;
import java.util.LinkedList;

/**
 * BreakIterator for word-breaks.
 *
 * @see BreakIterator#getWordInstance
 * @author Maxym Mykhalchuk
 */
public class WordIterator extends BreakIterator {
    BreakIterator breaker;
    String text;

    public WordIterator() {
        breaker = BreakIterator.getWordInstance();
    }

    /**
     * Set a new text string to be scanned. The current scan position is reset
     * to first().
     *
     * @param newText
     *            new text to scan.
     */
    public void setText(String newText) {
        text = newText;
        breaker.setText(newText);
        nextItems.clear();
    }

    /**
     * Return the first boundary. The iterator's current position is set to the
     * first boundary.
     *
     * @return The character index of the first text boundary.
     */
    public int first() {
        return breaker.first();
    }

    /**
     * Return character index of the text boundary that was most recently
     * returned by next(), previous(), first(), or last()
     *
     * @return The boundary most recently returned.
     */
    public int current() {
        return breaker.current();
    }

    LinkedList<Integer> nextItems = new LinkedList<>();

    /**
     * Return the boundary of the word following the current boundary.
     * <p>
     * Note: This iterator skips OmegaT-specific tags, and groups
     * [text-]mnemonics-text into a single token.
     *
     * @return The character index of the next text boundary or DONE if all
     *         boundaries have been returned. Equivalent to next(1).
     */
    public int next() {
        if (!nextItems.isEmpty()) {
            return nextItems.removeFirst();
        }

        int curr = current();
        int next = breaker.next();
        if (DONE == next) {
            return DONE;
        }

        String str = text.substring(curr, next);

        if (str.equals("&")) {
            // trying to see the mnemonic
            int next2 = breaker.next();
            if (DONE == next2) {
                return next;
            }

            String str2 = text.substring(next, next2);
            if (Character.isLetterOrDigit(str2.codePointAt(0))) {
                return next2;
            } else {
                // rewind back once
                breaker.previous();
                return next;
            }
        } else if (Character.isLetterOrDigit(str.codePointAt(0))) {
            // trying to see whether the next "word" is a "&"
            int next2 = breaker.next();
            if (DONE == next2) {
                return next;
            }

            String str2 = text.substring(next, next2);
            if (str2.equals("&")) { // yes, it's there
                int next3 = breaker.next();
                if (DONE == next3) {
                    // Something&
                    nextItems.add(next2);
                    return next;
                }

                String str3 = text.substring(next2, next3);
                // is it followed by a word like Some&thing
                if (Character.isLetterOrDigit(str3.codePointAt(0))) {
                    return next3; // oh yes
                } else { // oh no
                    // rewind back two times
                    breaker.previous();
                    breaker.previous();
                    return next;
                }
            } else {
                // rewind back once
                breaker.previous();
                return next;
            }
        } else {
            return next;
        }
    }

    // ////////////////////////////////////////////////////////////////////////
    // Not yet implemented
    // ////////////////////////////////////////////////////////////////////////

    /**
     * <b>Not yet implemented! Throws a RuntimeException if you try to call
     * it.</b>
     *
     * Return the nth boundary from the current boundary
     *
     * @param n
     *            which boundary to return. A value of 0 does nothing. Negative
     *            values move to previous boundaries and positive values move to
     *            later boundaries.
     * @return The index of the nth boundary from the current position.
     */
    public int next(int n) {
        throw new RuntimeException("Not Implemented");
    }

    /**
     * <b>Not yet implemented! Throws a RuntimeException if you try to call
     * it.</b>
     *
     * Return the first boundary following the specified offset. The value
     * returned is always greater than the offset or the value
     * BreakIterator.DONE
     *
     * @param offset
     *            the offset to begin scanning. Valid values are determined by
     *            the CharacterIterator passed to setText(). Invalid values
     *            cause an IllegalArgumentException to be thrown.
     * @return The first boundary after the specified offset.
     */
    public int following(int offset) {
        throw new RuntimeException("Not Implemented");
    }

    /**
     * <b>Not yet implemented! Throws a RuntimeException if you try to call
     * it.</b>
     *
     * Set a new text for scanning. The current scan position is reset to
     * first().
     *
     * @param newText
     *            new text to scan.
     */
    public void setText(java.text.CharacterIterator newText) {
        throw new RuntimeException("Not Implemented");
    }

    /**
     * <b>Not yet implemented! Throws a RuntimeException if you try to call
     * it.</b>
     *
     * Get the text being scanned
     *
     * @return the text being scanned
     */
    public java.text.CharacterIterator getText() {
        throw new RuntimeException("Not Implemented");
    }

    /**
     * <b>Not yet implemented! Throws a RuntimeException if you try to call
     * it.</b>
     *
     * Return the boundary preceding the current boundary.
     *
     * @return The character index of the previous text boundary or DONE if all
     *         boundaries have been returned.
     */
    public int previous() {
        throw new RuntimeException("Not Implemented");
    }

    /**
     * <b>Not yet implemented! Throws a RuntimeException if you try to call
     * it.</b>
     *
     * Return the last boundary. The iterator's current position is set to the
     * last boundary.
     *
     * @return The character index of the last text boundary.
     */
    public int last() {
        throw new RuntimeException("Not Implemented");
    }

}
