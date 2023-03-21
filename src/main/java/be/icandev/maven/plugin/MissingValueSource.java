package be.icandev.maven.plugin;

import org.codehaus.plexus.interpolation.AbstractValueSource;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class MissingValueSource extends AbstractValueSource {
    private final String defaultPattern;
    private final Set<String> missingKeys = new TreeSet<>();

    public MissingValueSource(String defaultPattern) {
        super(true);
        this.defaultPattern = Objects.toString(defaultPattern, "");
    }

    public Object getValue(String key) {
        if (missingKeys.add(key)) {
            this.addFeedback("Missing value: " + key);
        }

        return defaultPattern.replace("$key", key);
    }

    public Set<String> getMissingKeys() {
        return Collections.unmodifiableSet(missingKeys);
    }
}
