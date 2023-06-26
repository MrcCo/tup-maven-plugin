package rs.ac.bg.etf.sm203134m.utils;

import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class FileUtils {

  public List<Path> getProjectFilesByExtension(MavenProject project, String extension) {
    try (Stream<Path> paths = Files.walk(Paths.get(project.getBasedir().getAbsolutePath()))) {
      return paths.filter(Files::isRegularFile).filter(it -> it.toString().endsWith(extension)).toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
