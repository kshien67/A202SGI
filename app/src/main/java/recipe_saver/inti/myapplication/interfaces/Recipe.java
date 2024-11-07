package recipe_saver.inti.myapplication.interfaces;

import android.graphics.Bitmap;

public interface Recipe {
    String getRecipeId();
    String getUserId();
    Bitmap getImage();
    String getRecipeName();
    String getDescription();
    int getTimeTaken();
    int getServings();
    int getCuisine();
    String getDifficulty();
}