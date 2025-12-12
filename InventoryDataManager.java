import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventoryDataManager {

    private static final String FILE_NAME = "inventory.json";

    // Load data from JSON file
    public static List<Ingredient> loadInventory() {
        List<Ingredient> list = new ArrayList<>();
        File file = new File(FILE_NAME);

        // If file doesn't exist, create default data
        if (!file.exists()) {
            System.out.println("No database found. Creating default inventory.");
            list.add(new Ingredient("Eggs", "Poultry", 6, 10, "pcs", LocalDate.of(2025, 12, 1)));
            list.add(new Ingredient("Milk", "Dairy", 12, 10, "L", LocalDate.of(2025, 12, 5)));
            list.add(new Ingredient("Tomatoes", "Vegetable", 5, 10, "kg", LocalDate.of(2025, 11, 25)));
            saveInventory(list); 
            return list;
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(FILE_NAME)));
            
            // Basic check to ensure content isn't just "[]" or empty
            if (content.length() > 2) {
                // Remove outer brackets [ ]
                content = content.substring(1, content.length() - 1); 
                
                // FIXED: Split by "}, {" allowing for newlines or spaces in between
                // The regex "},\\s*\\{" matches "}," followed by any whitespace/newline, followed by "{"
                String[] items = content.split("},\\s*\\{");
                
                for (String item : items) {
                    // Because split consumes the brackets, we might need to add them back
                    if (!item.startsWith("{")) item = "{" + item;
                    if (!item.endsWith("}")) item = item + "}";
                    
                    Ingredient ing = Ingredient.fromJSON(item);
                    if (ing != null) list.add(ing);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return list;
    }

    // Save data to JSON file
    public static void saveInventory(List<Ingredient> inventory) {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < inventory.size(); i++) {
            json.append(inventory.get(i).toJSON());
            // Add comma and newline if not the last item
            if (i < inventory.size() - 1) {
                json.append(", \n");
            }
        }
        json.append("\n]");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(json.toString());
            System.out.println("Inventory saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}