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
                case '!', '#', '$', '%', '&', '(', ')', '*', '+', '-', '.', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_' : {
                    if(tokenType != TokenTypes.NO_TYPE_YET) {
                       listener.token(buffer, tokenStartOffset, tempOffsetMark, tokenType);
                       tokenStartOffset = tempOffsetMark;
                    }
                    //tokenType = TokenTypes.SINGLE_CHARACTER_TOKEN ;
                    tokenType = nextCodePoint ;

                    break;
                }
                case '"' : {
                    if( tokenType != TokenTypes.NO_TYPE_YET ) {
                        listener.token(buffer, tokenStartOffset, tempOffsetMark, tokenType);
                        tokenType = TokenTypes.NO_TYPE_YET;
                        tokenStartOffset = tempOffsetMark;
                    }
                    tokenType = TokenTypes.QUOTED_TOKEN;
                    parseQuotedToken(buffer);
                    break;
                }
                case '/' : {

                    if( tokenType != TokenTypes.NO_TYPE_YET) {
                        listener.token(buffer, tokenStartOffset, tempOffsetMark, tokenType);
                        tokenType = TokenTypes.NO_TYPE_YET;
                        tokenStartOffset = tempOffsetMark;
                    }

                    int tempOffsetMark2 = buffer.tempOffset;
                    int nextNextCodePoint = buffer.nextCodepoint();
                    if(nextNextCodePoint == '*') {
                        tokenType = TokenTypes.COMMENT;
                        parseComment(buffer);
                    } else {
                        // The / char was a single char token - not a comment begin.
                        tokenType = nextCodePoint;
                        listener.token(buffer, tokenStartOffset, tokenStartOffset+1, tokenType);

                        buffer.tempOffset = tempOffsetMark2;
                        tokenStartOffset  = tempOffsetMark2;
                        tokenType = TokenTypes.NO_TYPE_YET;
                    }
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

    private void parseComment(Utf8Buffer buffer) {
        boolean endOfCommentTokenFound = false;
        while(buffer.hasMoreBytes() && !endOfCommentTokenFound) {
            int nextCodePoint = buffer.nextCodepoint();

            if(nextCodePoint == '*') {
                if(!buffer.hasMoreBytes()) {
                    throw new RuntimeException("End of comment not found before end of input found");
                }
                int tempOffsetMark2 = buffer.tempOffset;
                int nextNextCodePoint = buffer.nextCodepoint();
                if(nextNextCodePoint == '/'){
                    endOfCommentTokenFound = true;
                } else {
                    // next char was not / (not end of comment), so move one char back and parse normally from there
                    buffer.tempOffset = tempOffsetMark2;
                }
            }
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
