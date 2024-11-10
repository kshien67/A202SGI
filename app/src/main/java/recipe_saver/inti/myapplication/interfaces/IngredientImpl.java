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

    public String getCategory() {
        return category;
    }

    // Function to parse ingredients and convert units between metric and imperial
    public void parseIngredients(String instructions, String type) {
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
            String unit = matcher.group(2);
            String ingredient = matcher.group(3);

            // Convert amount to double for calculation
            double amount = Double.parseDouble(amountStr);

            // Conversion logic
            if (Objects.nonNull(type) && type.equalsIgnoreCase("metric")) {
                if (Objects.nonNull(unit) && unit.equalsIgnoreCase("tbsp")) {
                    amount = Math.round(amount * 14.7868); // Convert tablespoon to ml and round up
                    unit = "ml";
                } else if (unit.equalsIgnoreCase("cup")) {
                    amount = Math.round(amount * 240); // Convert cup to ml and round up
                    unit = "ml";
                } else if (unit.equalsIgnoreCase("oz")) {
                    amount = Math.round(amount * 28.3495); // Convert ounce to grams and round up
                    unit = "g";
                } else if (unit.equalsIgnoreCase("lb")) {
                    amount = Math.round(amount * 453.592); // Convert pound to grams and round up
                    unit = "g";
                }
            } else if (Objects.nonNull(type) && type.equalsIgnoreCase("imperial")) {
                if (Objects.nonNull(unit) && unit.equalsIgnoreCase("ml")) {
                    if (amount >= 240) {
                        amount = Math.round(amount / 240); // Convert ml to cup if amount is larger than 240 ml and round up
                        unit = "cup";
                    } else {
                        amount = Math.round(amount / 14.7868); // Convert ml to tablespoon and round up
                        unit = "tbsp";
                    }
                } else if (unit.equalsIgnoreCase("g")) {
                    if (amount >= 453.592) {
                        amount = Math.round(amount / 453.592); // Convert grams to pounds if amount is larger than 453.592 g and round up
                        unit = "lb";
                    } else {
                        amount = Math.round(amount / 28.3495); // Convert grams to ounces and round up
                        unit = "oz";
                    }
                }
            }

            // Append the converted ingredient
            parsedInstructions.append("[[").append(amount).append("|").append(unit).append("|").append(ingredient).append("]]");

            lastMatchEnd = matcher.end();
        }

        // Append remaining text after the last match
        parsedInstructions.append(instructions.substring(lastMatchEnd));

        // Print or use the parsed instructions
        System.out.println(parsedInstructions);
    }
}
