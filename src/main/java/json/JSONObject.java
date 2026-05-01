package json;

import java.util.Map;
import java.util.HashMap;

import java.util.Objects;

public class JSONObject {

    private final JSONTokener tokener;
    private final Map<String, Object> map;

    public JSONObject(JSONTokener tokener) throws JSONException {
        this.tokener = Objects.requireNonNull(tokener);
        this.map = new HashMap<>();
        parse();
    }

    public Map<String, Object> getMap() {
        return Map.copyOf(map);
    }

    private static final class Null {
        @Override
        public boolean equals(Object obj) {
            return obj == null || obj == this;
        }
        @Override
        public int hashCode() {
            return 0;
        }
        @Override
        public String toString() {
            return "null";
        }
    }

    public static final Object NULL = new Null(); 

    private void parse() throws JSONException {
        Token t = tokener.peek();
        if (t != Token.CURLY_OPEN) 
            throw new JSONException("Object should start with {");
        tokener.consume();
        while ((t = tokener.peek()) != Token.CURLY_CLOSE) {
            String key = null;
            if (t.getTokenType() != Token.TokenType.STRING) 
                throw new JSONException("Invalid key for object  " + t);
            key = (String) t.getValue();
            tokener.consume(); // eat key
            t = tokener.peek();
            if (t != Token.COLON) 
                throw new JSONException("Colon expected, but get " + t);
            tokener.consume(); // eat colon
            t = tokener.peek();
            switch(t.getTokenType()) {
                case Token.TokenType.NULL -> {
                   map.put(key, JSONObject.NULL); 
                   tokener.consume();
                }
                case Token.TokenType.STRING, Token.TokenType.NUMBER, 
                     Token.TokenType.TRUE, Token.TokenType.FALSE -> {
                    map.put(key, t.getValue());
                    tokener.consume();
                }
                case Token.TokenType.SQUARE_OPEN -> {
                    map.put(key, new JSONArray(tokener));
                }
                case Token.TokenType.CURLY_OPEN -> {
                    map.put(key, new JSONObject(tokener));
                }
                case Token.TokenType.COMMA -> { 
                    tokener.consume(); // probably we can have some issues...
                }
                case Token.TokenType.EOF -> throw new JSONException("Unexpected end of the object");
                default -> throw new JSONException("Unexpected token in the object " + t);
            }
            if (tokener.peek() == Token.COMMA) {
                tokener.consume();
            }
        }
        if (t != Token.CURLY_CLOSE) 
            throw new JSONException("Object should be ended with }");
        tokener.consume();
    }

}
