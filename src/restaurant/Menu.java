package restaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Menu {
	public Menu()
	{
		this.addItem("Steak",15.99);
		this.addItem("Chicken",10.99);
		this.addItem("Salad", 5.99);
		this.addItem("Pizza",8.99);
		
	}
	
//    public String choices[] = new String[]
//	{ "Steak"  ,
//	  "Chicken", 
//	  "Salad"  , 
//	  "Pizza"  };
//    
    
    public class FoodOnMenu{ 
		private String name;
		private double price;
		private boolean isAvailable;
		FoodOnMenu(String name, double price)
		{
			this.name=name;
			this.price=price;
			isAvailable=true;
		}
	}
    
    Map<String,FoodOnMenu> foodInventory = new HashMap<String,FoodOnMenu>();
    
    public List<String> choices = new ArrayList<String>();
	//Map<String,FoodOnMenu> FoodPriceMap;
	public void addItem(String name, double price)
	{
		foodInventory.put(name,new FoodOnMenu(name,price));
		choices.add(name);
	}
	public int getSizeOfMenu()
	{
		int size=0;
		for(String choice:choices)
		{
			if(getAvailability(choice))
			{
				size++;
			}
		}
		return size;
	}
	public void deleteItem(String name)
	{
		foodInventory.remove(name);
		for(int i=0;i<choices.size();i++)
		{
			if(choices.get(i).equals(name))
				choices.remove(i);
		}
	}
	public void setPrice(String name, double price)
	{
		foodInventory.get(name).price=price;
	}
	public boolean getAvailability(String name)
	{
		if(foodInventory.get(name)!=null)
			return(foodInventory.get(name).isAvailable);
		else
		{
			System.err.println("Menu:cannot find item in getAvailability");
			return false;
		}
	}
	
	public double getPrice(String name)
	{
		
		if(foodInventory.get(name)!=null)
			return(foodInventory.get(name).price);
		else
		{
			System.err.println("Menu:cannot find item in getPrice");
			return 0;
		}
	}
	public double getHighestPrice()
	{
		double highestPrice=0;
		for(String choice:choices)
		{
			if(getPrice(choice)>highestPrice)
				highestPrice=getPrice(choice);
		}
		return highestPrice;
	}
	public double getLowestPrice()
	{
		double lowestPrice=getPrice(choices.get(0));
		for(String choice:choices)
		{
			if(getPrice(choice)<lowestPrice)
				lowestPrice=getPrice(choice);
		}
		return lowestPrice;

	}
	public void makeItUnavailable(String foodName)
	{	
			if(foodInventory.get(foodName)!=null)
			{
				if(foodInventory.get(foodName).isAvailable==true)
					{
						foodInventory.get(foodName).isAvailable=false;
						System.out.println("Menu:The item "+foodName+" is set to unavailable!");			
					}
				//else
					//System.err.println("Menu:The item "+foodName+" to be unavailable is already unavailable!");			
			}		
			//else
				//System.err.println("Menu:The item "+foodName+" to be unavailable is not in the menu list!");
			//printAvailableFood();
	}
	
	public void makeItAvailable(String foodName)
	{
		if(foodInventory.get(foodName)!=null)
		{
			if(foodInventory.get(foodName).isAvailable==false)
				{
					foodInventory.get(foodName).isAvailable=true;
					//System.err.println("Menu:The item "+foodName+" is set to available!");			
				}
			//else
				//System.err.println("Menu:The item "+foodName+" to be available is already available!");			
		}		
		//else
			//System.err.println("Menu:The item "+foodName+" to be available is not in the menu list!");
		//printAvailableFood();

	}
	public void printAvailableFood()
	{
		System.out.println("Available foods are:");
		for(int i=0;i<choices.size();i++)
		{
			if(foodInventory.get(choices.get(i)).isAvailable)
				System.out.println(choices.get(i));
		}
	}
}
