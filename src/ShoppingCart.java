import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<CartItem> items;

    public ShoppingCart() {
        items = new ArrayList<>();
    }

    @Override
    public String toString() {
        String res = "Cart Items:\n";
        for(CartItem item : items){
            res += item.toString() + "\n";
        }
        res += "__________________________";
        return res;
    }

    public void addItem(String itemName, int quantity, double price) {
        int itemfound = 0;
        for (CartItem item: items){
            if (item.getItemName().equals(itemName)) {
                int newQuantity = item.getQuantity() + quantity;
                item.setQuantity(newQuantity);
                itemfound = 1;
                break;
            }

        }
        if (itemfound == 0){
            CartItem item = new CartItem(itemName, quantity, price);
            items.add(item);
        }
    }

    public void reduceItem(String itemName, int quantity) {
        for (CartItem item : items) {
            if (item.getItemName().equals(itemName)) {
                int newQuantity = item.getQuantity() - quantity;
                if (newQuantity <= 0) {
                    items.remove(item);
                } else {
                    item.setQuantity(newQuantity);
                }
                break;
            }
        }
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getTotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void clear() {
        items.clear();
    }
}
