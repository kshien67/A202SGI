package recipe_saver.inti.myapplication.interfaces;

public class MeasurementUnitImpl implements MeasurementUnit {
    private double baseQuantity;
    private String defaultUnit;

    public MeasurementUnitImpl(double quantity, String unit) {
        this.baseQuantity = toGrams(quantity, unit);
        this.defaultUnit = unit;
    }

    @Override
    public double toGrams() {
        return baseQuantity;
    }

    @Override
    public double toMillilitres() {
        return baseQuantity;
    }

    @Override
    public double fromGrams(double grams) {
        return convertFromGrams(grams, defaultUnit);
    }

    @Override
    public double fromGrams(double grams, String unit) {
        return convertFromGrams(grams, unit);
    }

    @Override
    public double fromMillilitres(double millilitres) {
        return convertFromGrams(millilitres, defaultUnit);
    }

    @Override
    public double fromMillilitres(double millilitres, String unit) {
        return convertFromGrams(millilitres, unit);
    }

    @Override
    public String getDefaultUnit() {
        return defaultUnit;
    }

    static String[] getUnits() {
        return new String[] {"g", "kg", "lb", "oz", "ml", "l", "tbsp", "tsp", "cup", "pcs"};
    }

    private double toGrams(double value, String unit) {
        switch (unit.toLowerCase()) {
            case "kg":
                return value * 1000;
            case "lb":
                return value * 453.592;
            case "oz":
                return value * 28.3495;
            case "ml":
                return value; // Assuming 1 ml = 1 gram for water
            case "l":
                return value * 1000; // Assuming 1 litre = 1000 grams for water
            case "tbsp":
                return value * 15;
            case "tsp":
                return value * 5;
            case "cup":
                return value * 240;
            default: // Assume grams if no conversion needed
                return value;
        }
    }

    private double convertFromGrams(double grams, String unit) {
        switch (unit.toLowerCase()) {
            case "kg":
                return grams / 1000;
            case "lb":
                return grams / 453.592;
            case "oz":
                return grams / 28.3495;
            case "ml":
                return grams; // Assuming 1 ml = 1 gram for water
            case "l":
                return grams / 1000; // Assuming 1 litre = 1000 grams for water
            case "tbsp":
                return grams / 15;
            case "tsp":
                return grams / 5;
            case "cup":
                return grams / 240;
            default: // Assume grams if no conversion needed
                return grams;
        }
    }
}