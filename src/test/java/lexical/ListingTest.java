package lexical;

import org.junit.jupiter.api.Test;
import rome77.antlr.Rome77Lexer;

import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for Listing behavior.
 *
 * Each test verifies one specific listing property.
 */
final class ListingTest {

    @Test
    void tokensReturnsOneTokenForEmptySource() throws Exception {
        assertThat(
            "Empty source should produce listing with only EOF",
            new Rome77Lexer("").tokenized().size(),
            is(equalTo(1))
        );
    }

    @Test
    void tokensReturnsTwoTokensForSingleRomanNumeral() throws Exception {
        assertThat(
            "Single numeral should produce one token plus EOF",
            new Rome77Lexer("XIV").tokenized().size(),
            is(equalTo(2))
        );
    }

    @Test
    void tokensReturnsFiveTokensForVariableDeclaration() throws Exception {
        assertThat(
            "Variable declaration should produce four tokens plus EOF",
            new Rome77Lexer("As x = V").tokenized().size(),
            is(equalTo(5))
        );
    }

    @Test
    void tokensIterateInSourceOrder() throws Exception {
        final Iterator<Token> tokens = new Rome77Lexer("As x = V")
            .tokenized().tokens().iterator();
        assertThat(
            "First token should be AS",
            tokens.next().category(),
            is(equalTo(TokenCategory.AS))
        );
        assertThat(
            "Second token should be IDENTIFIER",
            tokens.next().category(),
            is(equalTo(TokenCategory.IDENTIFIER))
        );
        assertThat(
            "Third token should be EQUALS",
            tokens.next().category(),
            is(equalTo(TokenCategory.EQUALS))
        );
    }

    @Test
    void sizeReturnsOneForEmptySource() throws Exception {
        assertThat(
            "Empty source listing should have size 1 (EOF only)",
            new Rome77Lexer("").tokenized().size(),
            is(equalTo(1))
        );
    }

    @Test
    void sizeReturnsFiveForVariableDeclaration() throws Exception {
        assertThat(
            "Variable declaration listing should have size 5",
            new Rome77Lexer("As x = V").tokenized().size(),
            is(equalTo(5))
        );
    }

    @Test
    void tokensCanBeIteratedMultipleTimes() throws Exception {
        final Listing listing = new Rome77Lexer("XIV").tokenized();
        int first = 0;
        for (final Token ignored : listing.tokens()) {
            first = first + 1;
        }
        int second = 0;
        for (final Token ignored : listing.tokens()) {
            second = second + 1;
        }
        assertThat(
            "Both iterations should count same number of tokens",
            first,
            is(equalTo(second))
        );
    }

    @Test
    void tokenizedThrowsExceptionOnInvalidCharacter() {
        assertThrows(
            LexicalException.class,
            () -> new Rome77Lexer("$invalid").tokenized()
        );
    }
}
