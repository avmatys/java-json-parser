package json;

import java.io.StringReader;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JSONObjectTest {

    private JSONTokener createTokener(String input) throws IOException {
        Lexer lexer = Lexer.of(input);
        return JSONListTokener.of(lexer);
    }

    @Test
    @DisplayName("Check simple object")
    void checkSimpleObject1() throws IOException {
        JSONTokener tokener = createTokener("{ \"key\" : 4, \"key2\" : null }"); 
        Map<String, Object> parsed = new JSONObject(tokener).getMap(); 
        assertEquals((Double) 4.0, parsed.get("key"));
        assertEquals(JSONObject.NULL, parsed.get("key2"));
    }
}
