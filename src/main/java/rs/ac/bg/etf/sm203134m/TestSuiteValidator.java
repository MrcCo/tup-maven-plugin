package rs.ac.bg.etf.sm203134m;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import rs.ac.bg.etf.sm203134m.utils.FileUtils;
import rs.ac.bg.etf.sm203134m.utils.MavenProjectDependencyChecker;

import java.util.Arrays;
import java.util.List;

@Mojo(name = "validate-test-suite", defaultPhase = LifecyclePhase.VALIDATE)
public class TestSuiteValidator extends AbstractMojo {

  private static final String ALL_FILES = "all";

  @Parameter(defaultValue = ALL_FILES, name = "files")
  private String files;

  @Parameter(defaultValue = "${project}")
  private MavenProject project;

  private final FileUtils fileUtils = new FileUtils();

  @Override
  public void execute() {

    var missing = new MavenProjectDependencyChecker(project).checkForMandatoryDependencies();
    if(!missing.isEmpty()) {
      getLog().error("Project is missing the following dependencies: ");
      missing.forEach(it -> getLog().error(it));
      throw new RuntimeException("Project missing dependencies");
    }

    List<TestCase> tests;
    if (ALL_FILES.equalsIgnoreCase(files)) {

      tests = fileUtils.getProjectFilesByExtension(project, ".tup").stream()
          .map(it -> new TestCase(it.toAbsolutePath().toString()))
          .toList();

    } else {

      var fileNames = Arrays.stream(files.split(",")).toList();

      tests = fileUtils.getProjectFilesByExtension(project, ".tup").stream()
          .filter(it -> fileNames.contains(it.getFileName().toString()))
          .map(it -> new TestCase(it.toAbsolutePath().toString()))
          .toList();
    }

    var suite = new TestSuite(tests);

    if (!suite.getValidationResult().getWarnings().isEmpty()) {
      getLog().warn("There are some warnings: ");
      suite.getValidationResult().getWarnings().forEach(
          it -> getLog().warn(it.getMessage())
      );
    }

    if (!suite.getValidationResult().getErrors().isEmpty()) {
      getLog().error("There are some errors: ");
      suite.getValidationResult().getErrors().forEach(
          it -> getLog().error(it.getMessage())
      );
      throw new RuntimeException("Validation failed due to reported errors");
    }

    if (suite.getValidationResult().isCorrect()) {
      getLog().info("All tests in the test suite are valid!");
    }
  }
}
