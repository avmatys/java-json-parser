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

public class LexerTest {

    private List<Token> tokenize(String input) throws IOException {
        return Lexer.of(new StringReader(input)).tokenize();
    }

    @Test
    @DisplayName("Check simple tokens")
    void checkStructural() throws IOException {
        List<Token> tokens = tokenize("{ } [ ] : , ");
        assertEquals(Token.CURLY_OPEN, tokens.get(0));
        assertEquals(Token.CURLY_CLOSE, tokens.get(1));
        assertEquals(Token.SQUARE_OPEN, tokens.get(2));
        assertEquals(Token.SQUARE_CLOSE, tokens.get(3));
        assertEquals(Token.COLON, tokens.get(4));
        assertEquals(Token.COMMA, tokens.get(5));
        assertEquals(Token.EOF, tokens.get(6));
    }

    @Test
    @DisplayName("Check booleans")
    void checkBoolean() throws IOException {
        List<Token> tokens = tokenize("true false");
        assertEquals(3, tokens.size());
        assertEquals(Token.TRUE, tokens.get(0));
        assertEquals(Token.FALSE, tokens.get(1));
        assertEquals(Token.EOF, tokens.get(2));
    }

    @Test
    @DisplayName("Check null")
    void checkNull() throws IOException {
        List<Token> tokens = tokenize("null");
        assertEquals(2, tokens.size());
        assertEquals(Token.NULL, tokens.get(0));
        assertEquals(Token.EOF, tokens.get(1));
    }

    @Test 
    @DisplayName("Check simple string without \\ inside")
    void checkString1() throws IOException {
        List<Token> tokens = tokenize("\"here is a simple string\" , \"and one more\"");
        assertEquals(4, tokens.size());
        assertEquals(Token.of(Token.TokenType.STRING, "here is a simple string"), tokens.get(0));
        assertEquals(Token.COMMA, tokens.get(1));
        assertEquals(Token.of(Token.TokenType.STRING, "and one more"), tokens.get(2));
        assertEquals(Token.EOF, tokens.get(3));
    }

    @Test
    @DisplayName("Check string with \\ inside")
    void checkString2() throws IOException {
        List<Token> tokens = tokenize("\"\\\"\\\\here is a simple string\\\"\" , \"and one more\"");
        assertEquals(4, tokens.size());
        assertEquals(Token.of(Token.TokenType.STRING, "\"\\here is a simple string\""), tokens.get(0));
        assertEquals(Token.COMMA, tokens.get(1));
        assertEquals(Token.of(Token.TokenType.STRING, "and one more"), tokens.get(2));
        assertEquals(Token.EOF, tokens.get(3));
    }

    @Test
    @DisplayName("Check positive integer numbers")
    void checkNumber1() throws IOException {
        List<Token> tokens = tokenize("1 100 0 98764");
        assertEquals(5, tokens.size());
        assertEquals(Token.of(Token.TokenType.NUMBER, 1.0), tokens.get(0));
        assertEquals(Token.of(Token.TokenType.NUMBER, 100.0), tokens.get(1));
        assertEquals(Token.of(Token.TokenType.NUMBER, 0.0), tokens.get(2));
        assertEquals(Token.of(Token.TokenType.NUMBER, 98764.0), tokens.get(3));
        assertEquals(Token.EOF, tokens.get(4));
    }

    @Test
    @DisplayName("Check negative integer numbers")
    void checkNumber2() throws IOException {
        List<Token> tokens = tokenize("-1 -100 -98764");
        assertEquals(4, tokens.size());
        assertEquals(Token.of(Token.TokenType.NUMBER, -1.0), tokens.get(0));
        assertEquals(Token.of(Token.TokenType.NUMBER, -100.0), tokens.get(1));
        assertEquals(Token.of(Token.TokenType.NUMBER, -98764.0), tokens.get(2));
        assertEquals(Token.EOF, tokens.get(3));
    }

    @Test
    @DisplayName("Check double numbers")
    void checkNumber3() throws IOException {
        List<Token> tokens = tokenize("1.0 1.9 0.0 -900.0");
        assertEquals(5, tokens.size());
        assertEquals(Token.of(Token.TokenType.NUMBER, 1.0), tokens.get(0));
        assertEquals(Token.of(Token.TokenType.NUMBER, 1.9), tokens.get(1));
        assertEquals(Token.of(Token.TokenType.NUMBER, 0.0), tokens.get(2));
        assertEquals(Token.of(Token.TokenType.NUMBER, -900.0), tokens.get(3));
        assertEquals(Token.EOF, tokens.get(4));
    }

    @Test
    @DisplayName("Check simple json")
    void checkJSON1() throws IOException {
        List<Token> tokens = tokenize("{ \"key\" : 10.45, \"key2\" : null, \"key3\" : \"value3\", \"key4\" : false }");
        assertEquals(Token.CURLY_OPEN, tokens.get(0));
        assertEquals(Token.of(Token.TokenType.STRING, "key"), tokens.get(1));
        assertEquals(Token.COLON, tokens.get(2));
        assertEquals(Token.of(Token.TokenType.NUMBER, 10.45), tokens.get(3));
        assertEquals(Token.COMMA, tokens.get(4));
        assertEquals(Token.of(Token.TokenType.STRING, "key2"), tokens.get(5));
        assertEquals(Token.COLON, tokens.get(6));
        assertEquals(Token.NULL, tokens.get(7));
        assertEquals(Token.COMMA, tokens.get(8));
        assertEquals(Token.of(Token.TokenType.STRING, "key3"), tokens.get(9));
        assertEquals(Token.COLON, tokens.get(10));
        assertEquals(Token.of(Token.TokenType.STRING, "value3"), tokens.get(11));
        assertEquals(Token.COMMA, tokens.get(12));
        assertEquals(Token.of(Token.TokenType.STRING, "key4"), tokens.get(13));
        assertEquals(Token.COLON, tokens.get(14));
        assertEquals(Token.FALSE, tokens.get(15));
        assertEquals(Token.CURLY_CLOSE, tokens.get(16));
        assertEquals(Token.EOF, tokens.get(17));
    }

    @Test
    @DisplayName("Check complex json")
    void checkJSON2() throws IOException {
        List<Token> tokens = tokenize("{\"key\" : [10,\"string\",false], \"key2\" : { \"key3\" : null } }");
        assertEquals(Token.CURLY_OPEN, tokens.get(0));
        assertEquals(Token.of(Token.TokenType.STRING, "key"), tokens.get(1));
        assertEquals(Token.COLON, tokens.get(2));
        assertEquals(Token.SQUARE_OPEN, tokens.get(3));
        assertEquals(Token.of(Token.TokenType.NUMBER, 10.0), tokens.get(4));
        assertEquals(Token.COMMA, tokens.get(5));
        assertEquals(Token.of(Token.TokenType.STRING, "string"), tokens.get(6));
        assertEquals(Token.COMMA, tokens.get(7));
        assertEquals(Token.FALSE, tokens.get(8));
        assertEquals(Token.SQUARE_CLOSE, tokens.get(9));
        assertEquals(Token.COMMA, tokens.get(10));
        assertEquals(Token.of(Token.TokenType.STRING, "key2"), tokens.get(11));
        assertEquals(Token.COLON, tokens.get(12));
        assertEquals(Token.CURLY_OPEN, tokens.get(13));
        assertEquals(Token.of(Token.TokenType.STRING, "key3"), tokens.get(14));
        assertEquals(Token.COLON, tokens.get(15));
        assertEquals(Token.NULL, tokens.get(16));
        assertEquals(Token.CURLY_CLOSE, tokens.get(17));
        assertEquals(Token.CURLY_CLOSE, tokens.get(18));
    }

    @Test
    @DisplayName("Error: not finished string")
    void testUnterminatedString() {
        assertThrows(JSONException.class, () -> tokenize("\"not finished"));
    }

    @Test
    @DisplayName("Error: leading zeroes")
    void testLeadingZeroError() {
        assertThrows(JSONException.class, () -> tokenize("05"));
    }

    @Test
    @DisplayName("Error: not allowed symbold")
    void testUnknownSymbol() {
        assertThrows(JSONException.class, () -> tokenize("@"));
    }
}

