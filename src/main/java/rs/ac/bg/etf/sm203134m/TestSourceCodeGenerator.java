package rs.ac.bg.etf.sm203134m;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Mojo(name = "generate-test-source-code", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES)
public class TestSourceCodeGenerator extends AbstractMojo {

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}/generated-test-sources")
    private File outputDirectory;

    @Override
    public void execute() {

        getFiles().forEach(
                it -> {
                    var transpiler = new Transpiler(it.toAbsolutePath().toString());
                    transpiler.writeCode(outputDirectory.getAbsolutePath());
                }
        );

        project.addTestCompileSourceRoot(outputDirectory.getAbsolutePath());
    }

    private List<Path> getFiles() {
        try (Stream<Path> paths = Files.walk(Paths.get(project.getBasedir().getAbsolutePath()))) {
            return paths.filter(Files::isRegularFile).filter(it -> it.toString().endsWith(".tup")).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
