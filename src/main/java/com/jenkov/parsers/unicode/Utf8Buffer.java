package com.jenkov.parsers.unicode;


/** The Utf8Source class represents a textual source (input) for the Assembler. When the Assembler assembles a text
        * into a binary output, it requires a Utf8Source as input.
        */
public class Utf8Buffer {

    public byte[] buffer;
    public int offset;
    public int length;
    public int endOffset;

    /** A field that can be used during parsing of the data in this Utf8Source */
    public int tempOffset;

    /** A field that can point to the beginning of a token parsed from UTF-8 characters
     *  See the parseToken() method.
     * */
    public int tokenOffset;


    public Utf8Buffer(byte[] data) {
        this(data, 0, data.length);
    }
    public Utf8Buffer(byte [] data, int offset, int length) {
        this.buffer      = data;
        this.offset      = offset;
        this.tempOffset  = offset;
        this.length      = length;
        this.endOffset   = offset + length;
    }

    public Utf8Buffer clear() {
        this.offset     = 0;
        this.tempOffset = 0;
        this.length     = 0;
        this.endOffset  = 0;
        return this;
    }

    public Utf8Buffer rewind() {
        this.tempOffset = this.offset;
        return this;
    }

    public Utf8Buffer calculateLengthAndEndOffset() {
        this.length = this.tempOffset - this.offset;
        this.endOffset = this.tempOffset;
        return this;
    }

    public boolean hasMoreBytes() {
        //todo optimize this? No reason to subtract 1 every time hasMoreBytes() is called.
        //return this.tempOffset < this.endOffset - 1;
        return this.tempOffset < this.endOffset;
    }

    public int writeCodepoints(String characters) {
        int bytesWritten = 0;
        for( int i = 0; i < characters.length(); i++){
            bytesWritten += writeCodepoint(characters.codePointAt(i));
        }
        return bytesWritten;
    }

    public int writeCodepoint(int codepoint) {
        if(codepoint < 0x00_00_00_80){
            // This is a one byte UTF-8 char
            buffer[this.tempOffset++] = (byte) (0xFF & codepoint);
            return 1;
        } else if (codepoint < 0x00_00_08_00) {
            // This is a two byte UTF-8 char. Value is 11 bits long (less than 12 bits in value).
            // Get highest 5 bits into first byte
            buffer[this.tempOffset]     = (byte) (0xFF & (0b1100_0000 | (0b0001_1111 & (codepoint >> 6))));
            buffer[this.tempOffset + 1] = (byte) (0xFF & (0b1000_0000 | (0b0011_1111 & codepoint)));
            this.tempOffset+=2;
            return 2;
        } else if (codepoint < 0x00_01_00_00){
            // This is a three byte UTF-8 char. Value is 16 bits long (less than 17 bits in value).
            // Get the highest 4 bits into the first byte
            buffer[this.tempOffset]     = (byte) (0xFF & (0b1110_0000 | (0b0000_1111 & (codepoint >> 12))));
            buffer[this.tempOffset + 1] = (byte) (0xFF & (0b1000_0000 | (0b00111111 & (codepoint >> 6))));
            buffer[this.tempOffset + 2] = (byte) (0xFF & (0b1000_0000 | (0b00111111 & codepoint)));
            this.tempOffset+=3;
            return 3;
        } else if (codepoint < 0x00_11_00_00) {
            // This is a four byte UTF-8 char. Value is 21 bits long (less than 22 bits in value).
            // Get the highest 3 bits into the first byte
            buffer[this.tempOffset]     = (byte) (0xFF & (0b1111_0000 | (0b0000_0111 & (codepoint >> 18))));
            buffer[this.tempOffset + 1] = (byte) (0xFF & (0b1000_0000 | (0b0011_1111 & (codepoint >> 12))));
            buffer[this.tempOffset + 2] = (byte) (0xFF & (0b1000_0000 | (0b0011_1111 & (codepoint >> 6))));
            buffer[this.tempOffset + 3] = (byte) (0xFF & (0b1000_0000 | (0b0011_1111 & codepoint)));
            this.tempOffset+=4;
            return 4;
        }
        throw new IllegalArgumentException("Unknown Unicode codepoint: " + codepoint);
    }

    public int nextCodepoint() {
        int firstByteOfChar = 0xFF & buffer[tempOffset];

        if(firstByteOfChar < 0b1000_0000) {    // 128
            //this is a single byte UTF-8 char (an ASCII char)
            tempOffset++;
            return firstByteOfChar;
        } else if(firstByteOfChar < 0b1110_0000) {    // 224
            int nextCodepoint = 0;
            //this is a two byte UTF-8 char
            nextCodepoint = 0b0001_1111 & firstByteOfChar; //0x1F
            nextCodepoint <<= 6;
            nextCodepoint |= 0b0011_1111 & (0xFF & buffer[tempOffset + 1]); //0x3F
            tempOffset +=2;
            return  nextCodepoint;
        } else if(firstByteOfChar < 0b1111_0000) {    // 240
            //this is a three byte UTF-8 char
            int nextCodepoint = 0;
            //this is a two byte UTF-8 char
            nextCodepoint = 0b0000_1111 & firstByteOfChar; // 0x0F
            nextCodepoint <<= 6;
            nextCodepoint |= 0x3F & buffer[tempOffset + 1];
            nextCodepoint <<= 6;
            nextCodepoint |= 0x3F & buffer[tempOffset + 2];
            tempOffset +=3;
            return  nextCodepoint;
        } else if(firstByteOfChar < 0b1111_1000) {    // 248
            //this is a four byte UTF-8 char
            int nextCodepoint = 0;
            //this is a two byte UTF-8 char
            nextCodepoint = 0b0000_0111 & firstByteOfChar; // 0x07
            nextCodepoint <<= 6;
            nextCodepoint |= 0x3F & buffer[tempOffset + 1];
            nextCodepoint <<= 6;
            nextCodepoint |= 0x3F & buffer[tempOffset + 2];
            nextCodepoint <<= 6;
            nextCodepoint |= 0x3F & buffer[tempOffset + 3];
            tempOffset +=4;
            return  nextCodepoint;
        }

        throw new IllegalStateException("Codepoint not recognized from first byte: " + firstByteOfChar);
    }

    /*
    public void skipWhitespace() {
        int tempOffsetMark = this.tempOffset;
        int nextCodepoint  = nextCodepoint();

        while(hasMoreBytes() && isWhiteSpaceCodepoint(nextCodepoint)){
            tempOffsetMark = this.tempOffset;
            nextCodepoint  = nextCodepoint();
        }
        if(hasMoreBytes()) {
            // Latest code point was not a whitespace code point, and end-of-buffer was not found.
            // Push the tempOffset back so that latest code point is returned by next call to nextCodepoint()
            this.tempOffset = tempOffsetMark;
        }
    }

    public boolean isWhiteSpaceCodepoint(int nextCodepoint) {
        return nextCodepoint == ' ' || nextCodepoint == '\t' || nextCodepoint == '\r' || nextCodepoint == '\n';
    }

    public void parseIntegerToken() {
        this.tokenOffset = this.tempOffset;
        int tokenCodepointLength = 0;

        while( hasMoreBytes() ) {
            int previousCodepointOffset = this.tempOffset;
            int nextCodepoint = nextCodepoint();

            switch(nextCodepoint) {
                case '0' : ;
                case '1' : ;
                case '2' : ;
                case '3' : ;
                case '4' : ;
                case '5' : ;
                case '6' : ;
                case '7' : ;
                case '8' : ;
                case '9' : ; tokenCodepointLength++; break;
                default: {
                    if(tokenCodepointLength == 0){
                        // No integer character codepoint found.
                        throw new RuntimeException("Integer token expected, but found: " + (char) nextCodepoint);
                    } else {
                        // 1+ integer character codepoints found. Set tempOffset back to previousCodepointOffset,
                        // so the demarcation character codepoint (this codepoint) is returned as next codepoint.
                        this.tempOffset = previousCodepointOffset;
                        return; // end of integer token found - so return.
                    }
                }
            }
        }
    }

    public void parseDecimalToken() {
        this.tokenOffset = this.tempOffset;
        int tokenCodepointLength = 0;

        //todo Should the number of . be counted, and an exception be thrown if the number is greater than 1?

        while( hasMoreBytes() ) {
            int previousCodepointOffset = this.tempOffset;
            int nextCodepoint = nextCodepoint();

            switch(nextCodepoint) {
                case '0' : ;
                case '1' : ;
                case '2' : ;
                case '3' : ;
                case '4' : ;
                case '5' : ;
                case '6' : ;
                case '7' : ;
                case '8' : ;
                case '9' : ;
                case '.' : ; tokenCodepointLength++; break;
                default: {
                    if(tokenCodepointLength == 0){
                        // No integer character codepoint found.
                        throw new RuntimeException("Decimal token expected, but found: " + (char) nextCodepoint);
                    } else {
                        // 1+ integer character codepoints found. Set tempOffset back to previousCodepointOffset,
                        // so the demarcation character codepoint (this codepoint) is returned as next codepoint.
                        this.tempOffset = previousCodepointOffset;
                        return; // end of integer token found - so return.
                    }
                }
            }
        }
    }

    public void parseToken() {
        this.tokenOffset = this.tempOffset;
        int tokenCodepointLength = 1;

        while( hasMoreBytes()) {
            int previousCodepointOffset = this.tempOffset;
            int nextCodepoint = nextCodepoint();

            if(isWhiteSpaceCodepoint(nextCodepoint)) {
                // Boundary of token found (a whitespace char). Go 1 codepoint back and return here.
                this.tempOffset = previousCodepointOffset;
                return;

            } else if (isSingleCodepointToken(nextCodepoint)){
                if(tokenCodepointLength == 1) {
                    // This is the first codepoint found - return this as a single codepoint token
                    return;
                } else {
                    // This is not the first codepoint found - this single codepoint token functions as a "separator"
                    // from the earlier found codepoints - meaning there are two tokens: A multi-codepoint + a single-codepoint token
                    // Set tempOffset back to before the single-token codepoint, so it will be found at the next parseToken() call,
                    // and return with markers pointing to the multi-codepoint token.
                    this.tempOffset = previousCodepointOffset;
                    return;
                }
            } else {
                // continue to the next codepoint - as end of token is not yet found.
                tokenCodepointLength++;
            }
        }

    }

    public boolean isSingleCodepointToken(int nextCodepoint) {
        switch(nextCodepoint) {
            case '('  :
            case ')'  :
            case '{'  :
            case '}'  :
            case '<'  :
            case '>'  :
            case '['  :
            case ']'  :
            case '|'  :
            case ':'  :
            case ';'  :
            case '.'  :
            case ','  :
            case '"'  :
            case '\'' :
            case '!'  :
            case '#'  :
            case '$'  :
            case '%'  :
            case '&'  :
            case '/'  :
            case '\\' :
            case '*'  :
            case '+'  :
            case '-'  :
            case '_'  :
            case '^'  :
            case '~'  :
            case '@'  : return true;

            default   : return false;
        }
    }
*/

}