
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoffeeMachine {

    private Integer outlets;
    private Map<String, Integer> ingredients = new HashMap<>();
    private Map<String, Map<String,Integer>> beverages = new HashMap<>();
    private ExecutorService executorService;

    //Setup Coffee machine based on the json input received. Set outlets, beverages and ingredients
    public CoffeeMachine(String jsonPath) {
        ObjectMapper jsonParser = new ObjectMapper();
        try {
            Map map = jsonParser.readValue(Paths.get(jsonPath).toFile(), Map.class);
            map = (Map) map.get("machine");
            //fetch outlet
            outlets = (Integer) ((Map)map.get("outlets")).get("count_n");

            //Initialize ingredients
            for (Map.Entry<String, Integer> entry : ((Map<String, Integer>)map.get("total_items_quantity")).entrySet()) {
                ingredients.put(entry.getKey(), entry.getValue());
            }
            //Initialize Beverages and its recipe
            for (Map.Entry<String, Map<String, Integer>> entry : ((Map<String, Map<String, Integer>>)map.get("beverages")).entrySet()) {
                String beverage = entry.getKey();
                Map<String, Integer> recipe = entry.getValue();
                beverages.put(beverage, recipe);
            }
            executorService = Executors.newFixedThreadPool(outlets);
        } catch (IOException e) {
            System.out.println("File not found");
        }
    }

    public Map<String, Integer> getIngredients() {
        return ingredients;
    }

    //reloadMachine
    public void reloadMachine(Map<String,Integer> ingredientList) {
        for(Map.Entry<String,Integer> ingredient : ingredientList.entrySet()) {
            String key = ingredient.getKey();
            Integer value = ingredient.getValue();
           if(ingredients.containsKey(key)) {
               ingredients.put(key, ingredients.get(key) + value);
           } else {
               ingredients.put(key, value);
           }
        }
        System.out.println("Refill Done!!!");
    }
    //displayBeverages displays beverages
    public void displayBeverages(){
        System.out.println("Displaying Beverages");
        for(Map.Entry<String,Map<String,Integer>> beverage : beverages.entrySet()) {
            if (hasSufficientIngredients(beverage.getKey()))
                System.out.println(beverage.getKey() + "->In-Stock");
            else
                System.out.println(beverage.getKey() + "->Out-Of-Stock");
        }
    }

    //Check if the ingredients are available then prepare and reduce ingredients quantity
    public boolean prepareBeverage(String beverageName){
        boolean possible =this.hasSufficientIngredients(beverageName);
        if(!possible)
            return false;
        Map<String,Integer> beverage =beverages.get(beverageName);
        for(Map.Entry<String,Integer> e: beverage.entrySet()) {
            Integer quantityAvailable = ingredients.get(e.getKey());
            Integer quantityRequired = e.getValue();
            quantityAvailable -= quantityRequired;
            ingredients.put(e.getKey(), quantityAvailable);
        }
        System.out.println(beverageName+" is prepared");
        return true;
    }

    //Check if the ingredients are available to prepare the drink
    private Boolean hasSufficientIngredients(String beverageName){
        Boolean res = true;
        Map<String,Integer> beverage = beverages.get(beverageName);
        if(beverage==null) {
            System.out.println("Beverage is not present");
            return false;
        }
        for(Map.Entry<String,Integer> e : beverage.entrySet()){
            String ingredient = e.getKey();
            if(!ingredients.containsKey(ingredient)){
                System.out.println(beverageName+" cannot be prepared because item "+ingredient+" is not available");
                return false;
            }
            Integer availableIngredient = ingredients.get(ingredient);
            int quantityRequired = e.getValue();
            if(availableIngredient < quantityRequired) {
                System.out.println(beverageName+" cannot be prepared because item "+ingredient+" is not sufficient");
                return false;
            }
        }
        return res;
    }

    //serveBeverages tries all possible drinks from current state
    public void serveBeverages(){
        Set<String> beveragesList = new HashSet<>();
        while (beveragesList.size() < beverages.size()){
            for(Map.Entry<String, Map<String, Integer>> entry : beverages.entrySet()){
                Outlet outlet = new Outlet("Serving "+entry.getKey());
                if(!prepareBeverage(entry.getKey())){
                    beveragesList.add(entry.getKey());
                }else {
                    executorService.submit(new Thread(outlet));
                }
            }
        }
        health();
        System.out.println("Can not prepare any beverage.Please refill to resume.");
        executorService.shutdown();
    }

    public static void main(String[] args){

        String projectPath = Paths.get("").toAbsolutePath().normalize().toString();
        String requestJsonFilePath = projectPath + "/src/main/resources/Input.json";

        CoffeeMachine coffeeMachine = new CoffeeMachine(requestJsonFilePath);
        coffeeMachine.serveBeverages(); //Serves the beverage until no other drinks can be prepared
    }

    public void health() {
        System.out.println("Check machine status");
        for(Map.Entry<String,Integer> ingredientEntry : ingredients.entrySet()){
            System.out.println(ingredientEntry.getKey()+"--" + ingredientEntry.getValue());
        }
    }


}
