import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Ingredient {
    private String name;
    private String type;
    private int quantity;
    private int minLevel;
    private String unit;
    private LocalDate expiryDate; 

    // Date Formatter for consistency
    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public Ingredient(String name, String type, int quantity, int minLevel, String unit, LocalDate expiryDate) {
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.minLevel = minLevel;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public int getQuantity() { return quantity; }
    public int getMinLevel() { return minLevel; }
    public String getUnit() { return unit; }
    public LocalDate getExpiryDate() { return expiryDate; } 

    // Helper for table display
    public String getFormattedDate() {
        return expiryDate.format(DATE_FMT);
    }

    public String getStatus() {
        if (quantity == 0) return "Out of Stock";
        LocalDate now = LocalDate.now();
        if (expiryDate.isBefore(now)) return "Expired";
        else if (!expiryDate.isBefore(now) && expiryDate.isBefore(now.plusDays(7))) return "Expiring Soon";
        else if (quantity <= minLevel) return "Low Stock";
        else return "Good Stock";
    }

    // Setters
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setMinLevel(int minLevel) { this.minLevel = minLevel; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    // Helpers for Reports
    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }

    public boolean isExpiringSoon() {
        LocalDate now = LocalDate.now();
        return !expiryDate.isBefore(now) && expiryDate.isBefore(now.plusDays(7));
    }

    // --- NEW: Convert object to JSON String ---
    public String toJSON() {
        return String.format(
            "{\"name\":\"%s\", \"type\":\"%s\", \"quantity\":%d, \"minLevel\":%d, \"unit\":\"%s\", \"expiryDate\":\"%s\"}",
            name, type, quantity, minLevel, unit, getFormattedDate()
        );
    }

    // --- NEW: Create object from JSON String (Simple Parser) ---
    public static Ingredient fromJSON(String json) {
        // Simple manual parsing to avoid external libraries
        // Expected format: {"name":"Val", "type":"Val", ...}
        
        try {
            String content = json.replace("{", "").replace("}", "").replace("\"", "");
            String[] pairs = content.split(",");
            
            String n = "", t = "", u = "", d = "";
            int q = 0, m = 0;

            for (String pair : pairs) {
                String[] kv = pair.split(":");
                String key = kv[0].trim();
                String val = kv[1].trim();

                switch (key) {
                    case "name": n = val; break;
                    case "type": t = val; break;
                    case "quantity": q = Integer.parseInt(val); break;
                    case "minLevel": m = Integer.parseInt(val); break;
                    case "unit": u = val; break;
                    case "expiryDate": d = val; break;
                }
            }
            return new Ingredient(n, t, q, m, u, LocalDate.parse(d, DATE_FMT));
        } catch (Exception e) {
            System.err.println("Error parsing ingredient: " + json);
            return null;
        }
    }
}