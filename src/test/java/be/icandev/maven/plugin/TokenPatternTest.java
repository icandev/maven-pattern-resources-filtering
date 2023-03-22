package be.icandev.maven.plugin;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenPatternTest {
    @Test
    void nullDelimiter() {
        TokenPattern token = new TokenPattern(null);
        assertThat(token.getPattern()).isEqualTo(TokenPattern.KEY_TOKEN);
        assertThat(token.getStartExp()).isEmpty();
        assertThat(token.getEndExp()).isEmpty();
        assertThat(token.resolve("foo")).isEqualTo("foo");
    }

    @Test
    void emptyDelimiter() {
        TokenPattern token = new TokenPattern("");
        assertThat(token.getPattern()).isEqualTo(TokenPattern.KEY_TOKEN);
        assertThat(token.getStartExp()).isEmpty();
        assertThat(token.getEndExp()).isEmpty();
        assertThat(token.resolve("foo")).isEqualTo("foo");
    }

    @Test
    void simpleDelimiter() {
        TokenPattern token = new TokenPattern("@");
        assertThat(token.getStartExp()).isEqualTo("@");
        assertThat(token.getEndExp()).isEqualTo("@");
        assertThat(token.resolve("foo")).isEqualTo("@foo@");
    }

    @Test
    void startEndDelimiter() {
        TokenPattern token = new TokenPattern("${*}");
        assertThat(token.getStartExp()).isEqualTo("${");
        assertThat(token.getEndExp()).isEqualTo("}");
        assertThat(token.resolve("foo")).isEqualTo("${foo}");
    }
}
