package com.jenkov.parsers.tokenizers;

import com.jenkov.parsers.unicode.Utf8Buffer;
import org.junit.jupiter.api.Test;

import static com.jenkov.parsers.tokenizers.TokenizerTestUtil.assertToken;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicTokenizerMethodizedTest {

    @Test
    public void testTokenize() {
        String data = " + - \"this is a quoted token \" * &abc()def349.87iuy:899/*abc*/";

        System.out.println(data.length());

        Utf8Buffer utf8Buffer = new Utf8Buffer(new byte[1024], 0, 1024);

        utf8Buffer.writeCodepoints(data);
        utf8Buffer.calculateLengthAndEndOffset().rewind();

        BasicTokenizerMethodized tokenizer = new BasicTokenizerMethodized();

        TokenizerListenerIndexImpl listenerImpl = new TokenizerListenerIndexImpl(1024);
        tokenizer.tokenize(utf8Buffer, listenerImpl);

        assertEquals(19, listenerImpl.nextIndex);

        assertToken(listenerImpl,  0,  0,  1, TokenTypes.WHITE_SPACE);
        assertToken(listenerImpl,  1,  1,  2, '+');
        assertToken(listenerImpl,  2,  2,  3, TokenTypes.WHITE_SPACE);
        assertToken(listenerImpl,  3,  3,  4, '-');
        assertToken(listenerImpl,  4,  4,  5, TokenTypes.WHITE_SPACE);
        assertToken(listenerImpl,  5,  5, 30, TokenTypes.QUOTED_TOKEN);
        assertToken(listenerImpl,  6, 30, 31, TokenTypes.WHITE_SPACE);
        assertToken(listenerImpl,  7, 31, 32, '*');
        assertToken(listenerImpl,  8, 32, 33, TokenTypes.WHITE_SPACE);
        assertToken(listenerImpl,  9, 33, 34, '&');
        assertToken(listenerImpl, 10, 34, 37, TokenTypes.ALPHABETIC);
        assertToken(listenerImpl, 11, 37, 38, '(');
        assertToken(listenerImpl, 12, 38, 39, ')');
        assertToken(listenerImpl, 13, 39, 45, TokenTypes.ALPHA_NUMERIC);
        assertToken(listenerImpl, 14, 45, 46, '.');
        assertToken(listenerImpl, 15, 46, 51, TokenTypes.ALPHA_NUMERIC);
        assertToken(listenerImpl, 16, 51, 52, ':');
        assertToken(listenerImpl, 17, 52, 55, TokenTypes.NUMERIC);
        assertToken(listenerImpl, 18, 55, 62, TokenTypes.COMMENT);


        System.out.println("Done");

    }

    @Test
    public void testSpecificString() {
        String data = "select /* this is a comment */*from dept;";

        Utf8Buffer utf8Buffer = new Utf8Buffer(new byte[1024], 0, 1024);

        utf8Buffer.writeCodepoints(data);
        utf8Buffer.calculateLengthAndEndOffset().rewind();

        BasicTokenizerMethodized tokenizer = new BasicTokenizerMethodized();

        TokenizerListenerIndexImpl listenerImpl = new TokenizerListenerIndexImpl(1024);
        tokenizer.tokenize(utf8Buffer, listenerImpl);

        assertEquals(8, listenerImpl.nextIndex);


    }
}
