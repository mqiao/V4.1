package restaurant;

import restaurant.CookAgent.Status;
import restaurant.layoutGUI.Food;

public class Order {
public WaiterAgent waiter;
public int tableNum;
public String choice;
public Status status;
public Food food; //a gui variable


/** Constructor for Order class 
 * @param waiter waiter that this order belongs to
 * @param tableNum identification number for the table
 * @param choice type of food to be cooked 
 */
public Order(WaiterAgent waiter, int tableNum, String choice){
    this.waiter = waiter;
    this.choice = choice;
    this.tableNum = tableNum;
    this.status = Status.pending;
}

/** Represents the object as a string */
public String toString(){
    return choice + " for " + waiter ;
}
}