import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Ingredient {
    private String name;
    private String type; // This acts as the "Category"
    private int quantity;
    private int minLevel;
    private String unit;
    private LocalDate expiryDate; 

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
        return expiryDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
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
}