package com.jenkov.parsers.tokenizers;

import com.jenkov.parsers.unicode.Utf8Buffer;

public class TokenizerListenerIndexImpl implements TokenizerListener {

    public int[] startOffsets = null;
    public int[] endOffsets   = null;

    public byte[] tokenTypes  = null;

    public int nextIndex = 0;

    public TokenizerListenerIndexImpl(int capacity) {
        this.startOffsets = new int[capacity];
        this.endOffsets   = new int[capacity];
        this.tokenTypes   = new byte[capacity];
    }


    @Override
    public void token(Utf8Buffer buffer, int fromOffset, int toOffset, int tokenType) {
        this.startOffsets[this.nextIndex] = fromOffset;
        this.endOffsets  [this.nextIndex] = toOffset;
        this.tokenTypes  [this.nextIndex] = (byte) tokenType;
        this.nextIndex++;
    }
}
