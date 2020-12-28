package com.smartechbraintechnologies.medillah.Models;

public class ModelLabTest {
    private final String testImage;
    private final String testName;
    private final Double testMRP;
    private final Double testSellingPrice;

    public ModelLabTest(String testImage, String testName, Double testMRP, Double testSellingPrice) {
        this.testImage = testImage;
        this.testName = testName;
        this.testMRP = testMRP;
        this.testSellingPrice = testSellingPrice;
    }

    public String getTestImage() {
        return testImage;
    }

    public String getTestName() {
        return testName;
    }

    public Double getTestMRP() {
        return testMRP;
    }

    public Double getTestSellingPrice() {
        return testSellingPrice;
    }
}


