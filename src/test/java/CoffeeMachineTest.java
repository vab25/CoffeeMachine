import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.HashMap;

public class CoffeeMachineTest {
    CoffeeMachine coffeeMachine;
    @Before
    public void setup(){
        String projectPath = Paths.get("").toAbsolutePath().normalize().toString();

        String requestJsonFilePath = projectPath + "/src/test/resources/Input.json";
        coffeeMachine = new CoffeeMachine(requestJsonFilePath);
    }

    @Test
    public void health(){
        int hotMilkQuantity = coffeeMachine.getIngredients().get("hot_milk");
        coffeeMachine.health();
        Assert.assertEquals(hotMilkQuantity, (int)coffeeMachine.getIngredients().get("hot_milk"));
    }

    @Test
    public void reloadMachine(){
        HashMap<String,Integer> ingredientList = new HashMap<>();
        int hotMilkQuantity = coffeeMachine.getIngredients().get("hot_milk");
        ingredientList.put("hot_milk",500);
        coffeeMachine.reloadMachine(ingredientList);
        Assert.assertEquals(hotMilkQuantity+500, (int)coffeeMachine.getIngredients().get("hot_milk"));
    }

    @Test
    public void displayBeverages(){
        int hotMilkQuantity = coffeeMachine.getIngredients().get("hot_milk");
        coffeeMachine.displayBeverages();
        Assert.assertEquals(hotMilkQuantity, (int)coffeeMachine.getIngredients().get("hot_milk"));
    }

    @Test
    public void prepareInvalidBeverage(){
        boolean invalidDrink = coffeeMachine.prepareBeverage("Latte");
        Assert.assertEquals(invalidDrink,false);
    }

    @Test
    public void prepareValidBeverage(){
        String beverageName ="hot_coffee";
        int hotMilkQuantity = coffeeMachine.getIngredients().get("hot_milk");
        boolean invalidDrink = coffeeMachine.prepareBeverage(beverageName);
        Assert.assertEquals(invalidDrink,true);
        Assert.assertEquals(hotMilkQuantity-400, (int)coffeeMachine.getIngredients().get("hot_milk"));
    }

    @Test
    public void prepareLowIngredientBeverage(){
        String beverageName ="hot_coffee";
        coffeeMachine.getIngredients().put("hot_milk", 100);
        boolean invalidDrink = coffeeMachine.prepareBeverage(beverageName);
        int hotMilkQuantity = coffeeMachine.getIngredients().get("hot_milk");
        Assert.assertEquals(invalidDrink,false);
        Assert.assertEquals(hotMilkQuantity, (int)coffeeMachine.getIngredients().get("hot_milk"));
    }

    @Test
    public void serveBeverage(){
        HashMap<String,Integer> ingridientList = new HashMap<>();
        int hotMilkQuantity = coffeeMachine.getIngredients().get("hot_milk");
        ingridientList.put("hot_milk",400);
        ingridientList.put("hot_water",100);
        ingridientList.put("ginger_syrup",30);
        ingridientList.put("sugar_syrup",50);
        ingridientList.put("tea_leaves_syrup",30);
        coffeeMachine.reloadMachine(ingridientList);
        coffeeMachine.serveBeverages();
        Assert.assertNotEquals(hotMilkQuantity, (int)coffeeMachine.getIngredients().get("hot_milk"));
    }
}
