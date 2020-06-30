package com.org;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Karan tripathi
 * @date 30/06/20
 *
 *
 * This Class deals with all the operations related to the
 * ingredient stocks. Currently the stocks are being maintained by
 * a in-memory HashMap {@link #stock}.
 */

public class Stock {

    private Map<String, Integer> stock;

    public Stock(){
        // Used ConcurrentHashMap's since they are thread-safe
        stock = new ConcurrentHashMap<String, Integer>();
    }

    public Map<String, Integer> getStock() {
        return stock;
    }

    /**
     * This function is used to update the quantity of different ingredients
     * ingredient quantities are maintained in the {@link #stock} Map.
     *
     * The same function is also called to *refill* the ingredients, in which case
     * the ingredient quantities are increased by the given amount.
     *
     * @param ingredients ingredients and their quantities to be added, represented by HashMap
     */
    public void updateIngredients(Map<String, Integer> ingredients) {
        Map<String, Integer> invalidInputs = new HashMap<>();

        for (Map.Entry<String, Integer> e : ingredients.entrySet()) {
            String ingredient = e.getKey();
            Integer quantity = e.getValue();
            if(quantity>0) {
                stock.put(ingredient, stock.getOrDefault(ingredient, 0) + quantity);
            } else {
                invalidInputs.put(ingredient, quantity);
            }
        }
        if(invalidInputs.size() > 0 ){
            System.out.println("INVALID INGREDIENT QUANTITIES : -");
            invalidInputs.entrySet().forEach(e -> System.out.println(e.getKey()+" : "+e.getValue()));
        }
    }

    /**
     * This function is used to consume stocks for a given recipe.
     * It is only called for recipes which are considered valid by
     * {@link #validateRecipe(Map)} function.
     * @param recipe recipe of a beverage
     */
    private void consumeStock(Map<String, Integer> recipe) {
        for (Map.Entry<String, Integer> e : recipe.entrySet()) {
            String ingredient = e.getKey();
            Integer quantity = e.getValue();
            stock.put(ingredient, stock.get(ingredient) - quantity);
        }
    }

    /**
     * This function is used to validate the given recipe based
     * on the required quantities of the ingredients.
     * If the stock of any required ingredient < quantity required in recipe
     * the recipe is considered to be invalid
     * OR
     * quantity required in recipe < 0
     *
     * This function is dealing with the current state of the stocks and consumption
     * the stocks. Thus to make it thread safe *synchronized* is used here.
     *
     * {@link #consumeStock(Map)} is called iff the given recipe is valid for stock consumption.
     *
     * @param recipe {@link Map} of the ingredients and there required quantity
     * @return {@link List} of all the insufficient ingredients.
     */
    public synchronized List<String> validateRecipe(Map<String, Integer> recipe) {
        List<String> insufficientIngredients = new ArrayList<>();
        for (Map.Entry<String, Integer> e : recipe.entrySet()) {
            String ingredient = e.getKey();
            Integer requiredQuantity = e.getValue();
            if (stock.getOrDefault(ingredient, 0) < requiredQuantity || requiredQuantity<0) {
                insufficientIngredients.add(ingredient);
            }
        }
        if(insufficientIngredients.size()==0){
            consumeStock(recipe);
        }
        return insufficientIngredients;
    }
}
