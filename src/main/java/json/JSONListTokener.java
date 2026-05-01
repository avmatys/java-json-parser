package json;

import java.util.List;
import java.util.Objects;

import java.io.IOException;

public class JSONListTokener implements JSONTokener {

    private List<Token> tokens;
    private int idx;

    private JSONListTokener(List<Token> tokens) {
        this.tokens = tokens;
    }

    public static JSONListTokener of(Lexer lexer) throws IOException {
       lexer = Objects.requireNonNull(lexer);
       return new JSONListTokener(lexer.tokenize());
    }

    @Override
    public boolean hasMore() {
       return idx < tokens.size(); 
    }

    @Override
    public Token peek() {
        if (hasMore()) return tokens.get(idx);
        throw new JSONException("No more tokens");
    }

    @Override
    public Token consume() {
        if (hasMore()) return tokens.get(idx++);
        throw new JSONException("No more tokens");
    }


}
