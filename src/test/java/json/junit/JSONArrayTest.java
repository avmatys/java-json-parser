package json;

import java.io.StringReader;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JSONArrayTest {

    private JSONTokener createTokener(String input) throws IOException {
        Lexer lexer = Lexer.of(input);
        return JSONListTokener.of(lexer);
    }

    @Test
    @DisplayName("Check simple array")
    void checkArray1() throws IOException {
        JSONTokener tokener = createTokener("[1,true,\"value\",null]"); 
        List<Object>  parsed = new JSONArray(tokener).getItems(); 
        assertEquals((Double) 1.0, parsed.get(0));
        assertEquals(Boolean.TRUE, parsed.get(1));
        assertEquals("value", parsed.get(2));
        assertEquals(JSONObject.NULL, parsed.get(3));
    }

}
