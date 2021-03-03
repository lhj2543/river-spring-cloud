package com.river.learn.java.design.builder;

/**
 * 产品
 */
public class Product {
    private String name;
    private String type;
    private String notes;

    public Product() {
    }
    public Product(String name, String type, String notes) {
        this.name = name;
        this.type = type;
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
