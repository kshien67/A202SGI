package recipe_saver.inti.myapplication.interfaces;

public interface MeasurementUnit {
    double toGrams();
    double toMillilitres();
    double fromGrams(double grams);
    double fromGrams(double grams, String unit);
    double fromMillilitres(double millilitres);
    double fromMillilitres(double millilitres, String unit);
    String getDefaultUnit();
    static String[] getUnits() {
        return new String[] {"g", "kg", "lb", "oz", "ml", "l", "tbsp", "tsp", "cup", "pcs"};
    }
}
