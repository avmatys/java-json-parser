package json;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.PushbackReader;

import java.util.List;
import java.util.ArrayList;

import java.util.Objects;
import java.io.IOException;

public class Lexer {
    
    private final PushbackReader reader;
    private final List<Token> tokens;
    
    private Lexer(PushbackReader reader) {
        this.reader = reader;
        this.tokens = new ArrayList<>();
    }

    public static Lexer of(Reader reader) {
        reader = Objects.requireNonNull(reader);
        return new Lexer(new PushbackReader(new BufferedReader(reader)));
    }

    public List<Token> tokenize() throws IOException {
        while (true) {
            int val = next();
            if (val == -1) break;
            char c = (char) val;
            if (c == ' ' || c == '\r' || c == '\n' || c == '\t') 
                continue;
            Token token = null;
            switch(c) {
                case '{' -> token = Token.CURLY_OPEN;
                case '}' -> token = Token.CURLY_CLOSE;
                case '[' -> token = Token.SQUARE_OPEN;
                case ']' -> token = Token.SQUARE_CLOSE;
                case ':' -> token = Token.COLON;
                case ',' -> token = Token.COMMA;
                case '"' -> token = tokenString();
                case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                    back(c); // return back to build a number value
                    token = tokenNumber(); 
                }
                case 'f' -> token = tokenFalse();
                case 't' -> token = tokenTrue();
                case 'n' -> token = tokenNull();
                default -> throw new JSONException("Unknown symbol " + c + " in the JSON");
            }
            tokens.add(token);
        }
        tokens.add(Token.EOF);
        return tokens;
    }

    private Token tokenString() throws IOException {
        StringBuilder sb = new StringBuilder();
        while(true) {
            int val = next();
            if (val == -1) {
                throw new JSONException("Unexpected end of the string token");
            }
            char c = (char) val;
            if (c == '"') break;
            // Specific logic to process the quoted literals
            if (val == '\\') {
                val = next();
                if (val == -1) {
                    throw new JSONException("Unterminated escaped character");
                }
                c = (char) val;
                switch(c) {
                    case '\\'-> sb.append('\\');
                    case '"' -> sb.append('"');
                    case 'n' -> sb.append('\n');
                    case 't' -> sb.append('\t');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'r' -> sb.append('\r');
                    case '/' -> sb.append('/');
                    default -> throw new JSONException("Invalid escape " + c);
                }
            } else {
                sb.append((char) val);
            }
        }
        return Token.of(Token.TokenType.STRING, sb.toString());
    }

    private Token tokenNumber() throws IOException {
        char c = (char) next();
        int sign = c == '-' ? -1 : 1;
        if (c != '-') back(c);
        Double number = extractNumber() * sign; 
        return Token.of(Token.TokenType.NUMBER, number);
    }

    private Double extractNumber() throws IOException {
        int val = next();
        if (val == -1) {
            throw new JSONException("Unexpected end of the number token");
        }
        char c = (char) val;
        if (!Character.isDigit(c)) {
            throw new JSONException("Number should start with digit");
        }
        boolean leadZero = c == '0';
        boolean hasDot = false;
        boolean hasExp = false;
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        char prev = c;
        while (true) {
            val = next();
            if (val == -1) {
                break;
                //throw new JSONException("Unexpected end of the number token");
            }
            c = (char) val;
            if (!Character.isDigit(c) && c != 'e' && c != 'E' && c != '-' && c != '.') {
                back(c);
                break;
            }
            if (Character.isDigit(c)) {
                if (leadZero && !hasDot) {
                    throw new JSONException("Number can't have a leading zero");
                }
                sb.append(c);
            } else if (c == '.') {
                if (hasDot) {
                    throw new JSONException("Number can't have multiple dots");
                }
                if (!Character.isDigit(prev)) {
                    throw new JSONException("Dot should be after the digit");
                }
                sb.append(c);
                hasDot = true;
            } else if (c == 'e' || c == 'E') {
                if (hasExp) {
                    throw new JSONException("Number can't have multiple e/E");
                }
                if (!Character.isDigit(prev)) {
                    throw new JSONException("Exp should be after the digit");
                }
                sb.append(c);
                hasExp = true;
            } else if (c == '-') {
                if (prev != 'e' && prev != 'E') {
                    throw new JSONException("Minus is allowed in the beginning or after e/E");
                }
                sb.append(c);
            }
            prev = c;
        }
        if (!Character.isDigit(prev)) {
            throw new JSONException("Number token must end with a digit");
        }
        return Double.parseDouble(sb.toString());
    }

    private Token tokenFalse() throws IOException {
        char[] seq = new char[] { 'a', 'l', 's', 'e' };
        for (char c: seq) {
            int val = next();
            if (val == -1) {
                throw new JSONException("Unexpected end of the false token");
            }
            if ((char) val != c) {
                throw new JSONException("Unexpected char " + (char) val + " in false token");
            }
        }
        return Token.FALSE;
    }
    
    private Token tokenTrue() throws IOException {
        char[] seq = new char[] { 'r', 'u', 'e' };
        for (char c: seq) {
            int val = next();
            if (val == -1) {
                throw new JSONException("Unexpected end of the true token");
            }
            if ((char) val != c) {
                throw new JSONException("Unexpected char " + (char) val + " in true token");
            }
        }
        return Token.TRUE;
    } 

    private Token tokenNull() throws IOException {
        char[] seq = new char[] { 'u', 'l', 'l' };
        for (char c: seq) {
            int val = next();
            if (val == -1) {
                throw new JSONException("Unexpected end of the null token");
            }
            if ((char) val != c) {
                throw new JSONException("Unexpected char " + (char) val + " in null token");
            }
        }
        return Token.NULL;
    }

    private int next() throws IOException {
        return reader.read(); 
    }

    private void back(int c) throws IOException {
        reader.unread(c);
    }

}
