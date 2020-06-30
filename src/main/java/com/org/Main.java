package com.org;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // create object mapper instance
        ObjectMapper mapper = new ObjectMapper();

        List<JsonNode> testList = null;
        try {
            // convert JSON array to list of JsonNode
            testList = Arrays.asList(mapper.readValue(Paths.get("src/test.json").toFile(), JsonNode[].class));
        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
        }
        // Parsing the input json and executing the respective operation
        if(testList != null && testList.size() > 0) {
            for(JsonNode test : testList) {
                Iterator<Map.Entry<String, JsonNode>> itr = test.get("machine").fields();
                Machine machine = null;
                while(itr.hasNext()) {
                    Map.Entry<String, JsonNode> entry = itr.next();
                    String key = entry.getKey();
                    JsonNode json = entry.getValue();
                    switch (key) {
                        case "outlets":
                            if(machine!=null) {
                                System.out.println("Machine object already exists.");
                                return;
                            } else {
                                int outlets = json.get("count_n").asInt();
                                machine = Machine.getInstance(outlets);
                            }
                            break;
                        case "refill":
                            if(machine==null) {
                                System.out.println("Machine object not exists.");
                                return;
                            } else {
                                Map<String, Integer> ingredients = (Map<String, Integer>) mapper.convertValue(json, Map.class);
                                machine.refill(ingredients);
                            }
                            break;
                        case "total_items_quantity":
                            if(machine==null) {
                                System.out.println("Machine object not exists.");
                                return;
                            } else {
                                Map<String, Integer> ingredients = (Map<String, Integer>) mapper.convertValue(json, Map.class);
                                machine.initiateStocks(ingredients);
                            }
                            break;
                        case "beverages" :
                            if(machine==null) {
                                System.out.println("Machine object not exists.");
                                return;
                            } else {
                                Map<String, Map<String, Integer>> orders = (Map<String, Map<String, Integer>>) mapper.convertValue(json, Map.class);
                                machine.takeOrders(orders);
                            }
                            break;
                        case "stock_status" :
                            if(machine==null) {
                                System.out.println("Machine object not exists.");
                                return;
                            } else {
                                machine.stockStatus();
                            }
                            break;
                        default:
                            System.out.println("Invalid Input");
                            break;
                    }
                }
                System.out.println("=============");
            }
        }
        return;
    }
}
