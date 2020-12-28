package com.smartechbraintechnologies.medillah.Models;

public class ModelMyLabTest {
    private String testID;
    private String testImage;
    private String testName;
    private String testStatus;
    private String testTiming;

    public ModelMyLabTest() {
    }

    public ModelMyLabTest(String testID, String testImage, String testName, String testStatus, String testTiming) {
        this.testID = testID;
        this.testImage = testImage;
        this.testName = testName;
        this.testStatus = testStatus;
        this.testTiming = testTiming;
    }

    public String getTestID() {
        return testID;
    }

    public String getTestImage() {
        return testImage;
    }

    public String getTestName() {
        return testName;
    }

    public String getTestStatus() {
        return testStatus;
    }

    public String getTestTiming() {
        return testTiming;
    }
}


