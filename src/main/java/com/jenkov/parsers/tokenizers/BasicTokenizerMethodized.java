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
 *
 * This implementation uses a method to parse each class of tokens.
 */
public class BasicTokenizerMethodized {

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
                    parseWhiteSpace(buffer);
                    tokenType = TokenTypes.WHITE_SPACE;
                    listener.token(buffer, tokenStartOffset, buffer.tempOffset, tokenType);
                    tokenStartOffset = buffer.tempOffset;
                    break;
                }

                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' : {
                    tokenType = parseAlphaNumericToken(buffer);
                    listener.token(buffer, tokenStartOffset, buffer.tempOffset, tokenType);
                    tokenStartOffset = buffer.tempOffset;
                    break;
                }
                case 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                     'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' : {
                    tokenType = parseAlphaNumericToken(buffer);
                    listener.token(buffer, tokenStartOffset, buffer.tempOffset, tokenType);
                    tokenStartOffset = buffer.tempOffset;
                    break;
                }
                case '!', '#', '$', '%', '&', '(', ')', '*', '+', '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_' : {
                    tokenType = nextCodePoint ;
                    listener.token(buffer, tokenStartOffset, tokenStartOffset+1, tokenType);
                    tokenStartOffset = buffer.tempOffset;

                    break;
                }
                case '"' : {
                    parseQuotedToken(buffer);
                    tokenType = TokenTypes.QUOTED_TOKEN;
                    listener.token(buffer, tokenStartOffset, buffer.tempOffset, TokenTypes.QUOTED_TOKEN);
                    tokenStartOffset = buffer.tempOffset;
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
        //int tempOffsetMark = buffer.tempOffset;
        int nextCodePoint  = buffer.nextCodepoint();

        while(nextCodePoint != '"') {
            // First without allowed escape characters in the quoted token

            //tempOffsetMark = buffer.tempOffset;
            nextCodePoint  = buffer.nextCodepoint();
        }
    }

    private void parseWhiteSpace(Utf8Buffer buffer) {
        boolean endOfTokenFound = false;

        while(buffer.hasMoreBytes() && !endOfTokenFound) {
            int tempOffsetMark = buffer.tempOffset;
            int nextCodePoint  = buffer.nextCodepoint();

            switch(nextCodePoint) {
                case ' ', '\t', '\n', '\r' : {
                    break;
                }
                default : {
                    endOfTokenFound = true;
                    buffer.tempOffset = tempOffsetMark; // set tempOffset back to before last read code point.
                    break;
                }
            }
        }
    }

    private int parseAlphaNumericToken(Utf8Buffer buffer) {
        int tokenType = TokenTypes.NO_TYPE_YET;

        boolean endOfTokenFound = false;

        while(buffer.hasMoreBytes() && !endOfTokenFound) {
            int tempOffsetMark = buffer.tempOffset;
            int nextCodePoint  = buffer.nextCodepoint();

            switch(nextCodePoint) {
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' : {
                    tokenType |= TokenTypes.NUMERIC;
                    break;
                }
                case 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                     'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' : {
                    tokenType |= TokenTypes.ALPHABETIC ;
                    break;
                }
                default : {
                    endOfTokenFound = true;
                    buffer.tempOffset = tempOffsetMark; // set tempOffset back to before last read code point.
                    break;
                }
            }
        }

        return tokenType;
    }


}
