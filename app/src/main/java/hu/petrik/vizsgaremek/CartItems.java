package hu.petrik.vizsgaremek;


import java.util.List;

public class CartItems {
    public String id;
    public int total;
    public int quantity;
    public Menu menuItem;
}
class CartItemListHelper {
    private List<CartItems> shoppingCart;
    private String sumTotal;


    public List<CartItems> getshoppingCart() {
        return shoppingCart;
    }
    public String getSumTotal() {
        return sumTotal;
    }
}


