package restaurant;

import restaurant.gui.RestaurantGui;
import restaurant.layoutGUI.*;
import agent.Agent;
import java.util.*;
import java.awt.Color;

/** Restaurant customer agent. 
 * Comes to the restaurant when he/she becomes hungry.
 * Randomly chooses a menu item and simulates eating 
 * when the food arrives. 
 * Interacts with a waiter only */
public class CustomerAgent extends Agent {
    private String name;
    private int hungerLevel = 5;  // Determines length of meal
    private RestaurantGui gui;
    
    // ** Agent connections **
    private HostAgent host;
    private WaiterAgent waiter;
    private CashierAgent cashier;
    Restaurant restaurant;
    private Menu menu;
    Timer timer = new Timer();
    GuiCustomer guiCustomer; //for gui
   // ** Agent state **
    private boolean isHungry = false; //hack for gui
    public enum AgentState
	    {DoingNothing, WaitingInRestaurant, Deciding,SeatedWithMenu, WaiterCalled, WaitingForFood, NeedToReorder,Eating,NeedPaying,PayingAndWaiting,NeedMoney,WaitingForBillWhenDoneEating};
	//{NO_ACTION,NEED_SEATED,NEED_DECIDE,NEED_ORDER,NEED_EAT,NEED_LEAVE};
    private AgentState state = AgentState.DoingNothing;//The start state
    public enum AgentEvent 
	    {gotHungry, askedToDecide, decideToLeave, decideToStay,beingSeated,tooExpensiveAndLeave, decidedChoice, waiterToTakeOrder,reorderFood,getTheBill, foodDelivered, doneEating,payForFood,doneAndLeave,handleChanges,callFriend};
    List<AgentEvent> events = new ArrayList<AgentEvent>();
    //Added in V4.1
    BillFromCustomer bill;
    double change=0;
    double moneyInPocket;
    String potentialOrder;
    boolean ifWait;
    boolean ifReorder=false;
    boolean ifRandom=false;
    /** Constructor for CustomerAgent class 
     * @param name name of the customer
     * @param gui reference to the gui so the customer can send it messages
     */

    public CustomerAgent(String name, Restaurant restaurant) {
	super();
	this.gui = null;
	this.name = name;
	this.restaurant = restaurant;
	guiCustomer = new GuiCustomer(name.substring(0,1), new Color(0,255,0), restaurant);
	
    }

    public CustomerAgent(String name, RestaurantGui gui, Restaurant restaurant,double money, String order, boolean ifWait) {
    	super();
    	this.gui = gui;
    	this.name = name;
    	this.restaurant = restaurant;
    	this.moneyInPocket=money;
    	if(order.equals("Let us decide the order"))
    		ifRandom=true;
    	else
    		this. potentialOrder = order;
    	this.ifWait = ifWait; //TODO
    	guiCustomer = new GuiCustomer(name.substring(0,2), new Color(0,255,0), restaurant);
    	
        }
    public CustomerAgent(String name, RestaurantGui gui, Restaurant restaurant,Menu menu) {
    	super();
    	this.gui = gui;
    	this.name = name;
    	this.restaurant = restaurant;
    	this.menu=menu;
    	generateRandomMoneyInPocket();

    	ifRandom=true;
    	if(Math.random()>0.6)
    		this.ifWait = true; //TODO
    	else
    		this.ifWait = false;
    	guiCustomer = new GuiCustomer(name.substring(0,2), new Color(0,255,0), restaurant);
    	
        }
    // *** MESSAGES ***
    /** Sent from GUI to set the customer as hungry */
    public void setHungry() {
	events.add(AgentEvent.gotHungry);
	isHungry = true;
	print("I'm hungry");
	stateChanged();
    }
    /** Waiter sends this message so the customer knows to sit down 
     * @param waiter the waiter that sent the message
     * @param menu a reference to a menu */
    public void msgFollowMeToTable(WaiterAgent waiter, Menu menu) {
	this.menu = menu;
	this.waiter=waiter;
	//generateRandomMoneyInPocket();
	print("Customer: "+name+" has "+moneyInPocket+" when he enters the restaurant");
	print("Received msgFollowMeToTable from " + waiter);
	events.add(AgentEvent.beingSeated);
	stateChanged();
    }
    /** Waiter sends this message to take the customer's order */
    public void msgDecided(){
	events.add(AgentEvent.decidedChoice);
	stateChanged(); 
    }
    /** Waiter sends this message to take the customer's order */
    public void msgWhatWouldYouLike(){
	events.add(AgentEvent.waiterToTakeOrder);
	stateChanged(); 
    }

    /** Waiter sends this when the food is ready 
     * @param choice the food that is done cooking for the customer to eat */
    public void msgHereIsYourFood(String choice) {
	events.add(AgentEvent.foodDelivered);
	stateChanged();
    }
    /** Timer sends this when the customer has finished eating */
    public void msgDoneEating() {
	events.add(AgentEvent.doneEating);
	stateChanged(); 
    }
    /**Waiter sends this when food is out of stock*/
    public void msgOutOfChoice(Menu menu) {
    	this.menu=menu;
    	events.add(AgentEvent.reorderFood); 
    	stateChanged();
    }
    /**Waiter sends this when the bill is ready*/
    public void msgHereIsBill(BillFromCustomer bill)
    {
    	this.bill=bill;
    	events.add(AgentEvent.getTheBill); 
    	stateChanged();
    }
    /**Cashier sends this when the checkout is done.*/
    public void msgThanksForComing(BillFromCustomer bill,double change)
    {
    	this.change=change; 
    	this.bill=bill;
    	events.add(AgentEvent.doneAndLeave); 
    	stateChanged();
    }
    /**Host sends this when all the tables are full*/
    public void msgTableIsFull() {
    	events.add(AgentEvent.askedToDecide);
    	stateChanged(); 
    }

    /***/
    public void msgNotEnoughPleaseCallFriends(BillFromCustomer bill) {
    	this.bill=bill;
    	events.add(AgentEvent.callFriend);
    	stateChanged();
    }
   
    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
	if (events.isEmpty()) return false;
	AgentEvent event = events.remove(0); //pop first element
	
	//Simple finite state machine
	if(event==AgentEvent.getTheBill)
	{
		
		if(state==AgentState.WaitingForBillWhenDoneEating)
			{
			
				showBillOnTable();
				leaveTableToPay();
				state = AgentState.PayingAndWaiting;
			}
		else
			showBillOnTable();
		return true;
	}
	
	if (state == AgentState.DoingNothing){
	    if (event == AgentEvent.gotHungry)	{
		goingToRestaurant();
		state = AgentState.WaitingInRestaurant;
		return true;
	    }
	    // elseif (event == xxx) {}
	}
	
	if (state == AgentState.WaitingInRestaurant) {
	    if (event == AgentEvent.beingSeated)	{
		makeMenuChoice();
		state = AgentState.SeatedWithMenu;
		return true;
	    }
	    else if(event == AgentEvent.askedToDecide){
	    	decideToStayOrLeave(); 
	    	state=AgentState.Deciding;
	    	return true;
	    }
	}
	
	if(state == AgentState.Deciding)
	{
		if(event==AgentEvent.decideToLeave){
			CantWaitAndLeave();
			state=AgentState.DoingNothing; 
			return true;
			}
		else if(event==AgentEvent.decideToStay) {
			keepWaiting();
			state=AgentState.WaitingInRestaurant; 
			return true;
			}
	}
	
	
	if (state == AgentState.SeatedWithMenu) {
	    if (event == AgentEvent.decidedChoice)	{
	    	callWaiter();
	    	state = AgentState.WaiterCalled;
	    	return true;
	    }
	    else if(event==AgentEvent.tooExpensiveAndLeave)
	    {
	    	tooExpensiveAndLeave();
	    	state=AgentState.DoingNothing;	
	    	return true;
	    }
	}
	if (state == AgentState.WaiterCalled) {
	    if (event == AgentEvent.waiterToTakeOrder)	{
	    	orderFood();
	    	state = AgentState.WaitingForFood;
	    	return true;
	    }
	}
	if (state == AgentState.WaitingForFood) {
	    if (event == AgentEvent.foodDelivered)	{
	    	eatFood();
	    	state = AgentState.Eating;
	    	return true;
	    }
	    else if(event == AgentEvent.reorderFood)
	    {
	    	reorderFood();
	    	state=AgentState.SeatedWithMenu;
	    	return true;
	    }
	}
	if (state == AgentState.Eating) {
	    if (event == AgentEvent.doneEating)	{
	    if(bill!=null)
	    {	leaveTableToPay();
			state = AgentState.PayingAndWaiting;
	    }
	    else
	    {
	    	state=AgentState.WaitingForBillWhenDoneEating;
	    }
		return true;
	    }
	}
	if(state == AgentState.PayingAndWaiting) {
		if(event == AgentEvent.doneAndLeave) {
			doneAndLeave();
			state=AgentState.DoingNothing; 
			return true;
		}
		if(event == AgentEvent.callFriend) {
			callForMoney();
			state=AgentState.NeedMoney;
			return true;
			}
	}
	
	if(state ==AgentState.NeedMoney)
	{
		if(event == AgentEvent.payForFood)
		{ 
			payForFood();	
			state=AgentState.PayingAndWaiting; 
			return true;
		}
	}
	print("No scheduler rule fired, should not happen in FSM, event="+event+" state="+state);
	return false;
    }
    
    
    // *** ACTIONS ***
    
    private void showBillOnTable()
    {
    	print("Customer: "+name+ "'s bill is ready");
    }
    
    private void decideToStayOrLeave()
    {
//    	double decision = Math.random(); 
//    	if (decision<0.5)
//    	{
//    		print("Customer: "+name+ " decide to leave in decideToStayOrLeave");
//    		events.add(AgentEvent.decideToLeave); 
//    	}
//    	else
//    	{	
//    		print("Customer: "+name+ " decide to stay in decideToStayOrLeave");
//    		events.add(AgentEvent.decideToStay);
//    	}
    	if (!ifWait)
    	{
    		print("Customer: "+name+ " decide to leave in decideToStayOrLeave");
    		events.add(AgentEvent.decideToLeave); 
    	}
    	else
    	{	
    		print("Customer: "+name+ " decide to stay in decideToStayOrLeave");
    		events.add(AgentEvent.decideToStay);
    	}
    	stateChanged();
    }
    
    private void CantWaitAndLeave()
    {
		print("Customer: "+name+ " tells the host that he or she will leave");
		
    	host.msgIWillLeave(this);
    	isHungry = false;
    	ifReorder=false;
    	guiCustomer.leave();
    	stateChanged();
    }
    private void keepWaiting()
    {
		print("Customer: "+name+ " tells the host that he or she will stay");
    	host.msgIWillWait(this);
    	stateChanged();
    }
    private void tooExpensiveAndLeave()
    {
		print("Customer: "+name+ " tells the waiter that he or she will leave because he cannot afford anything");
    	waiter.msgTooExpensiveIWillLeave(this);
    	guiCustomer.leave();
    	isHungry=false;
    	ifReorder=false;
    	bill=null;
    	stateChanged();
    }
    private void reorderFood()
    {
    	print("Customer: "+name+ " will reorder food when he is notified that the food is out of stock");
    	this.bill=null;
    	ifReorder=true;
    	events.add(AgentEvent.decidedChoice); 
    	stateChanged();
    }
    
    private void doneAndLeave()
    {
    	this.moneyInPocket+=change;
    	bill=null;
    	isHungry=false;
    	ifReorder=false;
    	print("Customer "+name+ " left the restaturant when he finished paying the bill and got "+change);
    	change=0;
    	stateChanged();
    }
    private void callForMoney()
    {
    	
    	timer.schedule(new TimerTask() {
    		    public void run() {  
    		    	double moneyEarned =bill.getTotalPrice()-bill.getAmountPaid();
    		    	print("getTotalPrice: "+bill.getTotalPrice());
    		    	print("getAmountPaid: "+bill.getAmountPaid());
    		    	moneyEarned= ((double)(int)(moneyEarned*100))/100;
    		    	moneyInPocket = moneyEarned;
    		    	print("Customer "+name+ " called his friend and get "+moneyEarned +" and now have enough money to pay the bill");
    		    	events.add(AgentEvent.payForFood);
    		    	stateChanged();   
    		    }},
    		    3000);//how long to wait before running task
    			
    }
    
    /** Goes to the restaurant when the customer becomes hungry */
    private void goingToRestaurant() {
	print("Going to restaurant");
	guiCustomer.appearInWaitingQueue();
	host.msgIWantToEat(this);//send him our instance, so he can respond to us
	stateChanged();
    }
    
    /** Starts a timer to simulate the customer thinking about the menu */
    private void makeMenuChoice(){
    
    	boolean hasEnoughMoney=false;
    	for(String choice:menu.choices)
    	{
    	    if(menu.getPrice(choice)<=moneyInPocket&&menu.getAvailability(choice))
    	    {
    	    	hasEnoughMoney=true;
    	    	break;
    	    }
    	}	
    	if(!hasEnoughMoney)
    	{
    		print("Customer: "+this.name+" does not have enough money to order anything");
    		events.add(AgentEvent.tooExpensiveAndLeave);
    		stateChanged();
    		return;
    	}
    
    	print("Deciding menu choice...(3000 milliseconds)");
    	timer.schedule(new TimerTask() {
	    public void run() {  
	    	msgDecided();	    
	    }},
	    3000);//how long to wait before running task
    	stateChanged();
    }
    
    private void callWaiter(){
    	print("I decided!");
    	waiter.msgImReadyToOrder(this);
    	stateChanged();
    }

    /** Picks a random choice from the menu and sends it to the waiter */
    private void orderFood(){
    	String choice;
    	if(ifReorder||ifRandom)
    	{
    		//print("Customer "+this.name+" before entering the while loop");
    		choice = menu.choices.get((int)(Math.random()*menu.getSizeOfMenu()));
    		//print("Temp choice is: "+choice);
    		//menu.printAvailableFood();
    		while(moneyInPocket<menu.getPrice(choice)||(!menu.getAvailability(choice)))
    		{
    			choice = menu.choices.get((int)(Math.random()*menu.getSizeOfMenu()));
    			//print("Temp choice is: "+choice);
    		}
    	}
    	else
    	{
    		choice=potentialOrder;
    	}
		print("Ordering the " + choice);
		waiter.msgHereIsMyChoice(this, choice);
		stateChanged();

   }

    /** Starts a timer to simulate eating */
    private void eatFood() {
	print("Eating for " + hungerLevel*1000 + " milliseconds.");
	timer.schedule(new TimerTask() {
	    public void run() {
		msgDoneEating();    
	    }},
	    getHungerLevel() * 1000);//how long to wait before running task
	stateChanged();
    }
    

    /** When the customer is done eating, he leaves the restaurant */
    private void leaveTableToPay() {
    	print("Leaving the table and pay the bill to cashier" );
    	//handle payment
    	double money=moneyInPocket;
    	
    	//money=generateRandomMoneyToPay();

    		
    	this.moneyInPocket=this.moneyInPocket-money;
    	guiCustomer.leave(); //for the animation
    	print("Customer :"+this.name+" tells waiter "+this.waiter.getName()+" to clean the table");
    	waiter.msgDoneEatingAndLeaving(this);
    	print("Customer :"+this.name+" goes to the cashier and pays the bill: "+money);
    	if(bill==null)
    	{
    		print("Customer: bill is null");
    	}
    	else if(cashier==null)
    	{
    		print("Customer: cashier is null");
    	}
    	cashier.msgHereIsTheMoney(this, money, bill);
    	isHungry = false;
    	ifReorder=false;
    	stateChanged();
    	gui.setCustomerEnabled(this); //Message to gui to enable hunger button

	//hack to keep customer getting hungry. Only for non-gui customers
    	if (gui==null) becomeHungryInAWhile();//set a timer to make us hungry.
    }
    
    private void payForFood()
    {
    	//handle payment
    	double money=this.moneyInPocket;   
    	print("Customer :"+this.name+" goes to the cashier and repays the bill");
    	cashier.msgHereIsTheMoney(this, money, bill);
    	this.moneyInPocket=0;
    }
    
    /** This starts a timer so the customer will become hungry again.
     * This is a hack that is used when the GUI is not being used */
    private void becomeHungryInAWhile() {
	timer.schedule(new TimerTask() {
	    public void run() {  
		setHungry();		    
	    }},
	    15000);//how long to wait before running task
    }
    
    // *** EXTRA ***

    /** establish connection to host agent. 
     * @param host reference to the host */
    public void setHost(HostAgent host) {
		this.host = host;
    }
    
    public void setCashier(CashierAgent cashier)
    {
    	this.cashier = cashier;
    }
    /** Returns the customer's name
     *@return name of customer */
    public String getName() {
	return name;
    }

    /** @return true if the customer is hungry, false otherwise.
     ** Customer is hungry from time he is created (or button is
     ** pushed, until he eats and leaves.*/
    public boolean isHungry() {
	return isHungry;
    }

    /** @return the hungerlevel of the customer */
    public int getHungerLevel() {
	return hungerLevel;
    }
    
    /** Sets the customer's hungerlevel to a new value
     * @param hungerLevel the new hungerlevel for the customer */
    public void setHungerLevel(int hungerLevel) {
	this.hungerLevel = hungerLevel; 
    }
    public GuiCustomer getGuiCustomer(){
	return guiCustomer;
    }
    
    /** @return the string representation of the class */
    public String toString() {
	return "customer " + getName();
    }

    private void generateRandomMoneyInPocket()
    {
    	double chance=Math.random();
    	double money=0;
    	if(chance>0.5)
    		{
    			money=(Math.random()*(menu.getHighestPrice()-menu.getLowestPrice()+5))+menu.getLowestPrice();
    		}
    	else
    	{
    		money=Math.random()*(menu.getLowestPrice())+4;
    	}
    	this.moneyInPocket=((int)money*100)/100;
    }
    private void setBill(BillFromCustomer bill)
    {
    	this.bill=bill;
    }
    private double generateRandomMoneyToPay()
    {
    	double money;
    	if(this.moneyInPocket>bill.getTotalPrice()) {
    		money= bill.getTotalPrice()+(this.moneyInPocket-bill.getTotalPrice())*Math.random(); 
    		money=(int)(money+1);
    		}
    		else {
    		money=this.moneyInPocket; 
    		}
    	System.out.println("Pay "+ money+ " for the bill of "+bill.getTotalPrice());
    	return money;
    }
//    public static void main(String[] args)
//    {
////    	CustomerAgent customer=new CustomerAgent("Test",null);
//////    	customer.msgFollowMeToTable(null, new Menu());
//////    	customer.setBill(new BillFromCustomer(null, customer, 12));
//////    	double money= customer.generateRandomMoneyToPay();
////    	customer.state=AgentState.WaitingForFood;
////    	Menu menu=new Menu();
////    	menu.makeItUnavailable("Steak");
////    	customer.msgOutOfChoice(menu);
//    	double change=4.99;
//    	change = ((double)(int)(change*100))/100;
//    	System.out.println(change);
//    }
    
}

