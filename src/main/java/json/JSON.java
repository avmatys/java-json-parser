package json;

import java.io.IOException;

public class JSON {

    public static JSONObject parseEager(String input) throws JSONException {
        try {
            Lexer lexer = Lexer.of(input);
            JSONTokener tokener = JSONListTokener.of(lexer);
            return new JSONObject(tokener);
        } catch (IOException e) {
            // Shouldn't happen
            throw new RuntimeException("Unexpected exception " + e.getMessage());
        }
    }

    public static JSONObject parseStreaming(String input) {
        throw new UnsupportedOperationException("Streaming parsing is not implemented yet!");
    }

}
