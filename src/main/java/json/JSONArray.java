package json;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class JSONArray {

    private final JSONTokener tokener;
    private final List<Object> items;

    public JSONArray(JSONTokener tokener) throws JSONException {
        this.tokener = Objects.requireNonNull(tokener);
        this.items = new ArrayList<>();
        parse();
    }

    public List<Object> getItems() {
        return List.copyOf(items);
    }

    private void parse() throws JSONException {
        Token t = tokener.peek();
        if (t != Token.SQUARE_OPEN) 
            throw new JSONException("Array should start with [");
        tokener.consume();
        while ((t = tokener.peek()) != Token.SQUARE_CLOSE) {
            switch(t.getTokenType()) {
                case Token.TokenType.NULL -> {
                    items.add(JSONObject.NULL);
                    tokener.consume(); // eat
                }
                case Token.TokenType.STRING, Token.TokenType.NUMBER, 
                     Token.TokenType.TRUE, Token.TokenType.FALSE -> {
                     items.add(t.getValue());
                     tokener.consume(); // eat
                }
                case Token.TokenType.SQUARE_OPEN -> {
                    items.add(new JSONArray(tokener));
                }
                case Token.TokenType.CURLY_OPEN -> {
                    items.add(new JSONObject(tokener));
                }
                case Token.TokenType.COMMA -> {
                    tokener.consume(); // for strict mode this is not ok, but for us is ok
                }
                case Token.TokenType.EOF -> throw new JSONException("Unexpected end of the array");
                default -> throw new JSONException("Unexpected token in the array " + t);
            }
        }
        if (t != Token.SQUARE_CLOSE) 
            throw new JSONException("Array should be ended with ]");
        tokener.consume();
    }

}
