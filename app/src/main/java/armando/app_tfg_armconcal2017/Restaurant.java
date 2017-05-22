package armando.app_tfg_armconcal2017;

public class Restaurant {

    private int id, likes, dislikes;
    private String name, phone;


    public Restaurant(int id, int likes, int dislikes, String name, String phone) {
        this.id = id;
        this.likes = likes;
        this.dislikes = dislikes;
        this.name = name;
        this.phone = phone;
    }

    public int getId() {

        return id;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
