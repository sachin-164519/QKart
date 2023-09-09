package QKART_TESTNG;

public class TestResult {
    private String testCaseName;
    private Object[] parameters;
    private String status;

    public TestResult(String testCaseName, Object[] testParams, String status) {
        this.testCaseName = testCaseName;
        this.parameters = testParams;
        this.status = status;
    }

    // Getters and setters

    public String getTestCaseName() {
        return testCaseName;
    }
    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
