package com.jenkov.parsers.tokenizers;

import com.jenkov.parsers.unicode.Utf8Buffer;

public interface TokenizerListener {


    // Todo Is Utf8Buffer necessary as parameter?
    // Todo Should tokenType just be a byte? Or a short?
    public void token(Utf8Buffer buffer, int fromOffset, int toOffset, int tokenType);
}
