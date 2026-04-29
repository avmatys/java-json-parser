package json;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenTest {
    @Test
    void testFrom() {
        assertEquals(Token.TRUE, Token.from(Token.TokenType.TRUE));
    }
    
}
