package com.jenkov.parsers.tokenizers;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenizerTestUtil {

    public static void assertToken(TokenizerListenerIndexImpl listenerImpl, int tokenIndex, int startOffset, int endOffset, int tokenType) {
        assertEquals(startOffset, listenerImpl.startOffsets[tokenIndex]);
        assertEquals(endOffset, listenerImpl.endOffsets[tokenIndex]);
        assertEquals(tokenType, listenerImpl.tokenTypes[tokenIndex]);
    }
}
