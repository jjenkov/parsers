package com.jenkov.parsers.tokenizers;

import com.jenkov.parsers.unicode.Utf8Buffer;
import org.junit.jupiter.api.Test;

import static com.jenkov.parsers.tokenizers.TokenizerTestUtil.assertToken;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicTokenizerTest {

    @Test
    public void testTokenize() {
        String data = " + - \"this is a quoted token \" * &abc()def349.87iuy:899/*abc*/";

        Utf8Buffer utf8Buffer = new Utf8Buffer(new byte[1024], 0, 1024);

        utf8Buffer.writeCodepoints(data);
        utf8Buffer.calculateLengthAndEndOffset().rewind();

        BasicTokenizer tokenizer = new BasicTokenizer();

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

        //String data = " + - \"this is a quoted token \" * &abc()def349.87iuy:899";

        System.out.println("Done");
    }


    @Test
    public void testTokenizeJsons() {
        String data = """
                       {\"field1\" : \"value1\", \"field2\":123} 
                      """ ;

        System.out.println("'" + data + "'");

        Utf8Buffer utf8Buffer = new Utf8Buffer(new byte[1024], 0, 1024);

        utf8Buffer.writeCodepoints(data);
        utf8Buffer.calculateLengthAndEndOffset().rewind();

        BasicTokenizer tokenizer = new BasicTokenizer();

        TokenizerListenerIndexImpl listenerImpl = new TokenizerListenerIndexImpl(1024);
        tokenizer.tokenize(utf8Buffer, listenerImpl);

        assertEquals(14, listenerImpl.nextIndex);

        assertToken(listenerImpl,  0, 0, 1, TokenTypes.WHITE_SPACE);
        assertToken(listenerImpl,  1, 1, 2, '{');
        assertToken(listenerImpl,  2, 2, 10, TokenTypes.QUOTED_TOKEN);
        assertToken(listenerImpl,  3, 10, 11, TokenTypes.WHITE_SPACE);
        assertToken(listenerImpl,  4, 11, 12, ':');
        assertToken(listenerImpl,  5, 12, 13, TokenTypes.WHITE_SPACE);
        assertToken(listenerImpl,  6, 13, 21, TokenTypes.QUOTED_TOKEN);
        assertToken(listenerImpl,  7, 21, 22, ',');
        assertToken(listenerImpl,  8, 22, 23, TokenTypes.WHITE_SPACE);
        assertToken(listenerImpl,  9, 23, 31, TokenTypes.QUOTED_TOKEN);
        assertToken(listenerImpl, 10, 31, 32, ':');
        assertToken(listenerImpl, 11, 32, 35, TokenTypes.NUMERIC);
        assertToken(listenerImpl, 12, 35, 36, '}');
        assertToken(listenerImpl, 13, 36, 37, TokenTypes.WHITE_SPACE);

    }

    @Test
    public void testSpecificString() {
        String data = "select /* this is a comment */*from dept;";

        Utf8Buffer utf8Buffer = new Utf8Buffer(new byte[1024], 0, 1024);

        utf8Buffer.writeCodepoints(data);
        utf8Buffer.calculateLengthAndEndOffset().rewind();

        BasicTokenizer tokenizer = new BasicTokenizer();

        TokenizerListenerIndexImpl listenerImpl = new TokenizerListenerIndexImpl(1024);
        tokenizer.tokenize(utf8Buffer, listenerImpl);

        assertEquals(8, listenerImpl.nextIndex);

        assertToken(listenerImpl,  0, 0, 6, TokenTypes.ALPHABETIC);
        assertToken(listenerImpl,  1, 6, 7, TokenTypes.WHITE_SPACE);
        assertToken(listenerImpl,  2, 7, 30, TokenTypes.COMMENT);
        assertToken(listenerImpl,  3, 30, 31, '*');
        assertToken(listenerImpl,  4, 31, 35, TokenTypes.ALPHABETIC);
        assertToken(listenerImpl,  5, 35, 36, TokenTypes.WHITE_SPACE);
        assertToken(listenerImpl,  6, 36, 40, TokenTypes.ALPHABETIC);
        assertToken(listenerImpl,  7, 40, 41, ';');


    }
}
