package com.jenkov.parsers.tokenizers;

import com.jenkov.parsers.TokenTypes;
import com.jenkov.parsers.Tokenizer2;
import com.jenkov.parsers.TokenizerListenerIndexImpl;
import com.jenkov.parsers.unicode.Utf8Buffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicTokenizerTest {

    @Test
    public void testTokenize() {
        String data = " + - \"this is a quoted token \" * &abc()def349.87iuy:899";

        Utf8Buffer utf8Buffer = new Utf8Buffer(new byte[1024], 0, 1024);

        utf8Buffer.writeCodepoints(data);
        utf8Buffer.calculateLengthAndEndOffset().rewind();

        Tokenizer2 tokenizer = new Tokenizer2();

        TokenizerListenerIndexImpl listenerImpl = new TokenizerListenerIndexImpl(1024);
        tokenizer.tokenize(utf8Buffer, listenerImpl);

        assertEquals(18, listenerImpl.nextIndex);

        assertEquals(0, listenerImpl.startOffsets[0]);
        assertEquals(1, listenerImpl.endOffsets[0]);
        assertEquals(com.jenkov.parsers.TokenTypes.WHITE_SPACE, listenerImpl.tokenTypes[0]);

        assertEquals(1, listenerImpl.startOffsets[1]);
        assertEquals(2, listenerImpl.endOffsets[1]);
        assertEquals('+', listenerImpl.tokenTypes[1]);

        assertEquals(2, listenerImpl.startOffsets[2]);
        assertEquals(3, listenerImpl.endOffsets[2]);
        assertEquals(com.jenkov.parsers.TokenTypes.WHITE_SPACE, listenerImpl.tokenTypes[2]);

        assertEquals(3, listenerImpl.startOffsets[3]);
        assertEquals(4, listenerImpl.endOffsets[3]);
        assertEquals('-', listenerImpl.tokenTypes[3]);

        assertEquals(4, listenerImpl.startOffsets[4]);
        assertEquals(5, listenerImpl.endOffsets[4]);
        assertEquals(com.jenkov.parsers.TokenTypes.WHITE_SPACE, listenerImpl.tokenTypes[4]);

        assertEquals(5, listenerImpl.startOffsets[5]);
        assertEquals(30, listenerImpl.endOffsets[5]);
        assertEquals(com.jenkov.parsers.TokenTypes.QUOTED_TOKEN, listenerImpl.tokenTypes[5]);

        assertEquals(30, listenerImpl.startOffsets[6]);
        assertEquals(31, listenerImpl.endOffsets[6]);
        assertEquals(com.jenkov.parsers.TokenTypes.WHITE_SPACE, listenerImpl.tokenTypes[6]);

        assertEquals(31, listenerImpl.startOffsets[7]);
        assertEquals(32, listenerImpl.endOffsets[7]);
        assertEquals('*', listenerImpl.tokenTypes[7]);

        assertEquals(32, listenerImpl.startOffsets[8]);
        assertEquals(33, listenerImpl.endOffsets[8]);
        assertEquals(com.jenkov.parsers.TokenTypes.WHITE_SPACE, listenerImpl.tokenTypes[8]);

        assertEquals(33, listenerImpl.startOffsets[9]);
        assertEquals(34, listenerImpl.endOffsets[9]);
        assertEquals('&', listenerImpl.tokenTypes[9]);

        assertEquals(34, listenerImpl.startOffsets[10]);
        assertEquals(37, listenerImpl.endOffsets[10]);
        assertEquals(com.jenkov.parsers.TokenTypes.ALPHABETIC, listenerImpl.tokenTypes[10]);

        assertEquals(37, listenerImpl.startOffsets[11]);
        assertEquals(38, listenerImpl.endOffsets[11]);
        assertEquals('(', listenerImpl.tokenTypes[11]);

        assertEquals(38, listenerImpl.startOffsets[12]);
        assertEquals(39, listenerImpl.endOffsets[12]);
        assertEquals(')', listenerImpl.tokenTypes[12]);

        assertEquals(39, listenerImpl.startOffsets[13]);
        assertEquals(45, listenerImpl.endOffsets[13]);
        assertEquals(com.jenkov.parsers.TokenTypes.ALPHA_NUMERIC, listenerImpl.tokenTypes[13]);

        assertEquals(45, listenerImpl.startOffsets[14]);
        assertEquals(46, listenerImpl.endOffsets[14]);
        assertEquals('.', listenerImpl.tokenTypes[14]);

        assertEquals(46, listenerImpl.startOffsets[15]);
        assertEquals(51, listenerImpl.endOffsets[15]);
        assertEquals(com.jenkov.parsers.TokenTypes.ALPHA_NUMERIC, listenerImpl.tokenTypes[13]);

        assertEquals(51, listenerImpl.startOffsets[16]);
        assertEquals(52, listenerImpl.endOffsets[16]);
        assertEquals(':', listenerImpl.tokenTypes[16]);

        assertEquals(52, listenerImpl.startOffsets[17]);
        assertEquals(55, listenerImpl.endOffsets[17]);
        assertEquals(TokenTypes.NUMERIC, listenerImpl.tokenTypes[17]);


        //String data = " + - \"this is a quoted token \" * &abc()def349.87iuy:899";


        System.out.println("Done");

    }
}
