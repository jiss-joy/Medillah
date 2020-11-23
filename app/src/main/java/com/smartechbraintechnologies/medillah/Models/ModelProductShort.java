package com.smartechbraintechnologies.medillah.Models;

import java.util.ArrayList;

public class ModelProductShort {

    private ArrayList<String> productImage;
    private String productName;
    private Double productSellingPrice, productMRP, productDiscount, productSaveAmount;

    public ModelProductShort() {
        //Empty Constructor required.
    }

    public ModelProductShort(ArrayList<String> productImage, String productName, Double productSellingPrice, Double productMRP, Double productDiscount, Double productSaveAmount) {
        this.productImage = productImage;
        this.productName = productName;
        this.productSellingPrice = productSellingPrice;
        this.productMRP = productMRP;
        this.productDiscount = productDiscount;
        this.productSaveAmount = productSaveAmount;
    }

    public String getProductImage() {
        return productImage.get(0);
    }

    public String getProductName() {
        return productName;
    }

    public Double getProductSellingPrice() {
        return productSellingPrice;
    }

    public Double getProductMRP() {
        return productMRP;
    }

    public Double getProductDiscount() {
        return productDiscount;
    }
}
