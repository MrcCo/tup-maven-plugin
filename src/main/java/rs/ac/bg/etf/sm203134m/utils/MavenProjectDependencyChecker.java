package rs.ac.bg.etf.sm203134m.utils;

import org.apache.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MavenProjectDependencyChecker {

    private final MavenProject mavenProject;

    private final List<String> requiredArtefacts = List.of(
        "org.junit.jupiter:junit-jupiter-engine",
        "org.junit.jupiter:junit-jupiter-params",
        "org.junit.jupiter:junit-jupiter-api",
        "com.fasterxml.jackson.core:jackson-databind",
        "com.squareup.okhttp3:okhttp",
        "io.github.bonigarcia:webdrivermanager",
        "org.seleniumhq.selenium:selenium-java"
    );

    public MavenProjectDependencyChecker(MavenProject mavenProject) {
        this.mavenProject = mavenProject;
    }

    public List<String> checkForMandatoryDependencies() {

        var artefacts = mavenProject.getDependencies().stream().map(
                it -> it.getGroupId() + ":" + it.getArtifactId()
        ).collect(Collectors.toSet());

        var missingRequirements = new ArrayList<>(requiredArtefacts);
        for (String artefact : artefacts) {
            missingRequirements.remove(artefact);
        }

        return missingRequirements;

    }

}
