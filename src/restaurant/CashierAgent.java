package restaurant;

import java.util.ArrayList;
import java.util.List;

import restaurant.BillFromCustomer.CustomerBillStatus;
import restaurant.BillFromMarket.MarketBillStatus;

import agent.Agent;

public class CashierAgent extends Agent{	
	private String name="Cashier";
	List<BillFromCustomer> billsFromCustomer=new ArrayList<BillFromCustomer>(); 
	List<BillFromMarket> billsFromMarket = new ArrayList<BillFromMarket>();
	Menu menu= new Menu();
	
	public CashierAgent()
	{
		super();
	}
	
	public void msgCustomerOrdered(WaiterAgent waiter,CustomerAgent customer, String choice) {
		double price = menu.getPrice(choice); 
		billsFromCustomer.add(new BillFromCustomer(waiter,customer,price));//the state of the bill is pending
		stateChanged();
//		for(BillFromCustomer bill:billsFromCustomer)
//		{
//			print("billsFromCustomer"+bill.getCustomer().getName()+" "+bill.getTotalPrice()+" "+bill.getWaiter().getName());
//		}
		
	}
	
	public void msgHereIsTheMoney(CustomerAgent c, double money, BillFromCustomer bill) {
		bill.payBill(money); 
		bill.setStatus(CustomerBillStatus.UNPAID); 
		billsFromCustomer.add(bill);
		stateChanged();
		}
	
	public void msgBillOfFood(BillFromMarket bill) {
		print("Cashier got the bill of "+bill.choice+" from the market");
		bill.setStatus(MarketBillStatus.HANDLING_BY_CASHIER);
		billsFromMarket.add(bill);	//the state of the bill is handling by cashier
		stateChanged();
	}
	
	 protected boolean pickAndExecuteAnAction(){
		for(BillFromCustomer b:billsFromCustomer)
		{
			if(b.getStatus()==CustomerBillStatus.PENDING)
				{
					hereIsBill(b);
					return true;
				}
		}
		
		for(BillFromMarket b: billsFromMarket)
		{
			if(b.getStatus()==MarketBillStatus.HANDLING_BY_CASHIER)
			{
				hereIsMoney(b);
				return true;
			}
//			else{
//				print(""+b.status);
//			}
			
		}
		
		for(BillFromCustomer b: billsFromCustomer)
		{
			if(b.getStatus()==CustomerBillStatus.UNPAID)
				{
					checkOut(b);
					return true;
				}
		}

		return false;
	}

	protected void hereIsMoney (BillFromMarket bill) //send the money along with the bill to the market
	{
		print("Cashier: give the money along with bill to the market.");
		double money = bill.getPrice();
		bill.payBill(money);
		bill.getMarket().msgHereIsMoney(bill);
		removeMarketBill(bill);
		stateChanged();
	}
	protected void checkOut(BillFromCustomer bill) //take the bill from the customer and compute
	{
		if(bill.getAmountPaid()<bill.getTotalPrice())
			{
				print("Cashier: tell "+bill.getCustomer()+" that his money is not enough and he needs to call a friend for more money. customer has "+bill.getAmountPaid()+" and the price is "+bill.getTotalPrice());
				bill.getCustomer().msgNotEnoughPleaseCallFriends(bill);
			}
		else{
				print("Cashier: tell the customer thanks for his coming and give him his change");
				double change=bill.getAmountPaid()-bill.getTotalPrice();
		    	change = ((double)(int)(change*100))/100;
				bill.getCustomer().msgThanksForComing(bill,change);
			}
		removeCustomerBill(bill);
		stateChanged();
	}
	protected void hereIsBill (BillFromCustomer bill)
	{
		print("Cashier: tell the waiter to pickup the bill");
		bill.getWaiter().msgHereIsBill(bill);
		removeCustomerBill(bill);
		stateChanged();
	}
	private void removeMarketBill(BillFromMarket bill)
	{
		for(BillFromMarket b:billsFromMarket)
		{
			if(b.equals(bill))
			{
				billsFromMarket.remove(b);
				stateChanged();
				return;
			}
		}
	}
	private void removeCustomerBill(BillFromCustomer bill)
	{
		for(BillFromCustomer b:billsFromCustomer)
		{
			if(b.equals(bill))
			{
				billsFromCustomer.remove(b);
				stateChanged();
				return;
			}
		}
	}
}
