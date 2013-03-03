package restaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import restaurant.BillFromMarket.MarketBillStatus;
import restaurant.CookAgent.Status;
import restaurant.FoodRequest.RequestStatus;
import agent.Agent;

public class MarketAgent extends Agent{
	
	Map<String,FoodInMarket> storage = new HashMap<String,FoodInMarket>();//string is choice
	float income;
	String name;
	List<FoodRequest> requests;
	List<BillFromMarket> bills;
	Timer timer = new Timer();
	CashierAgent cashier;
	
	public MarketAgent(String name)
	{
		this.name=name;
		income=0;
		requests=new ArrayList<FoodRequest>();
		bills= new ArrayList<BillFromMarket>();
		initializeStorage();
	}
	
	public class FoodInMarket{
		String foodName;
		double foodPrice;
		double preparingTime;
		
		public FoodInMarket(String foodName, double foodPrice, double preparingTime)
		{
			this.foodName=foodName;
			this.foodPrice=foodPrice;
			this.preparingTime=preparingTime;
		}
		
	}


	public void initializeStorage()
	{
		storeANewFood("Steak",7.49,3000);
		storeANewFood("Chicken",6.33,4000);
		storeANewFood("Salad",4.99,1500);
		storeANewFood("Pizza",10.99,2500);
	}
	public boolean ifHas(String foodName)
	{
		return(storage.get(foodName)!=null);
	}
	
	public void storeANewFood(String name, double price, double preparingTime)
	{
		storage.put(name, new FoodInMarket(name,price,preparingTime));
	}
	
	public void updateFoodInfo(Menu menu, String name,int prepTime,boolean update)
	{
		double price= menu.getPrice(name);
		if(storage.get(name)==null)
		{
			if(update)
			{
				storeANewFood(name,price,prepTime*1000);
				print(""+name+" store a new food: "+name);
			}
			
		}
		else{
				//print("already store the food.");
			if(update==false)
			{
				storage.remove(name);
				print(""+name+" is removed from the market "+this.name);
			}
			else{
				storage.get(name).preparingTime=prepTime*1000;
			}
		}

	}
	
	public void msgOrderFood(FoodRequest r)
	{
		print(""+name+" got the massage of ordering "+r.food);
		requests.add(r);
		r.status=RequestStatus.PENDING;
		stateChanged();
	}
	
	public void msgHereIsMoney(BillFromMarket bill)
	{
		print("Market: "+this.name+" got the money from the cashier");
		bill.setStatus(MarketBillStatus.DELIVERED_TO_MARKET);	
		bills.add(bill);
		stateChanged();
	}

	
	protected boolean pickAndExecuteAnAction(){
		
		for(FoodRequest r:requests)
		{
			if(r.status==RequestStatus.DONE)
				{
					sendFoodAndBill(r);	
					return true;
				}
		}
		
		for(FoodRequest r:requests)
		{
			if(r.status==RequestStatus.PENDING)
				{
					handleRequest(r);
					return true;
				}
		}
		

		for(BillFromMarket b: bills)
		{
			if(b.status==MarketBillStatus.DELIVERED_TO_MARKET)
				{
					handlePayment(b);
					return true;
				}
		}
		
		return false;
	}
	
	protected void handleRequest( final FoodRequest r)
	{

//	    print("Market :handling request is executing." +requests.size()+"requests in the list");
		if(storage.get(r.food)!=null)
		{
			 for(FoodRequest request: requests)
			    {
			    	if(r.equals(request))
			    	{
			    		request.status=RequestStatus.PREPARING;
			    		break;
			    	}
			    }
			DoHandlingRequest(r);
		}
		else
		{
			print("Market: tell cook that we do not have "+r.food);
			r.cook.msgWeDontHaveIt(this,r.food);
			removeRequest(r);
			stateChanged();
		}

	}
	protected void DoHandlingRequest(final FoodRequest r)
	{
		timer.schedule(new TimerTask(){
		    public void run(){//this routine is like a message reception 
			//r.status = RequestStatus.DONE;
		    	msgDoneHandlingRequest(r);
		    }
		}, (int)(storage.get(r.food).preparingTime));
	}
	
	protected void msgDoneHandlingRequest(FoodRequest r)
	{
		// print("Market :handling request is done.");
			//r.status = RequestStatus.DONE;
		    for(FoodRequest request: requests)
		    {
		    	if(r.equals(request))
		    	{
		    		request.status=RequestStatus.DONE;
		    		break;
		    	}
		    }
		    stateChanged();
	}
	protected void sendFoodAndBill(FoodRequest r)
	{
		print("Market: send food "+r.food+" to the cook");
		
		//int amount=((int)Math.random())*r.amount;//a random number between 0-amount;TODO: modify the algorithm of paying
		int amount = r.amount;
		double totalPrice=amount* storage.get(r.food).foodPrice;
		BillFromMarket bill= new BillFromMarket(r.cook,this,totalPrice,r.food);
		r.setAmount(amount);
		//r.cook.msgHereIsFoodAndBill(r,bill);
		r.cook.msgHereIsFood(r);
		print("Market: send the bills of  "+r.food+" to the cashier");
		cashier.msgBillOfFood(bill);
		removeRequest(r);
		stateChanged();

	}
	
	protected void handlePayment(BillFromMarket b)
	{
		print("Market: add "+b.getMoney()+" to the account");
		income+=b.getMoney();
		removeBill(b);
		stateChanged();
	}
	
	private void removeRequest(FoodRequest request)
	{
		for(int i=0;i<requests.size();i++)
		{
			if(requests.get(i).equals(request))
			{
				requests.remove(i);
				return;
			}
		}
	}
	private void removeBill(BillFromMarket bill)
	{
		for(int i=0;i<bills.size();i++)
		{
			if(bills.get(i).equals(bill))
			{
				bills.remove(i);
				return;
			}
		}
	}
//	public static void main (String[] args)
//	{
//		FoodRequest r = new FoodRequest(null,"Steak",10);
//		MarketAgent market= new MarketAgent();
//		market.requests.add(r);
//		market.handleRequest(r);
//		for(int i=0;i<market.requests.size();i++)
//			if(market.requests.get(i).status==RequestStatus.DONE)
//				System.out.println("Status set to Done");
//			else if(market.requests.get(i).status==RequestStatus.PENDING)
//				System.out.println("Status is pending");
//			else if(market.requests.get(i).status==RequestStatus.PREPARING)
//				System.out.println("Status is preparing");
//		
//		
//	}
}
