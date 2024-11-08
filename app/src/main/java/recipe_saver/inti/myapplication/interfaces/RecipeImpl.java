package recipe_saver.inti.myapplication.interfaces;

import android.graphics.Bitmap;

public class RecipeImpl implements Recipe {
    private String recipeId;
    private String userId;
    private Bitmap image;
    private String recipeName;
    private String description;
    private int timeTaken;
    private int servings;
    private String cuisine;
    private String difficulty;
    private String instructions;

    public RecipeImpl(String recipeId, String userId, Bitmap image, String recipeName, String description, int timeTaken, int servings, String cuisine, String difficulty, String instructions) {
        this.recipeId = recipeId;
        this.userId = userId;
        this.image = image;
        this.recipeName = recipeName;
        this.description = description;
        this.timeTaken = timeTaken;
        this.servings = servings;
        this.cuisine = cuisine;
        this.difficulty = difficulty;
        this.instructions = instructions;
    }

    public RecipeImpl(String userId, Bitmap image, String recipeName, String description, int timeTaken, int servings, String cuisine, String difficulty, String instructions) {
        this.recipeId = "";
        this.userId = userId;
        this.image = image;
        this.recipeName = recipeName;
        this.description = description;
        this.timeTaken = timeTaken;
        this.servings = servings;
        this.cuisine = cuisine;
        this.difficulty = difficulty;
        this.instructions = instructions;
    }

    public RecipeImpl(Bitmap image, String recipeName, String description, int timeTaken, int servings, String cuisine, String difficulty, String instructions) {
        this.recipeId = "";
        this.userId = "";
        this.image = image;
        this.recipeName = recipeName;
        this.description = description;
        this.timeTaken = timeTaken;
        this.servings = servings;
        this.cuisine = cuisine;
        this.difficulty = difficulty;
        this.instructions = instructions;
    }

    @Override
    public String getRecipeId() {
        return recipeId;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public Bitmap getImage() {
        return image;
    }

    @Override
    public String getRecipeName() {
        return recipeName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getTimeTaken() {
        return timeTaken;
    }

    @Override
    public int getServings() {
        return servings;
    }

    @Override
    public String getCuisine() {
        return cuisine;
    }

    @Override
    public String getDifficulty() {
        return difficulty;
    }

    @Override
    public String getInstructions() {
        return instructions;
    }
}