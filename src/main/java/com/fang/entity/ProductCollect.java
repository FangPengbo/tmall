package com.fang.entity;

public class ProductCollect {
    private Integer productCollect_id;
    private Product productCollect_product;
    private User productCollect_user;

    public Integer getProductCollect_id() {
        return productCollect_id;
    }

    public void setProductCollect_id(Integer productCollect_id) {
        this.productCollect_id = productCollect_id;
    }

    public Product getProductCollect_product() {
        return productCollect_product;
    }

    public void setProductCollect_product(Product productCollect_product) {
        this.productCollect_product = productCollect_product;
    }

    public User getProductCollect_user() {
        return productCollect_user;
    }

    public void setProductCollect_user(User productCollect_user) {
        this.productCollect_user = productCollect_user;
    }

    public ProductCollect(Product productCollect_product, User productCollect_user) {
        this.productCollect_product = productCollect_product;
        this.productCollect_user = productCollect_user;
    }

    public ProductCollect() {
    }
}
