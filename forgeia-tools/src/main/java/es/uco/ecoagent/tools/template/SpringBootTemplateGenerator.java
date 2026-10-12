package es.uco.ecoagent.tools.template;

import es.uco.ecoagent.tools.api.ToolException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Generates a known Spring Boot project skeleton without using an LLM.
 */
public final class SpringBootTemplateGenerator {
    /**
     * Creates a deterministic Spring Boot template under the target directory.
     */
    public TemplateResult generate(Path targetDirectory, String groupId, String artifactId) {
        Path root = targetDirectory.toAbsolutePath().normalize();
        String packageName = groupId + "." + artifactId.replace("-", "");
        String packagePath = packageName.replace(".", "/");
        try {
            write(root.resolve("pom.xml"), pomXml(groupId, artifactId));
            write(root.resolve("src/main/java/" + packagePath + "/Application.java"), applicationJava(packageName));
            write(root.resolve("src/main/java/" + packagePath + "/health/HealthController.java"), healthControllerJava(packageName));
            write(root.resolve("src/main/java/" + packagePath + "/task/Task.java"), taskEntityJava(packageName));
            write(root.resolve("src/main/java/" + packagePath + "/task/TaskRepository.java"), taskRepositoryJava(packageName));
            write(root.resolve("src/main/java/" + packagePath + "/task/TaskService.java"), taskServiceJava(packageName));
            write(root.resolve("src/main/resources/application.yml"), applicationYml());
            write(root.resolve("src/test/java/" + packagePath + "/health/HealthControllerTest.java"), healthControllerTestJava(packageName));
            return new TemplateResult(root, 8);
        } catch (IOException exception) {
            throw new ToolException("Cannot generate Spring Boot template: " + root, exception);
        }
    }

    /**
     * Writes UTF-8 content and creates parent directories.
     */
    private static void write(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent());
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Returns the Maven build file for the generated application.
     */
    private static String pomXml(String groupId, String artifactId) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n"
                + "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
                + "    <modelVersion>4.0.0</modelVersion>\n"
                + "    <parent>\n"
                + "        <groupId>org.springframework.boot</groupId>\n"
                + "        <artifactId>spring-boot-starter-parent</artifactId>\n"
                + "        <version>3.3.4</version>\n"
                + "        <relativePath/>\n"
                + "    </parent>\n"
                + "    <groupId>" + groupId + "</groupId>\n"
                + "    <artifactId>" + artifactId + "</artifactId>\n"
                + "    <version>0.1.0-SNAPSHOT</version>\n"
                + "    <properties>\n"
                + "        <java.version>21</java.version>\n"
                + "    </properties>\n"
                + "    <dependencies>\n"
                + "        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>\n"
                + "        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-jpa</artifactId></dependency>\n"
                + "        <dependency><groupId>org.postgresql</groupId><artifactId>postgresql</artifactId><scope>runtime</scope></dependency>\n"
                + "        <dependency><groupId>com.h2database</groupId><artifactId>h2</artifactId><scope>test</scope></dependency>\n"
                + "        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-test</artifactId><scope>test</scope></dependency>\n"
                + "    </dependencies>\n"
                + "    <build><plugins><plugin><groupId>org.springframework.boot</groupId><artifactId>spring-boot-maven-plugin</artifactId></plugin></plugins></build>\n"
                + "</project>\n";
    }

    /**
     * Returns the Spring Boot application entry point.
     */
    private static String applicationJava(String packageName) {
        return "package " + packageName + ";\n\n"
                + "import org.springframework.boot.SpringApplication;\n"
                + "import org.springframework.boot.autoconfigure.SpringBootApplication;\n\n"
                + "@SpringBootApplication\n"
                + "public class Application {\n"
                + "    public static void main(String[] args) {\n"
                + "        SpringApplication.run(Application.class, args);\n"
                + "    }\n"
                + "}\n";
    }

    /**
     * Returns a simple health endpoint for template verification.
     */
    private static String healthControllerJava(String packageName) {
        return "package " + packageName + ".health;\n\n"
                + "import java.util.Map;\n"
                + "import org.springframework.web.bind.annotation.GetMapping;\n"
                + "import org.springframework.web.bind.annotation.RestController;\n\n"
                + "@RestController\n"
                + "public class HealthController {\n"
                + "    @GetMapping(\"/api/health\")\n"
                + "    public Map<String, String> health() {\n"
                + "        return Map.of(\"status\", \"UP\");\n"
                + "    }\n"
                + "}\n";
    }

    /**
     * Returns a minimal JPA entity used by the generated layers.
     */
    private static String taskEntityJava(String packageName) {
        return "package " + packageName + ".task;\n\n"
                + "import jakarta.persistence.Entity;\n"
                + "import jakarta.persistence.GeneratedValue;\n"
                + "import jakarta.persistence.Id;\n\n"
                + "@Entity\n"
                + "public class Task {\n"
                + "    @Id @GeneratedValue\n"
                + "    private Long id;\n"
                + "    private String title;\n"
                + "    private boolean completed;\n\n"
                + "    protected Task() {}\n"
                + "    public Task(String title) { this.title = title; }\n"
                + "    public Long getId() { return id; }\n"
                + "    public String getTitle() { return title; }\n"
                + "    public boolean isCompleted() { return completed; }\n"
                + "    public void markCompleted() { this.completed = true; }\n"
                + "}\n";
    }

    /**
     * Returns a Spring Data repository for the template entity.
     */
    private static String taskRepositoryJava(String packageName) {
        return "package " + packageName + ".task;\n\n"
                + "import org.springframework.data.jpa.repository.JpaRepository;\n\n"
                + "public interface TaskRepository extends JpaRepository<Task, Long> {\n"
                + "}\n";
    }

    /**
     * Returns a service layer used by future generated code.
     */
    private static String taskServiceJava(String packageName) {
        return "package " + packageName + ".task;\n\n"
                + "import java.util.List;\n"
                + "import org.springframework.stereotype.Service;\n\n"
                + "@Service\n"
                + "public class TaskService {\n"
                + "    private final TaskRepository repository;\n"
                + "    public TaskService(TaskRepository repository) { this.repository = repository; }\n"
                + "    public List<Task> list() { return repository.findAll(); }\n"
                + "}\n";
    }

    /**
     * Returns database configuration with PostgreSQL defaults.
     */
    private static String applicationYml() {
        return "spring:\n"
                + "  datasource:\n"
                + "    url: jdbc:postgresql://localhost:5432/app\n"
                + "    username: app\n"
                + "    password: app\n"
                + "  jpa:\n"
                + "    hibernate:\n"
                + "      ddl-auto: update\n";
    }

    /**
     * Returns a test that verifies the health endpoint contract.
     */
    private static String healthControllerTestJava(String packageName) {
        return "package " + packageName + ".health;\n\n"
                + "import static org.assertj.core.api.Assertions.assertThat;\n"
                + "import org.junit.jupiter.api.Test;\n\n"
                + "class HealthControllerTest {\n"
                + "    @Test\n"
                + "    void healthReturnsUp() {\n"
                + "        assertThat(new HealthController().health().get(\"status\")).isEqualTo(\"UP\");\n"
                + "    }\n"
                + "}\n";
    }
}

