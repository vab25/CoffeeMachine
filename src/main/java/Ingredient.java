public class Ingredient {
    String name;
    int quantity_present;
    @Override
    public int hashCode(){
        return name.hashCode();
    }
    @Override
    public boolean equals(Object o){
        return this.name.equals(((Ingredient)o).name);
    }
    public Ingredient(String name, int quantity_present) {
        this.name = name;
        this.quantity_present = quantity_present;
    }
    public void refill(int quantity){
        quantity_present+=quantity;
    }
}
