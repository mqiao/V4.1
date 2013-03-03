package restaurant;


public class FoodRequest{
	CookAgent cook;
	String food;
	int amount;
	RequestStatus status;
	public enum RequestStatus{PENDING, PREPARING,DONE,/*DONT_HAVE_IT*/};
	
	public FoodRequest(CookAgent cook,  String food, int amount)
	{
		this.cook=cook;
		this.food=food;
		this.amount=amount;
		status=RequestStatus.PENDING;
	}
	
	public void setAmount(int amount){
		this.amount=amount;
	}
}