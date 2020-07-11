import java.util.HashMap;

public class Beverage {
    String name;
    HashMap<String,Integer> recipe = new HashMap<>();

    public Beverage(String name, HashMap<String, Integer> recipe) {
        this.name = name;
        this.recipe = recipe;
    }

}
