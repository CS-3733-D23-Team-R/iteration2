package edu.wpi.teamR.datahandling;

import edu.wpi.teamR.requestdb.AvailableItem;
import edu.wpi.teamR.requestdb.ItemRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class ShoppingCart {
    public HashMap<AvailableItem, Integer> items = new HashMap<AvailableItem, Integer>();

    private static ShoppingCart instance;



    public ShoppingCart() {}

    public static ShoppingCart getInstance(){
        if(instance == null){
            instance = new ShoppingCart();
        }
        return instance;
    }

    public void deleteCartInstance(){
        instance = null;
    }

    public void addItem(AvailableItem item, int quantity){
        if(!items.containsKey(item)){
            items.putIfAbsent(item, quantity);
        } else {incrementItem(item);}
    }
    public void incrementItem(AvailableItem item){
        items.replace(item, items.get(item) + 1);
    }
    public void deleteItem(AvailableItem item){
        items.remove(item);
    }
    public void decrementItem(AvailableItem item) {
        items.replace(item, items.get(item) - 1);
    }
    public void clearCart(){ items.clear(); }

    public int getItemQuantity(AvailableItem item){
        return items.get(item);
    }


    //returns the total price of all items in the items shopping cart
    public double calculateTotal() {
        Set<AvailableItem> itemsSet = items.keySet();
        double total = 0;
        for (AvailableItem i : itemsSet) {
            total += i.getItemPrice() * items.get(i);
        }
        return total;
    }
}
