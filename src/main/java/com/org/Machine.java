package com.org;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * @author Karan tripathi
 * @date 29/06/20
 *
 * This Class represents the coffee-machine and any operation
 * on the machine is triggered via this class's object.
 * {@link ExecutorService} is used to implement the parallel outlets functionality.
 *
 * Any orders which could not be completed because of insufficient ingredients are
 * saved in {@link #pendingOrders} and are attempted again after the {@code refill} operation
 */
public class Machine {

    private final int outlets;
    private ExecutorService executorService;
    private Stock stock;
    Map<String, Map<String, Integer>> pendingOrders;

    // private constructor to enforce the use of factory(getInstance()) method
    private Machine(int outlets) {
        this.outlets = outlets;
        // using synchronized because normal HashMaps aren't thread safe
        stock = new Stock();
        pendingOrders = new HashMap<>();
    }

    public static Machine getInstance(int outlets) {
        Machine instance = null;
        // A machine with outlets<=0 is useless, hence outlets>0
        if (outlets > 0) {
            instance = new Machine(outlets);
        } else {
            System.out.println("Invalid machine object initiation. outlets must be > 0");
        }
        return instance;
    }

    /**
     * See {@link Stock#updateIngredients(Map)}
     * @param ingredients
     */
    public void initiateStocks(Map<String, Integer> ingredients) {
        stock.updateIngredients(ingredients);
    }

    /**
     * First, Refill the ingredient stocks. See {@link Stock#updateIngredients(Map)}
     * Second, Start brewing of the pending orders.
     * @param ingredients
     */
    public void refill(Map<String, Integer> ingredients) {
        System.out.println("REFILL IN PROGRESS : ");
        stock.updateIngredients(ingredients);
        // Need to wait for refill if any of the brewing operations is still in progress
        // This will never be an infinte waiting, since termination timeout is defined to be 30ms.
        while(!executorService.isTerminated()) {
            //wait
        }
        if(pendingOrders.size()>0) {
            takeOrders(pendingOrders);
        }
    }

    /**
     * This is the main brewing brewing function.
     * First, the recipe checked and iff the recipe is considered valid
     * the required ingredient's quantities are consumed. See {@link Stock#validateRecipe(Map)}
     *
     * Second, Iff it's a valid recipe the brewing begins and the beverage is made
     * else the list of insufficient ingredients is shown.
     *
     * Orders, for which ingredients are insufficient, are saved in {@link #pendingOrders}
     * these orders are completed when the refill operation happens. See {@link #refill(Map)}
     *
     * @param beverage name of the beverage
     * @param recipe recipe of the beverage represented by a HashMap
     */
    private void prepare(String beverage, Map<String, Integer> recipe) {
        List<String> insufficientIngredients = stock.validateRecipe(recipe);
        StringBuilder response = new StringBuilder(beverage);
        if (insufficientIngredients.size() == 0) {
            response.append(" is prepared");
        } else {
            response.append(" cannot be prepared because ");
            response.append(String.join(" and ", insufficientIngredients));
            if (insufficientIngredients.size() == 1) {
                response.append(" is not sufficient");
            } else {
                response.append(" are not sufficient");
            }
            pendingOrders.put(beverage, recipe);
        }
        System.out.println(response);
    }

    /**
     * This function is responsible for taking the input of all the orders and
     * queuing them according the machine capacity(parallelism)
     *
     * @param orderList
     */
    public void takeOrders(Map<String, Map<String, Integer>> orderList) {
        // defining the thread-pool size for parallel servings
        executorService = Executors.newFixedThreadPool(this.outlets);
        for(Map.Entry<String, Map<String, Integer>> order : orderList.entrySet()) {
            final String beverage = order.getKey();
            final Map<String, Integer> recipe = order.getValue();
            executorService.submit(() -> {
                prepare(beverage, recipe);
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(30, TimeUnit.MILLISECONDS);
        }catch (Exception e) {
            System.out.println("Error while executor termination : " + e.getMessage());
        }
    }

    public void stockStatus() {
        Map<String, Integer> stockStatus = stock.getStock();
        System.out.println("STOCK STATUS");
        stockStatus.entrySet().forEach(e -> System.out.println(e.getKey()+" : "+e.getValue()));
    }

}
