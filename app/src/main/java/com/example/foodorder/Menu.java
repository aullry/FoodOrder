package com.example.foodorder;

public class Menu {

    private int id;
    private String name;
    private int price;
    private String category;

    public Menu(
            int id,
            String name,
            int price,
            String category
    ) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }
}