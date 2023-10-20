package com.jenkov.parsers.tokenizers;

import com.jenkov.parsers.unicode.Utf8Buffer;

/**
 * This a basic tokenizer - capable of breaking a sequence of UTF-8 encoded characters into tokens of the following types:
 *
 * - white space ( space, tab, line break, carriage return )
 * - single character tokens
 * - alphabetic
 * - numeric
 * - alphanumeric
 * - quoted tokens
 *
 */
public class BasicTokenizer {

    public void tokenize(Utf8Buffer buffer, TokenizerListener listener) {

        int tokenStartOffset = buffer.tempOffset;
        int tempOffsetMark   = buffer.tempOffset;  //todo necessary up here?
        int tokenType        = TokenTypes.NO_TYPE_YET;

        while(buffer.hasMoreBytes()) {

                tempOffsetMark = buffer.tempOffset;
            int nextCodePoint  = buffer.nextCodepoint();

            switch(nextCodePoint) {
                // check if quoted token. If it is - call parseQuotedToken();
                case ' ', '\t', '\n', '\r' : {
                    if(tokenType > TokenTypes.NO_TYPE_YET) {
                        listener.token(buffer, tokenStartOffset, tempOffsetMark, tokenType);
                        tokenStartOffset = tempOffsetMark;
                    }
                    tokenType = TokenTypes.WHITE_SPACE;

                    break;
                }

                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' : {
                    if(tokenType < TokenTypes.NUMERIC || tokenType > TokenTypes.ALPHA_NUMERIC) {
                        listener.token(buffer, tokenStartOffset, tempOffsetMark, tokenType);
                        tokenStartOffset = tempOffsetMark;
                        tokenType =  TokenTypes.NO_TYPE_YET;
                    }
                    tokenType |= TokenTypes.NUMERIC;
                    break;
                }
                case 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                     'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' : {
                    if(tokenType < TokenTypes.NUMERIC || tokenType > TokenTypes.ALPHA_NUMERIC) {
                        listener.token(buffer, tokenStartOffset, tempOffsetMark, tokenType);
                        tokenStartOffset = tempOffsetMark;
                        tokenType =  TokenTypes.NO_TYPE_YET;
                    }
                    tokenType |= TokenTypes.ALPHABETIC ;
                    break;
                }
                case '!', '#', '$', '%', '&', '(', ')', '*', '+', '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_' : {
                    if(tokenType != TokenTypes.NO_TYPE_YET) {
                       listener.token(buffer, tokenStartOffset, tempOffsetMark, tokenType);
                       tokenStartOffset = tempOffsetMark;
                    }
                    //tokenType = TokenTypes.SINGLE_CHARACTER_TOKEN ;
                    tokenType = nextCodePoint ;

                    break;
                }
                case '"' : {

                    //finish current token first?
                    if( tokenType != TokenTypes.NO_TYPE_YET ) {
                        listener.token(buffer, tokenStartOffset, tempOffsetMark, tokenType);
                        tokenType = TokenTypes.NO_TYPE_YET;
                        tokenStartOffset = tempOffsetMark;
                    }
                    tokenType = TokenTypes.QUOTED_TOKEN;
                    parseQuotedToken(buffer);

                    //todo does this even make sense?
                    //listener.token(buffer, tempOffsetMark, buffer.tempOffset, TokenTypes.QUOTED_TOKEN);
                    //tokenStartOffset = buffer.tempOffset;
                    break;
                }
            }
        }
        //check if end of buffer found while searching for end of a token:
        //if(tempOffsetMark - tokenStartOffset > 0) {
        if(buffer.tempOffset - tokenStartOffset > 0) {
            listener.token(buffer, tokenStartOffset, buffer.tempOffset, tokenType);
        }
    }

    private void parseQuotedToken(Utf8Buffer buffer) {
        int tempOffsetMark = buffer.tempOffset;
        int nextCodePoint  = buffer.nextCodepoint();

        while(nextCodePoint != '"') {
            // First without allowed escape characters in the quoted token

            tempOffsetMark = buffer.tempOffset;
            nextCodePoint  = buffer.nextCodepoint();
        }
    }


}
