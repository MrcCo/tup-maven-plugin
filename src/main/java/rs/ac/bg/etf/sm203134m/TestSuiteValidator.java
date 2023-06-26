package rs.ac.bg.etf.sm203134m;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import rs.ac.bg.etf.sm203134m.utils.FileUtils;

import java.util.Arrays;
import java.util.List;

@Mojo(name = "generate-test-suite", defaultPhase = LifecyclePhase.VALIDATE)
public class TestSuiteValidator extends AbstractMojo {

  private static final String DEFAULT_VALUE = "all";

  @Parameter(defaultValue = DEFAULT_VALUE, name = "files")
  private String files;

  @Parameter(defaultValue = "${project}")
  private MavenProject project;

  private final FileUtils fileUtils = new FileUtils();

  @Override
  public void execute() {

    List<Test> tests = null;
    if (DEFAULT_VALUE.equalsIgnoreCase(files)) {
      tests = fileUtils.getProjectFilesByExtension(project, ".tup").stream()
          .map(it -> new Test(it.toAbsolutePath().toString()))
          .toList();
    } else {

      var fileNames = Arrays.stream(files.split(",")).toList();

      tests = fileUtils.getProjectFilesByExtension(project, ".tup").stream()
          .filter(it -> fileNames.contains(it.getFileName().toString()))
          .map(it -> new Test(it.toAbsolutePath().toString()))
          .toList();
    }

    var suite = new TestSuite(tests);
    if (suite.getValidationResult().isCorrect()) {
      getLog().info("All tests in the test suite are valid!");
    }

    if (!suite.getValidationResult().getWarnings().isEmpty()) {
      getLog().warn("There are some warnings: ");
      suite.getValidationResult().getWarnings().forEach(
          it -> getLog().warn(it.getMessage())
      );
    }

    if (!suite.getValidationResult().getErrors().isEmpty()) {
      getLog().error("There are some warnings: ");
      suite.getValidationResult().getWarnings().forEach(
          it -> getLog().error(it.getMessage())
      );
    }

  }
}
