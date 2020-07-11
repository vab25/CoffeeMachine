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

        String requestJsonFilePath = projectPath + "/src/test/resources/Input1.json";
        coffeeMachine = new CoffeeMachine(requestJsonFilePath);
    }

    @Test
    public void healthOfmachine(){
        int hot_milk_quantity = coffeeMachine.ingredients.get("hot_milk").quantity_present;
        coffeeMachine.healthOfmachine();
        Assert.assertEquals(hot_milk_quantity,coffeeMachine.ingredients.get("hot_milk").quantity_present);
    }

    @Test
    public void refillMachine(){
        HashMap<String,Integer> ingridientList = new HashMap<>();
        int hot_milk_quantity = coffeeMachine.ingredients.get("hot_milk").quantity_present;
        ingridientList.put("hot_milk",500);
        coffeeMachine.refillMachine(ingridientList);
        Assert.assertEquals(hot_milk_quantity+500,coffeeMachine.ingredients.get("hot_milk").quantity_present);
    }

    @Test
    public void showBeverages(){
        int hot_milk_quantity = coffeeMachine.ingredients.get("hot_milk").quantity_present;
        coffeeMachine.showBeverages();
        Assert.assertEquals(hot_milk_quantity,coffeeMachine.ingredients.get("hot_milk").quantity_present);
    }

    @Test
    public void prepareBeverage_InvalidBeverage(){
        String beverageName ="hot_coffee";
        boolean invalidDrink = coffeeMachine.prepareBeverage("Latte");
        Assert.assertEquals(invalidDrink,false);
    }

    @Test
    public void prepareBeverage_EnoughQuantityOfIngridientsBeverage(){
        String beverageName ="hot_coffee";
        int hot_milk_quantity = coffeeMachine.ingredients.get("hot_milk").quantity_present;
        boolean invalidDrink = coffeeMachine.prepareBeverage(beverageName);
        Assert.assertEquals(invalidDrink,true);
        Assert.assertEquals(hot_milk_quantity-400,coffeeMachine.ingredients.get("hot_milk").quantity_present);
    }

    @Test
    public void prepareBeverage_LowIngridientQuantityOfBeverage(){
        String beverageName ="hot_coffee";
        boolean invalidDrink = coffeeMachine.prepareBeverage(beverageName);
        invalidDrink = coffeeMachine.prepareBeverage(beverageName);
        int hot_milk_quantity = coffeeMachine.ingredients.get("hot_milk").quantity_present;
        Assert.assertEquals(invalidDrink,false);
        Assert.assertEquals(hot_milk_quantity,coffeeMachine.ingredients.get("hot_milk").quantity_present);
    }

    @Test
    public void serveBeverage(){
        HashMap<String,Integer> ingridientList = new HashMap<>();
        int hot_milk_quantity = coffeeMachine.ingredients.get("hot_milk").quantity_present;
        ingridientList.put("hot_milk",400);
        ingridientList.put("hot_water",100);
        ingridientList.put("ginger_syrup",30);
        ingridientList.put("sugar_syrup",50);
        ingridientList.put("tea_leaves_syrup",30);
        coffeeMachine.refillMachine(ingridientList);
        coffeeMachine.serveBeverages();
        Assert.assertNotEquals(hot_milk_quantity,coffeeMachine.ingredients.get("hot_milk").quantity_present);
    }
}
