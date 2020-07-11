
import org.codehaus.jackson.map.ObjectMapper;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoffeeMachine {
    int outlets;
    ExecutorService outletPool;
    HashMap<String,Ingredient> ingredients = new HashMap<>();
    HashMap<String,Beverage>  beverages = new HashMap<>();

    public void healthOfmachine(){
        System.out.println("##############################HEATLH CHECK##############");
        for(Map.Entry<String,Ingredient> ingredientEntry:ingredients.entrySet()){
            System.out.println(ingredientEntry.getKey()+"--->"+((Ingredient)ingredientEntry.getValue()).quantity_present);
        }
        System.out.println("##############################HEATLH CHECK---END##############");
    }

    //refill
    public void refillMachine(HashMap<String,Integer> ingridientList){
        for(Map.Entry<String,Integer> e:ingridientList.entrySet()) {
            Ingredient ingredient = ingredients.get(e.getKey());
            if (ingredient == null) {
                ingredient = new Ingredient(e.getKey(), e.getValue());
                ingredients.put(e.getKey(), ingredient);
            }else{
                ingredient.quantity_present+=e.getValue();
                ingredients.put(e.getKey(), ingredient);
            }
        }
        System.out.println("Refill Done!!!");
    }
    //Show Beverages options
    public void showBeverages(){
        System.out.println("##############################Beverages List##############");
        for(Map.Entry<String,Beverage> beverageEntry:beverages.entrySet()){
            if(isItPossibleToPrepare(beverageEntry.getKey()))
                System.out.println(beverageEntry.getKey()+"--->In-Stock");
            else
                System.out.println(beverageEntry.getKey()+"--->Out-Of-Stock");
        }
        System.out.println("##############################Beverages List---END##############");
    }

    //Setup Coffee machine based on the json input received. Set outlets, beverages and ingredients
    public CoffeeMachine(String s) {
        ObjectMapper jsonParser = new ObjectMapper();
        try {
            Map map = jsonParser.readValue(Paths.get(s).toFile(), Map.class);
            map = (Map) map.get("machine");
            //fetch outlet
            outlets = (Integer) ((Map)map.get("outlets")).get("count_n");

            //Initialize ingredients
            for (Map.Entry<?, ?> entry : ((Map<?, ?>)map.get("total_items_quantity")).entrySet()) {
                Ingredient ingredient = new Ingredient((String) entry.getKey(),(Integer) entry.getValue());
                ingredients.put((String) entry.getKey(),ingredient);
            }
            //Initialize Beverages and its recipe
            for (Map.Entry<?, ?> entry : ((Map<?, ?>)map.get("beverages")).entrySet()) {
                String beverageName = (String) entry.getKey();
                Map recipe = (Map) entry.getValue();
                HashMap<String,Integer> brecipe = new HashMap<>();
                for (Map.Entry<?, ?> recipeEntry : ((Map<?, ?>)recipe).entrySet()) {
                    brecipe.put((String)recipeEntry.getKey(),(Integer) recipeEntry.getValue());
                }
                Beverage beverage = new Beverage(beverageName,brecipe);
                beverages.put(beverageName,beverage);
            }
            outletPool = Executors.newFixedThreadPool(outlets);
        } catch (IOException e) {
            System.out.println("File not found");
        }
    }

    //Check if the ingredients are available to prepare the drink
    private boolean isItPossibleToPrepare(String beverageName){
        boolean res = true;
        Beverage beverage =beverages.get(beverageName);
        if(beverage==null) {
            System.out.println("Beverage is not present");
            return false;
        }
        for(Map.Entry<String,Integer> e: beverage.recipe.entrySet()){
            String ingredient = e.getKey();
            if(!ingredients.containsKey(ingredient)){
                System.out.println(beverageName+" cannot be prepared because item "+ingredient+" is not available");
                return false;
            }
            Ingredient available_ingridient = ingredients.get(e.getKey());
            int quantity_required = e.getValue();
            if(available_ingridient.quantity_present<quantity_required) {
                System.out.println(beverageName+" cannot be prepared because item "+ingredient+" is not sufficient");
                return false;
            }
        }
        return res;
    }

    //Check if the ingredients are available then prepare and reduce ingridients quantity
    public boolean prepareBeverage(String beverageName){
        boolean possible =this.isItPossibleToPrepare(beverageName);
        if(!possible)
            return false;
        Beverage beverage =beverages.get(beverageName);
        for(Map.Entry<String,Integer> e: beverage.recipe.entrySet()){
            Ingredient ingredient = ingredients.get(e.getKey());
            int quantity_required = e.getValue();
            ingredient.quantity_present -=quantity_required;
        }
        System.out.println(beverageName+" is prepared");
        return true;
    }

    //this funtion tries all possible drinks from current state
    public void serveBeverages(){
        int c=0;
        Set<String> beveragesList =new HashSet<>();
        while (beveragesList.size()<beverages.size()){
            for(Map.Entry<String,Beverage> entry:beverages.entrySet()){
                Outlet o = new Outlet("Serving "+entry.getKey());

                if(!prepareBeverage(entry.getKey())){
                    beveragesList.add(entry.getKey());
                }else {
                    outletPool.submit(new Thread(o));
                }
            }
        }
        healthOfmachine();
        System.out.println("Can not prepare any beverage.Please refill to resume.");
        outletPool.shutdown();
    }

    public static void main(String[] args){
        String projectPath = Paths.get("").toAbsolutePath().normalize().toString();

        String requestJsonFilePath = projectPath + "/src/main/resources/Input1.json";
        //refer the Input1.json for input
        CoffeeMachine coffeeMachine = new CoffeeMachine("/Users/reshma.kumari/Documents/Projects/src/main/resources/Input1.json");
        coffeeMachine.serveBeverages(); //Servers the beverage untill no other drinks can be prepared
    }
}
