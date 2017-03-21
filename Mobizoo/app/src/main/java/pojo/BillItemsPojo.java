package pojo;

/**
 * Created by mailt on 3/20/2017.
 */

public class BillItemsPojo {
    int id, price_per, total_price;
    String quantity, name, details, discount, resource_url;

    public BillItemsPojo() {
    }

    public BillItemsPojo(int id, int price_per, int total_price, String quantity, String name, String details, String discount, String resource_url) {
        this.id = id;
        this.price_per = price_per;
        this.total_price = total_price;
        this.quantity = quantity;
        this.name = name;
        this.details = details;
        this.discount = discount;
        this.resource_url = resource_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice_per() {
        return price_per;
    }

    public void setPrice_per(int price_per) {
        this.price_per = price_per;
    }

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getResource_url() {
        return resource_url;
    }

    public void setResource_url(String resource_url) {
        this.resource_url = resource_url;
    }
}
