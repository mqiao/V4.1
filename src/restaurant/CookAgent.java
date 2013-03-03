package restaurant;

import agent.Agent;
import java.util.*;

import restaurant.BillFromMarket.MarketBillStatus;
import restaurant.FoodRequest.RequestStatus;
import restaurant.layoutGUI.*;
import java.awt.Color;


/** Cook agent for restaurant.
 *  Keeps a list of orders for waiters
 *  and simulates cooking them.
 *  Interacts with waiters only.
 */
public class CookAgent extends Agent {

		
    //List of all the orders
    private List<Order> orders = new ArrayList<Order>();
    private Map<String,FoodData> inventory = new HashMap<String,FoodData>();
    public enum Status {pending, cooking, done}; // order status

    //Name of the cook
    private String name;

    //Timer for simulation
    Timer timer = new Timer();
    Restaurant restaurant; //Gui layout

    /** Constructor for CookAgent class
     * @param name name of the cook
     */
    public CookAgent(String name, Restaurant restaurant) {
	super();

	this.name = name;
	this.restaurant = restaurant;
	//Create the restaurant's inventory.
	addAFood("Steak", 5,5);
	addAFood("Chicken", 4,5);
	addAFood("Pizza", 3,5);
	addAFood("Salad", 2,5);
	for(int i=0;i<5;i++)
	{
		MarketAgent tempMarket=new MarketAgent("Market"+i);
		tempMarket.startThread();
		markets.add(tempMarket);
	}


    }
    
    /** Private class to store information about food.
     *  Contains the food type, its cooking time, and ...
     */
    
	private class FoodData{
		String type; //FoodType;		
		double cookTime;
		int amount;		//from here to botom are added in V4.1
		int max;
		int threshold; // when amount< threshold, cook needs to order from marker
		FoodStatus status;

		public FoodData(String type, double cookTime,int max){//threshold==max*0.3
			this.type = type;
			this.cookTime = cookTime;
			this.amount=max;
			this.max=max;
			this.threshold=2;
			this.status=FoodStatus.ENOUGH;
		}
    }
		public enum FoodStatus {ENOUGH, LOW,LOW_AND_ORDERED};	//added in V4.1

    /** Private class to store order information.
     *  Contains the waiter, table number, food item,
     *  cooktime and status.
     */
//    private class Order {
//	public WaiterAgent waiter;
//	public int tableNum;
//	public String choice;
//	public Status status;
//	public Food food; //a gui variable
//
//	
//	/** Constructor for Order class 
//	 * @param waiter waiter that this order belongs to
//	 * @param tableNum identification number for the table
//	 * @param choice type of food to be cooked 
//	 */
//	public Order(WaiterAgent waiter, int tableNum, String choice){
//	    this.waiter = waiter;
//	    this.choice = choice;
//	    this.tableNum = tableNum;
//	    this.status = Status.pending;
//	}
//
//	/** Represents the object as a string */
//	public String toString(){
//	    return choice + " for " + waiter ;
//	}
//    }
    /***add an item to the inventory****/
    public void addAFood(String name, double cookTime, int max){
    	inventory.put(name,new FoodData(name, cookTime,max));
    	foodNames.add(name);
    }
    public void addNewFood(String name)
    {
    	print("cook add the new food "+name);
    	addAFood(name,5,5);
    	stateChanged();
    }
    public void updateFoodInfo(String name,int amount, int max, int threshold,int prepTime,ArrayList<Boolean> MKT,Menu menu)
    {
    	print("cook update the food "+name+" to amount: "+amount+" threshold: "+threshold+" max: "+max);
    	inventory.get(name).amount=amount;
    	inventory.get(name).max = max;
    	inventory.get(name).threshold=threshold;
    	for(int i=0;i<5;i++)
    	{ 
    		markets.get(i).updateFoodInfo(menu, name,prepTime,MKT.get(i));
			//System.out.println(MKT.get(i));
    	}
    	checkAvailabilityAtInitialization();
    	stateChanged();
    }
    
    /***newly added in V4.1**/
    List<MarketAgent> markets= new ArrayList<MarketAgent>();
    List<FoodRequest> foodsFromMarket = new ArrayList<FoodRequest>();
//    List<BillFromMarket> bills = new ArrayList<BillFromMarket>();
    List<String> foodNames= new ArrayList<String>();
    int favoriteMarket=0;
    private HostAgent host;
    private CashierAgent cashier;
    
    public void setHost(HostAgent host)
    {
    	this.host=host;
    }
    public void setCashier(CashierAgent cashier)
    {
    	this.cashier=cashier;
    	for(int i=0;i<5;i++)
    	{
    		markets.get(i).cashier=cashier;
    	}
    }
    // *** MESSAGES ***

    /** Message from a waiter giving the cook a new order.
     * @param waiter waiter that the order belongs to
     * @param tableNum identification number for the table
     * @param choice type of food to be cooked
     */
//    public void msgHereIsAnOrder(WaiterAgent waiter, int tableNum, String choice){
//    
//	orders.add(new Order(waiter, tableNum, choice));
//	stateChanged();
//    }
    
    public void msgHereIsAnOrder(Order o)
    {
    	orders.add(o);
    	stateChanged();
    }

    public void msgHereIsFood(FoodRequest foodFromMarket) {
    	foodsFromMarket.add(foodFromMarket);
//    	bill.setStatus((MarketBillStatus.PENDING));
//    	bills.add(bill);// the pending state is set by the sender:market
    	stateChanged();
    }
    
    public void msgWeDontHaveIt(MarketAgent market,String choice)//TODO need to modify the logic here
    {
    	if(favoriteMarket==markets.size()-1)
    		favoriteMarket=0;
    	else
    		favoriteMarket++;
    	print("Cook got the msg we don't have it and set "+choice+" 's status to \"enough\" again");
    	inventory.get(choice).status=FoodStatus.ENOUGH;//but atctually this is not enough so the market would reorder again
    	stateChanged();
    }
    
//    public void msgHereIsMoney(BillFromMarket bill)
//    {
//    	bill.status=MarketBillStatus.WAITINT_FOR_DELIVERY;
//    	bills.add(bill);	
//    	stateChanged();
//    }

    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
	

//    //if there exists a billFromMarket b whose status is pending, askCashierToPayBill
//    	for(BillFromMarket b:bills)
//    	{
//    		if(b.status==MarketBillStatus.PENDING)
//    		{
//    			askCashierToPayBill(b);
//    			return true;
//    		}
//    	}
//
//    //if there exist a billFromMarket b whose status is waitingForDelivery, deliver bill to market
//    	for(BillFromMarket b:bills)
//    	{
//    		if(b.status==MarketBillStatus.WAITINT_FOR_DELIVERY)
//    		{
//    			deliverBillToMarket(b);
//    			return true;
//    		}
//    	}
    	
    	
      //if there exists a food in inventory whose status is low, ask market for a food
    	for(String s:foodNames)
    	{
    		if(inventory.get(s).amount<=inventory.get(s).threshold && inventory.get(s).status==FoodStatus.ENOUGH )
    		{
    			inventory.get(s).status=FoodStatus.LOW;
    			orderFoodFromMarket(inventory.get(s));
    		   	return true;
    		}
    	}
    //if there exists an element in foodsFromMarket , addFood(f)
       for(FoodRequest f:foodsFromMarket)
        {
        	addFood(f);
        	return true;
        }
       
//     //if there exists a food in inventory whose status is low, ask market for a food
//       for(String f:foodNames)
//       {
//    	   if(inventory.get(f).status==FoodStatus.LOW)
//    	   {
//    		   	orderFoodFromMarket(inventory.get(f));
//    		   	return true;
//    	   }
//       }
	//If there exists an order o whose status is done, place o.
	for(Order o:orders){
	    if(o.status == Status.done){
	    	placeOrder(o);
	    	return true;
	    }
	}
	//if there exists an order o whose status is pending and we do not have the food, return the order to customer
	
	for(Order o:orders){
		if(o.status==Status.pending && inventory.get(o.choice).amount==0)
		{
			returnOrder(o);
			return true;
		}
	}

	//If there exists an order o whose status is pending, cook o.
	for(Order o:orders){
	    if(o.status == Status.pending && inventory.get(o.choice).amount>0){
	    	cookOrder(o);
	    	return true;
	    }
	}

	//we have tried all our rules (in this case only one) and found
	//nothing to do. So return false to main loop of abstract agent
	//and wait.
	return false;
    }
    

    // *** ACTIONS ***
    
    /** Starts a timer for the order that needs to be cooked. 
     * @param order
     */
    private void cookOrder(Order order){
		inventory.get(order.choice).amount--;
		print("cook: have "+inventory.get(order.choice).amount+" "+order.choice+" left");
    	DoCooking(order);
    	order.status = Status.cooking;
    }

    private void placeOrder(Order order){
    	DoPlacement(order);
    	order.waiter.msgOrderIsReady(order.tableNum, order.food);
    	removeOrder(order);
    }
    
//    private void askCashierToPayBill(BillFromMarket bill){
//    	bill.status=MarketBillStatus.HANDLING_BY_CASHIER;
//    	print("Cook: give cashier the bill from market.");
//    	cashier. msgBillOfFood(bill);
//    	print("Cook: tell host to add the unavailable food back");
//    	host.msgAddItemsToMenu(bill.getChoice());
//    	removeBill(bill);
//    	stateChanged();
//    }
    
//    private void deliverBillToMarket(BillFromMarket bill){
//    	print("Cook: give the money along with the bill to the market");
//    	bill.market.msgHereIsMoney(bill);
//    	removeBill(bill);
//    	stateChanged();
//    }
    private void addFood(FoodRequest f)
    {
    	print("Cook: add "+f.amount+" "+f.food+" to inventory");
    	inventory.get(f.food).amount+=f.amount;
    	for(FoodRequest food:foodsFromMarket)
    	{
    		if(food.equals(f))
    		{
    			foodsFromMarket.remove(food);
    	    	stateChanged();
    	    	return;
    		}
    	}
    }
    private void orderFoodFromMarket(FoodData food){

    	FoodRequest foodRequest= new FoodRequest(this,food.type,food.max-food.amount);
    	print("Cook: tell market "+favoriteMarket+" to give me "+(food.max-food.amount)+" "+food.type);
    	markets.get(favoriteMarket).msgOrderFood(foodRequest);
    	food.status=FoodStatus.LOW_AND_ORDERED;
    	stateChanged();

    }
    private void returnOrder(Order o)
    {
    	print("Cook: tell waiter "+o.waiter.getName()+" to reorder for "+o.tableNum+" because "+o.choice+" is out of stock");
    	o.waiter.msgRunningOutOfFood(o);
    	print("Cook: tell host to make "+o.choice+" unavailable from the menu because it is out of stock");
    	host.msgRunningOutOfFood(o.choice);
    	removeOrder(o);
    	stateChanged();
    }
    
    public int getAmount(String name)
    {
    	return(inventory.get(name).amount);
    }
    public int getMax(String name)
    {
    	return(inventory.get(name).max);
    }
    public int getThreshold(String name)
    {
    	return(inventory.get(name).threshold);
    }
    public int getPrepTime(String name)
    {
    	for(MarketAgent m:markets)
    	{
    		if (m.ifHas(name))
    			return (int) (m.storage.get(name).preparingTime/1000);
    			
    	}
    	return -1;
    }
    public boolean getMarketAvailability(int i,String foodName)
    {
    	return(markets.get(i).ifHas(foodName));
    }
    // *** EXTRA -- all the simulation routines***

    /** Returns the name of the cook */
    public String getName(){
        return name;
    }

    private void DoCooking(final Order order){
	print("Cooking:" + order + " for table:" + (order.tableNum+1));
	
	//put it on the grill. gui stuff
	order.food = new Food(order.choice.substring(0,2),new Color(0,255,255), restaurant);
	order.food.cookFood();

	timer.schedule(new TimerTask(){
		FoodData thisFood = inventory.get(order.choice);
	    public void run(){//this routine is like a message reception    
		order.status = Status.done;
//		if(thisFood.amount<=thisFood.threshold && thisFood.status==FoodStatus.ENOUGH )
//		{
//			inventory.get(order.choice).status=FoodStatus.LOW;
//		}
			stateChanged();
	    }
	}, (int)(inventory.get(order.choice).cookTime*1000));
    }
    
    public void DoPlacement(Order order){
    	print("Order finished: " + order + " for table:" + (order.tableNum+1));
    	order.food.placeOnCounter();
    }
    
    private void removeOrder(Order order)
    {
    	for(Order o:orders)
    	{
    		if(o.equals(order))
    		{
    			orders.remove(o);
    			return;
    		}
    	}
    }
    
    private void checkAvailabilityAtInitialization()
    {
    	for(String s: foodNames)
    	{
    		if(inventory.get(s).amount==0)
    		{
    			host.msgRunningOutOfFood(s);
    		}
    	}
    }
//    
//    private void removeBill(BillFromMarket bill)
//    {
//    	for(BillFromMarket b:bills)
//    	{
//    		if(b.equals(bill))
//    		{
//    			bills.remove(b);
//    			return;
//    		}
//    	}
//    }
}


    
