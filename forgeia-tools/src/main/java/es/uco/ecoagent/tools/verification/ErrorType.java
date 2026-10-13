package es.uco.ecoagent.tools.verification;

/**
 * Classifies build and test errors before they reach repair agents.
 */
public enum ErrorType {
    COMPILATION_ERROR,
    TEST_FAILURE,
    MAVEN_ERROR,
    UNKNOWN_ERROR
}

