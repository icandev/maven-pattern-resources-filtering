package be.icandev.maven.plugin;

import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.*;
import org.apache.maven.shared.utils.logging.MessageUtils;
import org.codehaus.plexus.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.plexus.build.incremental.BuildContext;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Properties;

@Component(role = MavenResourcesFiltering.class, hint = "default")
public class PatternMavenResourcesFiltering extends DefaultMavenResourcesFiltering {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatternMavenResourcesFiltering.class);
    private static final String KEY_FILTERING_DEFAULT = "resource.filtering.default";
    private static final String DEFAULT_DELIMITER = "@";
    private static final String DEFAULT_PATTERN = "${$key}";

    @Inject
    public PatternMavenResourcesFiltering(MavenFileFilter mavenFileFilter, BuildContext buildContext) {
        super(mavenFileFilter, buildContext);
    }

    @Override
    public void filterResources(MavenResourcesExecution mavenResourcesExecution) throws MavenFilteringException {
        if (mavenResourcesExecution.isUseDefaultFilterWrappers()) {
            initDefaultFilterWrappers(mavenResourcesExecution);
            mavenResourcesExecution.setUseDefaultFilterWrappers(false);
        }

        // Ajout d'un FilterWrapper pour les valeurs par défaut
        String defaultPattern = DEFAULT_PATTERN;
        MavenProject mavenProject = mavenResourcesExecution.getMavenProject();
        Properties properties = Optional.ofNullable(mavenProject)
                .map(MavenProject::getProperties)
                .orElse(new Properties());

        if (properties.containsKey(KEY_FILTERING_DEFAULT)) {
            defaultPattern = (String) properties.get(KEY_FILTERING_DEFAULT);
        }

        MissingValueSource missingValueSource = new MissingValueSource(defaultPattern);
        mavenResourcesExecution.addFilerWrapperWithEscaping(missingValueSource, DEFAULT_DELIMITER, DEFAULT_DELIMITER, "\\", false);

        super.filterResources(mavenResourcesExecution);
        outputFeedback(missingValueSource);
    }

    private void initDefaultFilterWrappers(MavenResourcesExecution mavenResourcesExecution) {
        try {
            Method method = DefaultMavenResourcesFiltering.class.getDeclaredMethod(
                "handleDefaultFilterWrappers", MavenResourcesExecution.class
            );

            method.setAccessible(true);
            method.invoke(this, mavenResourcesExecution);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void outputFeedback(MissingValueSource missingValueSource) {
        if (LOGGER.isInfoEnabled()) {
            for (String key : missingValueSource.getMissingKeys()) {
                LOGGER.info(MessageUtils.buffer().a("Missing filter value: ").warning(key).toString());
            }
        }
    }
}
