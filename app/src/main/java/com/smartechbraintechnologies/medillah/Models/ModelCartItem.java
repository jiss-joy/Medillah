package com.smartechbraintechnologies.medillah.Models;

public class ModelCartItem {

    private String productImage;
    private String productID;
    private String productBrand;
    private String productName;
    private Double productSellingPrice;
    private Double productMRP;
    private Double productDiscount;
    private Double productSavings;
    private Integer productQuantity;

    public ModelCartItem() {
    }

    public ModelCartItem(String productImage, String productID, String productBrand, String productName, Double productSellingPrice, Double productMRP, Double productDiscount, Double productSavings, Integer productQuantity) {
        this.productImage = productImage;
        this.productID = productID;
        this.productBrand = productBrand;
        this.productName = productName;
        this.productSellingPrice = productSellingPrice;
        this.productMRP = productMRP;
        this.productDiscount = productDiscount;
        this.productSavings = productSavings;
        this.productQuantity = productQuantity;
    }

    public String getProductImage() {
        return productImage;
    }

    public String getProductID() {
        return productID;
    }

    public String getProductBrand() {
        return productBrand;
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

    public Double getProductSavings() {
        return productSavings;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }
}
