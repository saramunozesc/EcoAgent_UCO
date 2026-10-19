package es.uco.ecoagent.tools.security;

import es.uco.ecoagent.tools.api.ToolException;
import es.uco.ecoagent.tools.api.ToolExecutionContext;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Verifies the security boundary shared by all deterministic tools.
 */
public final class SecurityPoliciesTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test(expected = ToolException.class)
    public void rejectsAbsolutePaths() throws Exception {
        Path workspace = temporaryFolder.newFolder("workspace").toPath();
        new WorkspacePathPolicy(workspace).resolveInsideWorkspace(
                temporaryFolder.newFile("outside.txt").toPath().toAbsolutePath().toString());
    }

    @Test
    public void rejectsSymbolicLinkEscapesWhenSupported() throws Exception {
        Path workspace = temporaryFolder.newFolder("workspace").toPath();
        Path outside = temporaryFolder.newFolder("outside").toPath();
        Path link = workspace.resolve("linked");
        try {
            Files.createSymbolicLink(link, outside);
        } catch (IOException exception) {
            Assume.assumeNoException(exception);
        } catch (UnsupportedOperationException exception) {
            Assume.assumeNoException(exception);
        } catch (SecurityException exception) {
            Assume.assumeNoException(exception);
        }

        boolean rejected = false;
        try {
            new WorkspacePathPolicy(workspace).resolveInsideWorkspace("linked/secret.txt");
        } catch (ToolException expected) {
            rejected = true;
        }
        assertEquals(true, rejected);
    }

    @Test
    public void allowsOnlyKnownGitAndMavenInvocations() {
        CommandAllowlist allowlist = CommandAllowlist.defaults();
        allowlist.requireAllowed(Arrays.asList("git", "diff", "--"));
        allowlist.requireAllowed(Arrays.asList("git", "branch", "forgeia/import-demo-20261019"));
        allowlist.requireAllowed(Arrays.asList("mvn", "-q", "-pl", "forgeia-core", "test"));

        assertRejected(allowlist, Arrays.asList("git", "checkout", "--", "."));
        assertRejected(allowlist, Arrays.asList("mvn", "-q", "exec:java"));
        assertRejected(allowlist, Arrays.asList("mvn", "-q", "-DskipTests", "package"));
    }

    @Test
    public void removesSensitiveVariablesFromChildEnvironment() {
        Map<String, String> environment = new LinkedHashMap<String, String>();
        environment.put("PATH", "bin");
        environment.put("OPENAI_API_KEY", "secret");
        environment.put("GITHUB_TOKEN", "secret");
        environment.put("DB_PASSWORD", "secret");

        CommandEnvironmentSanitizer.removeSensitiveVariables(environment);

        assertEquals(Collections.singletonMap("PATH", "bin"), environment);
    }

    @Test
    public void destructiveOperationsAreDeniedUnlessExplicitlyApproved() throws Exception {
        Path workspace = temporaryFolder.newFolder("approval-workspace").toPath();
        ToolExecutionContext denied = new ToolExecutionContext(workspace);
        boolean rejected = false;
        try {
            denied.requireDestructiveApproval("delete-project", "demo");
        } catch (ToolException expected) {
            rejected = true;
        }
        assertEquals(true, rejected);

        ToolExecutionContext approved = new ToolExecutionContext(workspace,
                DestructiveOperationPolicy.withApprovals(Collections.singleton("delete-project")));
        approved.requireDestructiveApproval("delete-project", "demo");

        boolean wrongOperationAccepted = true;
        try {
            approved.requireDestructiveApproval("delete-logs", "logs");
        } catch (ToolException expected) {
            wrongOperationAccepted = false;
        }
        assertFalse(wrongOperationAccepted);
    }

    private static void assertRejected(CommandAllowlist allowlist, java.util.List<String> command) {
        boolean rejected = false;
        try {
            allowlist.requireAllowed(command);
        } catch (ToolException expected) {
            rejected = true;
        }
        assertEquals(true, rejected);
    }
}
