# Coffee Machine Application

To build this application, you will need the correct version of Java and a build tool. This project uses maven.

Prerequisites include:

* Java Software Developer's Kit (SE) 1.8 or higher
* maven

To check your Java version, enter the following in a command window:

`java -version`

To check your maven version, enter the following in a command window:

`mvn -v`

If you do not have the required versions, follow these links to obtain them:

* [Java SE](https://openjdk.java.net/install/)
* [maven](https://maven.apache.org/download.cgi)

## Build and run the project

This example Play project was created from a seed template. It includes all Play components and an Akka HTTP server. The project is also configured with filters for Cross-Site Request Forgery (CSRF) protection and security headers.

To build and run the project:

1. Use a command window to change into the example project directory, for example: `cd karan_tripathi`

2. Build & Run the project. Enter: `sh run.sh`. The project builds and starts the main() function in Main.Java . Since this downloads libraries and dependencies, the amount of time required depends partly on your connection's speed.

## Walkthrough

Following operations are supported by the application 
* `outlets` 
Used to initiate a new machineInstance. `count_n` is a mandatory key in outlets.  
* `refill` Used to refill the stock quantities, Once the refill is done machine will again attempt to brew the pending orders.
* `total_items_quantity` used to initiate the stocks to a specific level
* `beverages` used to take beverage orders
* `status` Used to fetch machine's current stock status

All the test cases are present in `test.json`

### [GitHub repo](https://github.com/Chr0nicl3/coffee_machine)