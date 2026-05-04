package json;

import java.io.StringReader;
import java.io.IOException;
import java.util.Map;
import java.util.List;

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

    @Test
    @DisplayName("Check object with subobjecets")
    void checkObject2() throws IOException {
        JSONTokener tokener = createTokener("{\"key\" : { \"key2\" : 1, \"key3\" : { \"key4\" : 2 }}, \"key5\" : null, \"key6\" : true, \"key7\" : \"string2\" }");
        Map<String, Object> parsed = new JSONObject(tokener).getMap();
        assertEquals(JSONObject.class, parsed.get("key").getClass());
        Map<String, Object> inner = ((JSONObject) parsed.get("key")).getMap();
        assertEquals(JSONObject.class, inner.get("key3").getClass());
        assertEquals(1.0, inner.get("key2"));
        Map<String, Object> inner2 = ((JSONObject) inner.get("key3")).getMap();
        assertEquals(2.0, inner2.get("key4"));
        assertEquals(JSONObject.NULL, parsed.get("key5"));
        assertEquals(Boolean.TRUE, parsed.get("key6"));
        assertEquals("string2", parsed.get("key7"));
    }

    @Test
    @DisplayName("Check object with array")
    void checkObject3() throws IOException {
        JSONTokener tokener = createTokener("{\"key\" : [ 1, 2, 3, null, \"str\"] }");
        Map<String, Object> parsed = new JSONObject(tokener).getMap();
        assertEquals(JSONArray.class, parsed.get("key").getClass());
        List<Object> items = ((JSONArray) parsed.get("key")).getItems();
        assertEquals(5, items.size());
        assertEquals(1.0, items.get(0));
        assertEquals(2.0, items.get(1));
        assertEquals(3.0, items.get(2));
        assertEquals(JSONObject.NULL, items.get(3));
        assertEquals("str", items.get(4));
    }
}
