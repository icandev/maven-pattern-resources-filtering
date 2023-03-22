package be.icandev.maven.plugin;

import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.*;
import org.apache.maven.shared.utils.logging.MessageUtils;
import org.codehaus.plexus.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.plexus.build.incremental.BuildContext;

import javax.inject.Inject;
import java.util.*;

@Component(role = MavenResourcesFiltering.class, hint = "default")
public class PatternMavenResourcesFiltering extends DefaultMavenResourcesFiltering {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatternMavenResourcesFiltering.class);
    private static final String KEY_FILTERING_DEFAULT = "resource.filtering.default";
    private static final String DEFAULT_PATTERN = "${$key}";

    private final MavenFileFilter mavenFileFilter;

    @Inject
    public PatternMavenResourcesFiltering(MavenFileFilter mavenFileFilter, BuildContext buildContext) {
        super(mavenFileFilter, buildContext);
        this.mavenFileFilter = mavenFileFilter;
    }

    @Override
    public void filterResources(MavenResourcesExecution mavenResourcesExecution) throws MavenFilteringException {
        if (mavenResourcesExecution.isUseDefaultFilterWrappers()) {
            initDefaultFilterWrappers(mavenResourcesExecution);
            mavenResourcesExecution.setUseDefaultFilterWrappers(false);
        }

        String defaultPattern = resolveDefaultPattern(mavenResourcesExecution);
        LinkedHashSet<String> delimiters = mavenResourcesExecution.getDelimiters();
        MissingValueSource missingValueSource = new MissingValueSource(defaultPattern, delimiters);

        for (String delimiter : delimiters) {
            TokenPattern token = new TokenPattern(delimiter);
            mavenResourcesExecution.addFilerWrapperWithEscaping(
                missingValueSource, token.getStartExp(), token.getEndExp(), "\\", false
            );
        }

        super.filterResources(mavenResourcesExecution);
        outputFeedback(missingValueSource);
    }

    private void initDefaultFilterWrappers(MavenResourcesExecution mavenResourcesExecution) throws MavenFilteringException {
        List<FilterWrapper> filterWrappers = new ArrayList<>();
        if (mavenResourcesExecution.getFilterWrappers() != null) {
            filterWrappers.addAll(mavenResourcesExecution.getFilterWrappers());
        }

        filterWrappers.addAll(mavenFileFilter.getDefaultFilterWrappers(mavenResourcesExecution));
        mavenResourcesExecution.setFilterWrappers(filterWrappers);
    }

    private String resolveDefaultPattern(MavenResourcesExecution mavenResourcesExecution) {
        String defaultPattern = DEFAULT_PATTERN;
        MavenProject mavenProject = mavenResourcesExecution.getMavenProject();
        Properties properties = Optional.ofNullable(mavenProject)
            .map(MavenProject::getProperties)
            .orElse(new Properties());

        if (properties.containsKey(KEY_FILTERING_DEFAULT)) {
            defaultPattern = (String) properties.get(KEY_FILTERING_DEFAULT);
        }

        return defaultPattern;
    }

    private void outputFeedback(MissingValueSource missingValueSource) {
        if (LOGGER.isInfoEnabled()) {
            for (String key : missingValueSource.getMissingKeys()) {
                LOGGER.info(MessageUtils.buffer().a("Missing filter value: ").warning(key).toString());
            }
        }
    }
}
