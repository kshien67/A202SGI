package recipe_saver.inti.myapplication.interfaces;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Objects;

public class IngredientImpl {
    private final String ingredient_id;
    private final String ingredient_name;
    private final String category;

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

    // Function to parse ingredients and convert units between metric and imperial
    public String parseIngredients(String instructions, String type) {
        // Regular expression to identify ingredient format [[amount|unit|ingredient]]
        Pattern pattern = Pattern.compile("\\[\\[(\\d+)\\|(\\w+)\\|(\\w+)]]");
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
                switch (unit.toLowerCase()) {
                    case "tbsp":
                        normalFormat = amount + " tablespoons of " + ingredient + " (" + Math.round(amount * 15) + " grams)";
                        break;
                    case "cup":
                        normalFormat = amount + " cups of " + ingredient + " (" + Math.round(amount * 240) + " ml)";
                        break;
                    case "oz":
                        normalFormat = amount + " ounces of " + ingredient + " (" + Math.round(amount * 28.3495) + " grams)";
                        break;
                    case "lb":
                        normalFormat = amount + " pounds of " + ingredient + " (" + Math.round(amount * 453.592) + " grams)";
                        break;
                    case "ml":
                        normalFormat = amount + " milliliters of " + ingredient;
                        break;
                    case "g":
                        normalFormat = amount + " grams of " + ingredient;
                        break;
                    case "pcs":
                        normalFormat = amount + " pieces of " + ingredient;
                        break;
                    default:
                        normalFormat = amount + " " + unit + " of " + ingredient;
                        break;
                }
            }

            // Append the converted ingredient in the desired format
            parsedInstructions.append(normalFormat);

            lastMatchEnd = matcher.end();
        }

        // Append remaining text after the last match
        parsedInstructions.append(instructions.substring(lastMatchEnd));

        // Return the parsed instructions
        return parsedInstructions.toString();
    }

    public static void main(String[] args) {
        IngredientImpl ingredient = new IngredientImpl("Butter", "Dairy");
        String parsed = ingredient.parseIngredients("1. You'll need [[2|tbsp|Butter]]. Add one tablespoon in a large skillet, and scramble [[2|pcs|Egg]] in the skillet.\n2. Remove the eggs, and add the other tablespoon of butter. Add [[1|pcs|Carrot]] and [[1|pcs|Onion]] into the skillet and cook for 4 minutes.\n3. Add [[1|cup|Rice]] and saute for 4 minutes.\n4. Add the scrambled eggs back, and stir in [[20|ml|Soybean Oil]] and [[15|ml|Olive Oil]] for 2 minutes.", "metric");
        System.out.println(parsed);
    }
}
