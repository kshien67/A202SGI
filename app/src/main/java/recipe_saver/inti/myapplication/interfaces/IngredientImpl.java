package recipe_saver.inti.myapplication.interfaces;

import android.util.Log;

import org.json.JSONArray;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IngredientImpl {
    private final String ingredient_id;
    private final String ingredient_name;
    private final String category;

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

    public static String parseCalories(JSONArray ingredients) {
        double totalCalories = 0;
        for (int i = 0; i < ingredients.length(); i++) {
            try {
                JSONArray ingredient = ingredients.getJSONArray(i);
                String unit = ingredient.getString(0).split(" ")[1];
                double amount = Double.parseDouble(ingredient.getString(0).split(" ")[0]);
                double caloriesPerUnit = ingredient.getDouble(1);

                // Conversion logic for calories calculation
                switch (unit.toLowerCase()) {
                    case "tbsp":
                        Log.d("Calories", "Amount: " + amount + " Unit: " + unit + " Calories: " + caloriesPerUnit);
                        Log.d("Calories", "Calories: " + amount * 15 * caloriesPerUnit);
                        totalCalories += amount * 15 * caloriesPerUnit;
                        break;
                    case "cup":
                        Log.d("Calories", "Amount: " + amount + " Unit: " + unit + " Calories: " + caloriesPerUnit);
                        Log.d("Calories", "Calories: " + amount * 240 * caloriesPerUnit);
                        totalCalories += amount * 240 * caloriesPerUnit;
                        break;
                    case "oz":
                        Log.d("Calories", "Amount: " + amount + " Unit: " + unit + " Calories: " + caloriesPerUnit);
                        Log.d("Calories", "Calories: " + amount * 28.3495 * caloriesPerUnit);
                        totalCalories += amount * 28.3495 * caloriesPerUnit;
                        break;
                    case "lb":
                        Log.d("Calories", "Amount: " + amount + " Unit: " + unit + " Calories: " + caloriesPerUnit);
                        Log.d("Calories", "Calories: " + amount * 453.592 * caloriesPerUnit);
                        totalCalories += amount * 453.592 * caloriesPerUnit;
                        break;
                    case "ml":
                        Log.d("Calories", "Amount: " + amount + " Unit: " + unit + " Calories: " + caloriesPerUnit);
                        Log.d("Calories", "Calories: " + amount * caloriesPerUnit);
                        totalCalories += amount * caloriesPerUnit;
                        break;
                    case "g":
                        Log.d("Calories", "Amount: " + amount + " Unit: " + unit + " Calories: " + caloriesPerUnit);
                        Log.d("Calories", "Calories: " + amount * caloriesPerUnit);
                        totalCalories += amount * caloriesPerUnit;
                        break;
                    case "pcs":
                        Log.d("Calories", "Amount: " + amount + " Unit: " + unit + " Calories: " + caloriesPerUnit);
                        Log.d("Calories", "Calories: " + amount * 50 * caloriesPerUnit);
                        totalCalories += amount * 50 * caloriesPerUnit;
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Long.toString(Math.round(totalCalories));
    }

    // Function to parse ingredients and convert units between metric and imperial
    public static String parseIngredients(String instructions, String type) {
        Log.d("Instructions", instructions);
        // Regular expression to identify ingredient format [[amount|unit|ingredient]]
        Pattern pattern = Pattern.compile("\\[\\[(\\d+)\\|(\\w+)\\|([\\w\\s]+)]]");
        Matcher matcher = pattern.matcher(instructions);

        StringBuilder parsedInstructions = new StringBuilder();
        int lastMatchEnd = 0;

        while (matcher.find()) {
            // Append text between matches
            parsedInstructions.append(instructions, lastMatchEnd, matcher.start());

            // Extract the matched parts
            String amountStr = matcher.group(1);
            if (amountStr == null) {
                continue;
            }
            String unit = matcher.group(2);
            String ingredient = matcher.group(3);

            // Convert amount to double for calculation
            double amount = Double.parseDouble(amountStr);

            String normalFormat = "";

            // Conversion logic for outputting normal format
            if (unit != null) {
                Log.d("Unit", unit);
                switch (unit.toLowerCase()) {
                    case "tbsp":
                        normalFormat = convertUnit(amount, "tbsp", ingredient, type);
                        break;
                    case "cup":
                        normalFormat = convertUnit(amount, "cup", ingredient, type);
                        break;
                    case "oz":
                        normalFormat = convertUnit(amount, "oz", ingredient, type);
                        break;
                    case "lb":
                        normalFormat = convertUnit(amount, "lb", ingredient, type);
                        break;
                    case "ml":
                        normalFormat = convertUnit(amount, "ml", ingredient, type);
                        break;
                    case "g":
                        normalFormat = convertUnit(amount, "g", ingredient, type);
                        break;
                    case "pcs":
                        normalFormat = amount + " pieces of " + ingredient;
                        break;
                    default:
                        normalFormat = amount + " " + unit + " of " + ingredient;
                        break;
                }
            }
            Log.d("Normal Format", normalFormat);

            // Append the converted ingredient in the desired format
            parsedInstructions.append(normalFormat);

            lastMatchEnd = matcher.end();
        }

        // Append remaining text after the last match
        parsedInstructions.append(instructions.substring(lastMatchEnd));

        Log.d("Parsed Instructions", parsedInstructions.toString());
        // Return the parsed instructions
        return parsedInstructions.toString();
    }

    private static String convertUnit(double amount, String unit, String ingredient, String type) {
        switch (type) {
            case "Metric":
                switch (unit) {
                    case "tbsp":
                        return Math.round(amount*16.25) + " grams of " + ingredient;
                    case "cup":
                        return Math.round(amount*240) + " ml of " + ingredient;
                    case "oz":
                        return Math.round(amount*28.3495) + " grams of " + ingredient;
                    case "lb":
                        return Math.round(amount*450) + " grams of " + ingredient;
                    case "ml":
                        return amount + " milliliters of " + ingredient;
                    case "g":
                        return amount + " grams of " + ingredient;
                }
                break;
            case "Imperial":
                switch (unit) {
                    case "tbsp":
                        return amount + " tablespoons of " + ingredient;
                    case "cup":
                        return amount + " cups of " + ingredient;
                    case "oz":
                        return amount + " ounces of " + ingredient ;
                    case "lb":
                        return amount + " pounds of " + ingredient;
                    case "ml":
                        return Math.round(amount / 14.25) + "tablespoons  of " + ingredient;
                    case "g":
                        return Math.round(amount / 28.3495) + " ounces of ";
                }
                break;
        }
        return amount + " " + unit + " of " + ingredient;
    }

    /*
    public static void main(String[] args) {
        IngredientImpl ingredient = new IngredientImpl("Butter", "Dairy");
        String parsed = ingredient.parseIngredients("1. You'll need [[2|tbsp|Butter]]. Add one tablespoon in a large skillet, and scramble [[2|pcs|Egg]] in the skillet.\n2. Remove the eggs, and add the other tablespoon of butter. Add [[1|pcs|Carrot]] and [[1|pcs|Onion]] into the skillet and cook for 4 minutes.\n3. Add [[1|cup|Rice]] and saute for 4 minutes.\n4. Add the scrambled eggs back, and stir in [[20|ml|Soybean Oil]] and [[15|ml|Olive Oil]] for 2 minutes.", "metric");
        System.out.println(parsed);
    }
    */
}
