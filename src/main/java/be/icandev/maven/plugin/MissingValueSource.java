package be.icandev.maven.plugin;

import org.codehaus.plexus.interpolation.AbstractValueSource;

import java.util.*;

public class MissingValueSource extends AbstractValueSource {
    private final String defaultPattern;
    private final Set<TokenPattern> tokenPatterns;
    private final Set<String> missingKeys = new TreeSet<>();

    public MissingValueSource(String defaultPattern, Set<String> delimiters) {
        super(true);
        this.defaultPattern = Objects.toString(defaultPattern, "");
        this.tokenPatterns = resolveTokenPatterns(delimiters);
    }

    private Set<TokenPattern> resolveTokenPatterns(Set<String> delimiters) {
        Set<TokenPattern> patterns = new LinkedHashSet<>();
        if (delimiters == null) {
            return patterns;
        }

        for (String delimiter : delimiters) {
            patterns.add(new TokenPattern(delimiter));
        }

        return patterns;
    }

    public Object getValue(String key) {
        if (missingKeys.add(key)) {
            this.addFeedback("Missing value: " + key);
        }

        String defaultValue = defaultPattern.replace(TokenPattern.KEY_TOKEN, key);
        for (TokenPattern tokenPattern : tokenPatterns) {
            if (tokenPattern.resolve(key).equals(defaultValue)) {
                return null;
            }
        }

        return defaultValue;
    }

    public Set<String> getMissingKeys() {
        return Collections.unmodifiableSet(missingKeys);
    }
}
