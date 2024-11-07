package recipe_saver.inti.myapplication.interfaces;

public class IngredientImpl {
    private String ingredientID;
    private String ingredientName;
    private String category;

    public IngredientImpl(String ingredientID, String ingredientName, String category) {
        this.ingredientID = ingredientID;
        this.ingredientName = ingredientName;
        this.category = category;
    }

    public IngredientImpl(String ingredientName, String category) {
        this.ingredientID = "";
        this.ingredientName = ingredientName;
        this.category = category;
    }

    public String getIngredientID() {
        return ingredientID;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public String getCategory() {
        return category;
    }
}
