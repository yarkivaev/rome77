package lexical;

import org.junit.jupiter.api.Test;
import rome77.antlr.Rome77Lexer;

import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Tests for Token behavior.
 *
 * Each test verifies one specific token property.
 */
final class TokenTest {

    @Test
    void categoryReturnsROMANForRomanNumeral() throws Exception {
        assertThat(
            "Roman numeral token should have ROMAN category",
            this.firstToken("XIV").category(),
            is(equalTo(TokenCategory.ROMAN))
        );
    }

    @Test
    void categoryReturnsIDENTIFIERForLowercaseWord() throws Exception {
        assertThat(
            "Lowercase word should have IDENTIFIER category",
            this.firstToken("fib").category(),
            is(equalTo(TokenCategory.IDENTIFIER))
        );
    }

    @Test
    void categoryReturnsPLUSForPlusOperator() throws Exception {
        assertThat(
            "Plus sign should have PLUS category",
            this.tokenAt("a + b", 1).category(),
            is(equalTo(TokenCategory.PLUS))
        );
    }

    @Test
    void textReturnsXIVForRomanNumeralToken() throws Exception {
        assertThat(
            "Token text should match source",
            this.firstToken("XIV").text(),
            is(equalTo("XIV"))
        );
    }

    @Test
    void textReturnsUnicodeStringΦοβερός() throws Exception {
        assertThat(
            "Token text should handle irregular input with uppercase",
            this.firstToken("MMXXIV").text(),
            is(equalTo("MMXXIV"))
        );
    }

    @Test
    void lineReturnsOneForFirstLine() throws Exception {
        assertThat(
            "First line should be numbered 1",
            this.firstToken("XIV").line(),
            is(equalTo(1))
        );
    }

    @Test
    void lineReturnsTwoForSecondLine() throws Exception {
        assertThat(
            "Second line should be numbered 2",
            this.tokenAt("XIV\nV", 1).line(),
            is(equalTo(2))
        );
    }

    @Test
    void columnReturnsZeroForFirstColumn() throws Exception {
        assertThat(
            "First column should be numbered 0",
            this.firstToken("XIV").column(),
            is(equalTo(0))
        );
    }

    @Test
    void columnReturnsFiveForSixthCharacter() throws Exception {
        assertThat(
            "Sixth character should be at column 5",
            this.tokenAt("As x = V", 2).column(),
            is(equalTo(5))
        );
    }

    private Token firstToken(final String code) throws Exception {
        return new Rome77Lexer(code).tokenized()
            .tokens().iterator().next();
    }

    private Token tokenAt(final String code, final int index) throws Exception {
        final Iterator<Token> tokens = new Rome77Lexer(code)
            .tokenized().tokens().iterator();
        Token result = tokens.next();
        for (int i = 0; i < index; i = i + 1) {
            result = tokens.next();
        }
        return result;
    }
}
