package es.uco.ecoagent.analysis.javaast;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Verifies lightweight Java indexing without LLM usage.
 */
public class JavaAstIndexerTest {
    /**
     * Ensures class, interface, imports, annotations and methods are summarized.
     */
    @Test
    public void indexesJavaFileSummary() throws Exception {
        Path root = Files.createTempDirectory("ast-index");
        Path file = root.resolve("src/UserService.java");
        Files.createDirectories(file.getParent());
        Files.write(file, ("package demo;\n"
                + "import demo.UserRepository;\n"
                + "@Service\n"
                + "public class UserService implements IUserService {\n"
                + "  public UserDto createUser(CreateUserDto dto) { return null; }\n"
                + "  public UserDto findById(Long id) { return null; }\n"
                + "}\n").getBytes("UTF-8"));

        JavaFileSummary summary = new JavaAstIndexer().indexFile(root, file);

        assertEquals("UserService", summary.getPrimaryType());
        assertTrue(summary.getImplementedTypes().contains("IUserService"));
        assertTrue(summary.getDependencies().contains("UserRepository"));
        assertEquals(2, summary.getMethods().size());
    }
}

