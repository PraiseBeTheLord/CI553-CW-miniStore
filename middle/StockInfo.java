package middle;

import catalogue.Product;

import java.util.ArrayList;
import java.util.List;

public class StockInfo {
    private final String productNum;
    private final String description;
    private final double price;
    private int stockLevel;

    public StockInfo(String productNum, String description, double price, int stockLevel) {
        this.productNum = productNum;
        this.description = description;
        this.price = price;
        this.stockLevel = stockLevel;
    }

    // Getters
    public String getProductNum() {
        return productNum;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getStockLevel() {
        return stockLevel;
    }

    // Setters (if needed)
    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
    }

    // You might want to add a method to convert a Product to StockInfo
    public static StockInfo fromProduct(Product product) {
        return new StockInfo(product.getProductNum(), product.getDescription(), product.getPrice(), product.getQuantity());
    }

    // Method to get details as a list of integers
    public List<Integer> getDetailsAsList() {
        List<Integer> details = new ArrayList<>();
        details.add(stockLevel);
        details.add((int) price); // Assuming price can be represented as an integer
        return details;
    }
}