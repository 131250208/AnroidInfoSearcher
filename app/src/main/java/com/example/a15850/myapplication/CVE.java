package com.example.a15850.myapplication;

public class CVE {
    private String num;
    private String product;
    private String version;

    public CVE(String num, String product, String version) {
        this.num = num;
        this.product = product;
        this.version = version;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
