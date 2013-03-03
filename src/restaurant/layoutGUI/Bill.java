package restaurant.layoutGUI;

import java.awt.*;

public class Bill
{
    private int x, y;
    private Color color;
    private Restaurant restaurant;
    private String name;
    
    public Bill(Restaurant restaurant)
    {
        this.name       =   "BI";
        this.color      =   Color.GREEN;
        this.restaurant =   restaurant;
    }
    
    public int getX()
    {
        return x;
    }
    
    public int getY()
    {
        return y;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void generateBill()
    {
        this.y  =   restaurant.getCashierY();
        this.x  =   restaurant.getCashierX();
        //this.placeBill();
    }
    
    
    protected void placeBill()	//we do not need this since the bill won't show on the cashier
    {
        restaurant.placeBill(x, y, color, name);
    }
    
    protected void move(int x, int y)
    {
        this.x          =   x;
        this.y          =   y;
    }
    
    public void remove()	
    {
        restaurant.removeBill(x, y);
    }
}
