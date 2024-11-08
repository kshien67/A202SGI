package recipe_saver.inti.myapplication.interfaces;

public class IngredientImpl {
    private String ingredient_id;
    private String ingredient_name;
    private String category;

    public IngredientImpl(String ingredient_id, String ingredient_name, String category) {
        this.ingredient_id = ingredient_id;
        this.ingredient_name = ingredient_name;
        this.category = category;
    }

    public IngredientImpl(String ingredient_name, String category) {
        this.ingredient_id = "";
        this.ingredient_name = ingredient_name;
        this.category = category;
    }

    public String getIngredient_id() {
        return ingredient_id;
    }

    public String getIngredient_name() {
        return ingredient_name;
    }

    public String getCategory() {
        return category;
    }
}
