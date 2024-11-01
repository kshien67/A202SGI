package recipe_saver.inti.myapplication;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RecipeSaver.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_USERS = "Users";
    public static final String TABLE_RECIPE = "Recipe";
    public static final String TABLE_INGREDIENT = "Ingredient";
    public static final String TABLE_RECIPE_INGREDIENT = "RecipeIngredient";
    public static final String TABLE_USER_RECIPE_ACTION = "UserRecipeAction";

    // Users Table Columns
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMAIL = "email";

    // Recipe Table Columns
    public static final String COLUMN_RECIPE_ID = "recipe_id";
    public static final String COLUMN_RECIPE_NAME = "recipe_name";
    public static final String COLUMN_USER_ID_FK = "user_id";  // Foreign key
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CUISINE = "cuisine";
    public static final String COLUMN_TIME_TAKEN = "time_taken";
    public static final String COLUMN_SERVINGS = "servings";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_DIFFICULTY = "difficulty";

    // Ingredient Table Columns
    public static final String COLUMN_INGREDIENT_ID = "ingredient_id";
    public static final String COLUMN_INGREDIENT_NAME = "ingredient_name";

    // RecipeIngredient Table Columns
    public static final String COLUMN_QUANTITY = "quantity";

    // UserRecipeAction Table Columns
    public static final String COLUMN_ACTION_ID = "action_id";
    public static final String COLUMN_ACTION_TYPE = "action_type";
    public static final String COLUMN_CREATED_AT = "created_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT NOT NULL, " +
                COLUMN_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_EMAIL + " TEXT UNIQUE NOT NULL);";

        String createRecipeTable = "CREATE TABLE " + TABLE_RECIPE + " (" +
                COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID_FK + " INTEGER, " +
                COLUMN_IMAGE + " BLOB, " +
                COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_CUISINE + " TEXT, " +
                COLUMN_TIME_TAKEN + " INTEGER, " +
                COLUMN_SERVINGS + " INTEGER, " +
                COLUMN_CALORIES + " INTEGER, " +
                COLUMN_DIFFICULTY + " TEXT CHECK(" + COLUMN_DIFFICULTY + " IN ('easy', 'medium', 'hard')), " +
                "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE);";

        String createIngredientTable = "CREATE TABLE " + TABLE_INGREDIENT + " (" +
                COLUMN_INGREDIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_INGREDIENT_NAME + " TEXT NOT NULL UNIQUE);";

        String createRecipeIngredientTable = "CREATE TABLE " + TABLE_RECIPE_INGREDIENT + " (" +
                COLUMN_RECIPE_ID + " INTEGER, " +
                COLUMN_INGREDIENT_ID + " INTEGER, " +
                COLUMN_QUANTITY + " TEXT, " +
                "PRIMARY KEY (" + COLUMN_RECIPE_ID + ", " + COLUMN_INGREDIENT_ID + "), " +
                "FOREIGN KEY(" + COLUMN_RECIPE_ID + ") REFERENCES " + TABLE_RECIPE + "(" + COLUMN_RECIPE_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY(" + COLUMN_INGREDIENT_ID + ") REFERENCES " + TABLE_INGREDIENT + "(" + COLUMN_INGREDIENT_ID + ") ON DELETE CASCADE);";

        String createUserRecipeActionTable = "CREATE TABLE " + TABLE_USER_RECIPE_ACTION + " (" +
                COLUMN_ACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " INTEGER, " +
                COLUMN_RECIPE_ID + " INTEGER, " +
                COLUMN_ACTION_TYPE + " TEXT CHECK(" + COLUMN_ACTION_TYPE + " IN ('like', 'collect')) NOT NULL, " +
                COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY(" + COLUMN_RECIPE_ID + ") REFERENCES " + TABLE_RECIPE + "(" + COLUMN_RECIPE_ID + ") ON DELETE CASCADE);";

        db.execSQL(createUsersTable);
        db.execSQL(createRecipeTable);
        db.execSQL(createIngredientTable);
        db.execSQL(createRecipeIngredientTable);
        db.execSQL(createUserRecipeActionTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_RECIPE_ACTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Insert a new recipe
    public long insertRecipe(long userId, String recipeName, String description, String cuisine,
                             int timeTaken, int servings, int calories, String difficulty, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FK, userId);
        values.put(COLUMN_RECIPE_NAME, recipeName);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_CUISINE, cuisine);
        values.put(COLUMN_TIME_TAKEN, timeTaken);
        values.put(COLUMN_SERVINGS, servings);
        values.put(COLUMN_CALORIES, calories);
        values.put(COLUMN_DIFFICULTY, difficulty);
        values.put(COLUMN_IMAGE, image);
        return db.insert(TABLE_RECIPE, null, values);
    }

    // Insert a new ingredient
    public long insertIngredient(String ingredientName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INGREDIENT_NAME, ingredientName);
        return db.insert(TABLE_INGREDIENT, null, values);
    }

    // Insert into RecipeIngredient
    public long insertRecipeIngredient(long recipeId, long ingredientId, String quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RECIPE_ID, recipeId);
        values.put(COLUMN_INGREDIENT_ID, ingredientId);
        values.put(COLUMN_QUANTITY, quantity);
        return db.insert(TABLE_RECIPE_INGREDIENT, null, values);
    }

    // Insert user action on a recipe (like or collect)
    public long insertUserRecipeAction(long userId, long recipeId, String actionType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_RECIPE_ID, recipeId);
        values.put(COLUMN_ACTION_TYPE, actionType);
        return db.insert(TABLE_USER_RECIPE_ACTION, null, values);
    }
}
