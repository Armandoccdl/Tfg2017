package armando.app_tfg_armconcal2017;


import java.sql.Date;

public class Event {

    private int id;
    private String name, date, restaurant;
    private double price;


    public Event(int id, String restaurant, String name, String date, double price) {

        this.id = id;
        this.restaurant = restaurant;
        this.name = name;
        this.date = date;
        this.price = price;
    }


    public int getId() {
        return id;
    }


    public String getRestaurant() {
        return restaurant;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }

}


