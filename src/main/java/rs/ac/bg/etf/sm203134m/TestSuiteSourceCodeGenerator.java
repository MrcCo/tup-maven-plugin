package rs.ac.bg.etf.sm203134m;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import rs.ac.bg.etf.sm203134m.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Mojo(name = "generate-test-suite", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES)
public class TestSuiteSourceCodeGenerator extends AbstractMojo {

  private final FileUtils fileUtils = new FileUtils();
  @Parameter(defaultValue = "${project}")
  private MavenProject project;

  @Parameter(defaultValue = "${project.build.directory}/generated-test-sources")
  private File outputDirectory;

  @Override
  public void execute() {

    var testSuit = new TestSuite(
        fileUtils.getProjectFilesByExtension(project, ".tup").stream()
            .map(it -> new Test(it.toAbsolutePath().toString()))
            .toList()
    );

    testSuit.write(outputDirectory.getAbsolutePath());
    project.addTestCompileSourceRoot(outputDirectory.getAbsolutePath());

  }



}
