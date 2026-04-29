package json;

import java.util.Objects;

public class Token {
    
    public static enum TokenType {
    
        NULL("null"), 
        NUMBER("number"),
        STRING("string"),
        CURLY_OPEN("{"),
        CURLY_CLOSE("}"),
        SQUARE_OPEN("["),
        SQUARE_CLOSE("]"),
        COMMA(","),
        COLON(":"),
        TRUE("true"),
        FALSE("false"),
        EOF("EOF");

        private String s;

        TokenType(String s) {
            this.s = s;
        }
        
        @Override 
        public String toString() {
            return s;
        }

    }

    public final static Token TRUE = new Token(TokenType.TRUE, Boolean.TRUE);
    public final static Token FALSE = new Token(TokenType.FALSE, Boolean.FALSE);
    public final static Token NULL = new Token(TokenType.NULL, null);
    public final static Token COMMA = new Token(TokenType.COMMA, ",");
    public final static Token COLON = new Token(TokenType.COLON, ":");
    public final static Token CURLY_OPEN = new Token(TokenType.CURLY_OPEN, "{");
    public final static Token CURLY_CLOSE = new Token(TokenType.CURLY_CLOSE, "}");
    public final static Token SQUARE_OPEN = new Token(TokenType.SQUARE_OPEN, "[");
    public final static Token SQUARE_CLOSE = new Token(TokenType.SQUARE_CLOSE, "]");
    public final static Token EOF = new Token(TokenType.EOF, "0");

    private TokenType type;
    private Object value;

    private Token(TokenType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public static Token from(TokenType type) {
        type = Objects.requireNonNull(type);
        switch(type) {
            case NULL -> { return Token.NULL; }
            case CURLY_OPEN -> { return Token.CURLY_OPEN; } 
            case CURLY_CLOSE -> { return Token.CURLY_CLOSE; }
            case SQUARE_OPEN -> { return Token.SQUARE_OPEN; }
            case SQUARE_CLOSE -> { return Token.SQUARE_CLOSE; }
            case COMMA -> { return Token.COMMA; }
            case TRUE -> { return Token.TRUE; }
            case FALSE -> { return Token.FALSE; }
            case EOF -> { return Token.EOF; }
            default -> { throw new IllegalArgumentException("Token type " + type + " is unknow or should be created with value"); }
        }
    }

    public static Token of(TokenType type, Object value) {
        type = Objects.requireNonNull(type);
        value = Objects.requireNonNull(value);
        if (type != TokenType.NUMBER && type != TokenType.STRING) {
            throw new IllegalArgumentException("Token type " + type + " can't be created with a value");
        }
        return new Token(type, value);
    }

    @Override
    public String toString() {
        return "Type: " + type + " Value: " + value;
    }

    @Override 
    public boolean equals(Object obj) {
        if (obj instanceof Token){
            Token token = (Token) obj;
            return type == token.type && Objects.equals(value, token.value);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}
