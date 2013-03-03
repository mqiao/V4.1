package restaurant;

public class BillFromMarket{
	
	public enum MarketBillStatus{ PENDING, HANDLING_BY_CASHIER,WAITINT_FOR_DELIVERY,DELIVERED_TO_MARKET};
	CookAgent cook;
	MarketAgent market;
	double totalPrice;
	double amountPaid;
	String choice;
	MarketBillStatus status;
	
	public BillFromMarket(CookAgent cook,MarketAgent market, double totalPrice,String choice)
	{
		this.cook=cook;
		this.market=market;
		this.totalPrice=totalPrice;
		this.choice=choice;
		status=MarketBillStatus.PENDING;
		amountPaid=0;
	}
	/*****setters*****/
	public void setStatus(MarketBillStatus status)
	{
		this.status=status;
	}
	public void setMarket(MarketAgent ma)
	{
		market=ma;
	}
	public void setPrice(double price)
	{
		this.totalPrice=price;
	}
	public void payBill(double money)
	{
		amountPaid+=money;
	}
	
	/****getters*******/
	public double getPrice()
	{
		return(this.totalPrice);
	}

	public MarketAgent getMarket()
	{
		return(this.market);
	}
	public CookAgent getCook()
	{
		return(this.cook);
	}
	public MarketBillStatus getStatus()
	{
		return(this.status);
	}
	public String getChoice()
	{
		return(this.choice);
	}
	public double getMoney()
	{
		return(this.amountPaid);
	}
}


