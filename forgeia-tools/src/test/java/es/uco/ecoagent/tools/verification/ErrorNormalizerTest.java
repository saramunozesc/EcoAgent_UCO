package es.uco.ecoagent.tools.verification;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Verifies Maven logs are reduced to compact repair errors.
 */
public class ErrorNormalizerTest {
    /**
     * Ensures compilation errors expose file, line and related symbol.
     */
    @Test
    public void normalizesCompilationError() {
        String output = "[ERROR] /tmp/UserService.java:[47,13] cannot find symbol findByUsername\n"
                + "[ERROR]   symbol:   method findByUsername(java.lang.String)\n"
                + "[ERROR]   location: variable repository of type UserRepository\n";

        List<NormalizedError> errors = new ErrorNormalizer().normalize(output);

        assertEquals(1, errors.size());
        assertEquals(ErrorType.COMPILATION_ERROR, errors.get(0).getType());
        assertEquals("UserService.java", errors.get(0).getFile());
        assertEquals(47, errors.get(0).getLine());
        assertEquals("findByUsername", errors.get(0).getRelatedSymbol());
    }

    /**
     * Ensures test failures keep the failing test name.
     */
    @Test
    public void normalizesTestFailure() {
        String output = "[ERROR] UserServiceTest.createUser Time elapsed: 0.01 sec <<< FAILURE!\n";

        List<NormalizedError> errors = new ErrorNormalizer().normalize(output);

        assertEquals(ErrorType.TEST_FAILURE, errors.get(0).getType());
        assertEquals("UserServiceTest.createUser", errors.get(0).getTestName());
    }
}

