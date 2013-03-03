package restaurant;

import agent.Agent;
import java.util.*;

import restaurant.WaiterAgent.CustomerState;


/** Host agent for restaurant.
 *  Keeps a list of all the waiters and tables.
 *  Assigns new customers to waiters for seating and 
 *  keeps a list of waiting customers.
 *  Interacts with customers and waiters.
 */
public class HostAgent extends Agent {

    /** Private class storing all the information for each table,
     * including table number and state. */
    private class Table {
		public int tableNum;
		public boolean occupied;
	
		/** Constructor for table class.
		 * @param num identification number
		 */
		public Table(int num){
		    tableNum = num;
		    occupied = false;
		}	
    }
    public enum WaiterStatus{WORKING,WANT_TO_BREAK,IN_REST};
    /** Private class to hold waiter information and state */
    private class MyWaiter {
	public WaiterAgent wtr;
	//public boolean working = true;
	WaiterStatus status=WaiterStatus.WORKING;
	

	/** Constructor for MyWaiter class
	 * @param waiter
	 */
	public MyWaiter(WaiterAgent waiter){
	    wtr = waiter;
	}
    }

    enum CustomerStatus{PENDING,DECIDING,WAITING,LEAVING};
    
    private class MyCustomer{
    	public CustomerAgent customer;
    	public CustomerStatus status;
    	boolean sureWait=false;
    	public MyCustomer(CustomerAgent customer)
    	{
    		this.customer=customer;
    		this.status=CustomerStatus.WAITING;
    	}
    	public String toString()
    	{
    		String state= new String("");
    		if(status==CustomerStatus.PENDING)
    			state="pending";
    		if(status==CustomerStatus.LEAVING)
    			state="leaving";
    		if(status==CustomerStatus.WAITING)
    			state="waiting";
    		if(status==CustomerStatus.DECIDING)
    			state="deciding";
    		return(customer.getName()+ " "+state);
    	}
    }
    //List of all the customers that need a table
    private List<MyCustomer> waitList =
		Collections.synchronizedList(new ArrayList<MyCustomer>());

    //List of all waiter that exist.
    private List<MyWaiter> waiters =
		Collections.synchronizedList(new ArrayList<MyWaiter>());
    private int nextWaiter =0; //The next waiter that needs a customer
    
    //List of all the tables
    int nTables;
    private Table tables[];

    //Name of the host
    private String name;

    //Added in V4.1
    boolean toUpdateMenu=false;
    Menu menu;
    CookAgent cook;
    
    /** Constructor for HostAgent class 
     * @param name name of the host */
    public HostAgent(String name, int ntables) {
	super();
	this.nTables = ntables;
	tables = new Table[nTables];

	for(int i=0; i < nTables; i++){
	    tables[i] = new Table(i);
	}
	this.name = name;
    }

    // *** MESSAGES ***

    /** Customer sends this message to be added to the wait list 
     * @param customer customer that wants to be added */
    public void msgIWantToEat(CustomerAgent customer){
    	waitList.add(new MyCustomer(customer));
    	stateChanged();
    }

    /** Waiter sends this message after the customer has left the table 
     * @param tableNum table identification number */
    public void msgTableIsFree(int tableNum){
    	tables[tableNum].occupied = false;
    	stateChanged();
    }

    
    public void msgIWillWait(CustomerAgent customer)
    {
    	for(int i=0;i<waitList.size();i++) {
    		if(waitList.get(i).customer.equals(customer))
    		{
    			waitList.get(i).sureWait=true;
    			waitList.get(i).status=CustomerStatus.WAITING;
    			stateChanged();
    			return;
    		}
    	}
    	print("HostAgent: cannot find customer in msgIWillWait");
    }
    
    public void msgRunningOutOfFood(String choice) {
    	
    	menu.makeItUnavailable(choice); 
    	toUpdateMenu=true; 
    	stateChanged();
    }
    
    public void	msgAddItemsToMenu(String choice){
    	menu.makeItAvailable(choice);
    	toUpdateMenu=true;
    	stateChanged();
    }
    
    public void msgCanITakeABreak(WaiterAgent waiter)
    {
    	for(MyWaiter w:waiters)
    	{	
    		if(w.wtr.equals(waiter))
    		{
    			w.status=WaiterStatus.WANT_TO_BREAK;
    			stateChanged();
    			return;
    		}
    	}
    	
    	print("HostAgent: cannot find customer in msgCanITakeABreak");
    }
    
    public void msgGoBackToWork(WaiterAgent waiter)
    {
    	for(MyWaiter w:waiters)
    	{	
    		if(w.wtr.equals(waiter))
    		{
    			print("Host let waiter: "+ w.wtr.getName()+" go back to work");
    			w.status=WaiterStatus.WORKING;
    			stateChanged();
    			return;
    		}
    	}
    	print("HostAgent: cannot find customer in msgGoBackToWork");
    	
    }
    
    public void msgIWillLeave(CustomerAgent customer)
    {
    	for(int i=0;i<waitList.size();i++) {
    		if(waitList.get(i).customer.equals(customer))
    		{
//    			print("Host: found "+waitList.get(i).customer.getName()+"in waitlist and change its state to leaving");
    			waitList.get(i).status=CustomerStatus.LEAVING;
    			stateChanged();
    			return;
    		}
    	}
    	print("HostAgent: cannot find customer in msgIWillLeave");
    }
    
    
    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
	
    if(toUpdateMenu==true)
    {
    	tellWaitersToUpdate();
    	return true;
    }
    	
    for(MyWaiter w:waiters)
	{
		if(w.status==WaiterStatus.WANT_TO_BREAK)
		{
			checkIfQualified(w);
			return true;
		}
		
	}
//	for(MyCustomer c:waitList)
//	{
//		boolean allFull=true;
//		if(c.status==CustomerStatus.PENDING)
//		{
//			
//			for(int i=0;i<nTables;i++)
//			{	if(!tables[i].occupied)
//					{
//						allFull=false;
//						break;
//					}
//			}
//			if(allFull)
//			{
//				
//				tellCustomerToWait(waitList.get(0));
//				c.status=CustomerStatus.DECIDING;
//			}
//			else
//				c.status=CustomerStatus.WAITING;
//			return true;
//		}
//	}
	if(!waitList.isEmpty() && !waiters.isEmpty()){
	    synchronized(waiters){
		//Finds the next waiter that is working
		while(waiters.get(nextWaiter).status!=WaiterStatus.WORKING){
		    nextWaiter = (nextWaiter+1)%waiters.size();
		}
	    }
	    //Then runs through the tables and finds the first unoccupied 
	    //table and tells the waiter to sit the first customer at that table
	    boolean isFull=true;
	    for(int i=0; i < nTables; i++){
	    	if(!tables[i].occupied){
	    		synchronized(waitList){	
	    			for(MyCustomer c: waitList)
	    			{
	    				if(c.status==CustomerStatus.WAITING)	    					
	    				{	
	    				    print("picking waiter number:"+nextWaiter);
	    				    isFull=false;
	    					tellWaiterToSitCustomerAtTable(waiters.get(nextWaiter),c.customer, i);	
	    					//print("HostAgent: told customer to sit at table "+i);
	    					return true;
	    				}
	    			}
	    		}
	    	}	
	    }
	    if(isFull)
	    {
	    	for(MyCustomer c: waitList)
			{
				if(c.status==CustomerStatus.WAITING&&c.sureWait==false)
				{
					c.status=CustomerStatus.DECIDING;
					tellCustomerToWait(c);
					return true;
				}
			}
	    }
	    
	}

	

	for(MyCustomer c:waitList)
	{
		if(c.status==CustomerStatus.LEAVING)
		{
			removeCustomer(c.customer);
			return true;
		}
	}

	//we have tried all our rules (in this case only one) and found
	//nothing to do. So return false to main loop of abstract agent
	//and wait.
	return false;
    }
    
    // *** ACTIONS ***
    
    /** Assigns a customer to a specified waiter and 
     * tells that waiter which table to sit them at.
     * @param waiter
     * @param customer
     * @param tableNum */
	private void tellWaitersToUpdate()
	{
		for(int i=0;i<waiters.size();i++) 
			{
				print("Host: tell waiter"+waiters.get(i).wtr.getName()+" to update the menu.");
				waiters.get(i).wtr.msgUpdateMenu(this.menu);
			}
		toUpdateMenu=false;
		stateChanged();
	}
	private void checkIfQualified(MyWaiter waiter)
	{
		boolean ifBreak=false;
		for(MyWaiter w:waiters)
		{
			if(w.status==WaiterStatus.WORKING&&!w.equals(waiter))
			{
				ifBreak=true;
				print("Host: tell waiter "+waiter.wtr.getName()+" totake a break.");
				waiter.wtr.msgTakeABreak();
				waiter.status=WaiterStatus.IN_REST;
				break;
			}
			
		}
		//print("host is considering "+waiter.wtr.getName()+"'s request"+ ifBreak);

		if(!ifBreak)
		{
			waiter.status=WaiterStatus.WORKING;
			print("Host: "+waiter.wtr.getName()+" is not qualified to take a break.");
		}
		
		stateChanged();
	}
	
	private void tellCustomerToWait(MyCustomer customer)
	{
		print("Host: Tell customer "+customer.customer.getName()+" to wait");
		customer.customer.msgTableIsFull();
		stateChanged();
	}
	
	private void removeCustomer(CustomerAgent customer)
	{
		print("Host: remove customer "+customer.getName());
		for(MyCustomer c:waitList)
		{
			if(customer.equals(c.customer))
			{
				waitList.remove(c);
				stateChanged();
				return;
			}
			
		}
		print("Host:could not find customer "+customer.getName()+" when trying to remove.");
	}
	
    private void tellWaiterToSitCustomerAtTable(MyWaiter waiter, CustomerAgent customer, int tableNum){
	print("Telling " + waiter.wtr + " to sit " + customer +" at table "+(tableNum+1));
	waiter.wtr.msgSitCustomerAtTable(customer, tableNum);
	tables[tableNum].occupied = true;
	removeCustomer(customer);
	for(int i=0;i<waitList.size();i++)//test
	{
		print(waitList.get(i).toString());
	}
//	print("Waiters' size: "+waiters.size());
//	print("NextWaiter: "+nextWaiter);
	nextWaiter = (nextWaiter+1)%waiters.size();
//	print("Host: next waiter to be asigned is "+nextWaiter);
	stateChanged();
    }
	
    

    // *** EXTRA ***

    /** Returns the name of the host 
     * @return name of host */
    public String getName(){
        return name;
    }    

    public void setMenu(Menu menu)
    {
    	this.menu=menu;
    	stateChanged();
    }
    public Menu getMenu()
    {
    	return menu;
    }
    /** Hack to enable the host to know of all possible waiters 
     * @param waiter new waiter to be added to list
     */
    public void setWaiter(WaiterAgent waiter){
	waiters.add(new MyWaiter(waiter));
	stateChanged();
    }
    public void setCook(CookAgent cook)
    {
    	this.cook=cook;
    }
    
    //Gautam Nayak - Gui calls this when table is created in animation
    public void addTable() {
	nTables++;
	Table[] tempTables = new Table[nTables];
	for(int i=0; i < nTables - 1; i++){
	    tempTables[i] = tables[i];
	}  		  			
	tempTables[nTables - 1] = new Table(nTables - 1);
	tables = tempTables;
    }
    
    public void addItemToMenu(String name, double price)
    {
    	menu.addItem(name, price);
    	cook.addNewFood(name);
    	toUpdateMenu=true;
    	stateChanged();	
    }
    public void modifyMenu(String name, double price)
    {
    	menu.setPrice(name,price);
    }
}
