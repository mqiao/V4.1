package restaurant;


public class BillFromCustomer{ 
	
	private WaiterAgent waiter;
	private CustomerAgent customer;
	private double totalPrice;
	private double amountPaid;
	private CustomerBillStatus status;
	
	public enum CustomerBillStatus{PENDING, UNPAID };
	
	BillFromCustomer(WaiterAgent w, CustomerAgent c, double totalPrice)
	{
		this.waiter=w;
		this.customer=c;
		this.totalPrice=totalPrice;
		this.amountPaid=0;;
		this.status=CustomerBillStatus.PENDING;
	}
	/***setters****/
	public void setStatus(CustomerBillStatus status)
	{
		this.status=status;
	}
	
	public void setWaiter(WaiterAgent waiter)
	{
		this.waiter=waiter;
	}
	
	public void payBill(double amount)
	{
		amountPaid+=amount;
	}
	
	public void setTotalPrice(double totalPrice)
	{
		this.totalPrice=totalPrice;
	}
	/****getters****/
	public CustomerBillStatus getStatus()
	{
		return(this.status);
	}

	public double getAmountPaid(){
		return amountPaid;
	}
	
	public double getTotalPrice()
	{
		return(this.totalPrice);
	}
	public CustomerAgent getCustomer()
	{
		return(this.customer);
	}
	public WaiterAgent getWaiter()
	{
		return(this.waiter);
	}
}