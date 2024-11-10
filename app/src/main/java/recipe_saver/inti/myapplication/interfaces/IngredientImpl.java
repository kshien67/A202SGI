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
    // This method is retained for potential future use or extension
    /* This method is retained for potential future use or extension */
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
            if (amountStr == null) {
                continue;
            }
            String unit = matcher.group(2);
            String ingredient = matcher.group(3);

            // Convert amount to double for calculation
            double amount = Double.parseDouble(amountStr);

            // Conversion logic
            if (type != null && type.equalsIgnoreCase("metric")) {
                if (unit != null && unit.equalsIgnoreCase("tbsp")) {
                    amount = Math.round(amount * 14.7868); // Convert tablespoon to ml and round up
                    unit = "ml";
                } else if (unit != null && unit.equalsIgnoreCase("cup")) {
                    amount = Math.round(amount * 240); // Convert cup to ml and round up
                    unit = "ml";
                } else if (unit != null && unit.equalsIgnoreCase("oz")) {
                    amount = Math.round(amount * 28.3495); // Convert ounce to grams and round up
                    unit = "g";
                } else if (unit != null && unit.equalsIgnoreCase("lb")) {
                    amount = Math.round(amount * 453.592); // Convert pound to grams and round up
                    unit = "g";
                }
            } else if (type != null && type.equalsIgnoreCase("imperial")) {
                if (unit != null && unit.equalsIgnoreCase("ml")) {
                    if (amount >= 240) {
                        amount = Math.round(amount / 240); // Convert ml to cup if amount is larger than 240 ml and round up
                        unit = "cup";
                    } else {
                        amount = Math.round(amount / 14.7868); // Convert ml to tablespoon and round up
                        unit = "tbsp";
                    }
                } else if (unit != null && unit.equalsIgnoreCase("g")) {
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

    // Function to convert ingredients to grams, ml, or pieces, multiply by the second value, and return the sum
    public String calculateTotal(String[][] ingredients) {
        double totalGrams = 0;
        double totalMl = 0;
        double totalPcs = 0;

        for (String[] ingredient : ingredients) {
            String amountStr = ingredient[0];
            double multiplier = Double.parseDouble(ingredient[1]);
            double amount;

            if (amountStr.contains("pcs")) {
                String[] parts = amountStr.split(" ");
                int pcs = Integer.parseInt(parts[0]);
                amount = pcs * 50; // Assuming each piece is 50 grams
                totalPcs += amount * multiplier;
            } else if (amountStr.contains("g")) {
                String[] parts = amountStr.split(" ");
                amount = Double.parseDouble(parts[0]);
                totalGrams += amount * multiplier;
            } else if (amountStr.contains("ml")) {
                String[] parts = amountStr.split(" ");
                amount = Double.parseDouble(parts[0]);
                totalMl += amount * multiplier;
            } else if (amountStr.contains("cup")) {
                String[] parts = amountStr.split(" ");
                amount = Double.parseDouble(parts[0]) * 240; // Convert cup to ml
                totalMl += amount * multiplier;
            } else if (amountStr.contains("tbsp")) {
                String[] parts = amountStr.split(" ");
                amount = Double.parseDouble(parts[0]) * 14.7868; // Convert tablespoon to ml
                totalMl += amount * multiplier;
            } else if (amountStr.contains("oz")) {
                String[] parts = amountStr.split(" ");
                amount = Double.parseDouble(parts[0]) * 28.3495; // Convert ounce to grams
                totalGrams += amount * multiplier;
            } else if (amountStr.contains("lb")) {
                String[] parts = amountStr.split(" ");
                amount = Double.parseDouble(parts[0]) * 453.592; // Convert pound to grams
                totalGrams += amount * multiplier;
            }
        }

        return "Total: " + totalGrams + " g, " + totalMl + " ml, " + totalPcs + " pcs";
    }
}
