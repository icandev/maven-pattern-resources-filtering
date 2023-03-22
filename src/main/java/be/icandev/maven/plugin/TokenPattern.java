package be.icandev.maven.plugin;

import java.util.regex.Pattern;

public class TokenPattern {
    public static final String DELIMITER_SEPARATOR = "*";
    public static final String KEY_TOKEN = "$key";

    private final String startExp;
    private final String endExp;

    public TokenPattern(String delimiter) {
        String[] tokens = splitDelimiter(delimiter);
        this.startExp = tokens[0];
        this.endExp = tokens[1];
    }

    public String resolve(String key) {
        return getPattern().replace(KEY_TOKEN, key);
    }

    public String getStartExp() {
        return startExp;
    }

    public String getEndExp() {
        return endExp;
    }

    public String getPattern() {
        return startExp + KEY_TOKEN + endExp;
    }

    private String[] splitDelimiter(String delimiter) {
        if (delimiter == null) {
            return new String[] {"", ""};
        }

        if (delimiter.contains(DELIMITER_SEPARATOR)) {
            return delimiter.split(Pattern.quote(DELIMITER_SEPARATOR));
        }

        return new String[] {delimiter, delimiter};
    }
}
