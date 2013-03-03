package restaurant.layoutGUI;

import java.awt.*;

public class GuiCashier
{
    private int x, y;
    private Color color;
    private Restaurant restaurant;
    private String name;
    
    public GuiCashier(String name, Color color, Restaurant restaurant)
    {
        this.name       =   "cashier";
        this.color      =   color;
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
    
    protected void placeCashier()
    {
        restaurant.placeCustomer(x, y, color, name);
    }
    
    protected void move(int x, int y)
    {
        this.x  =   x;
        this.y  =   y;
    }
    
    public void leave()
    {
        restaurant.removeCustomer(x, y);
    }
}