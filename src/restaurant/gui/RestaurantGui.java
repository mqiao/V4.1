package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;
import java.io.File;


/** Main GUI class.
 * Contains the main frame and subsequent panels */
public class RestaurantGui extends JFrame implements ActionListener , ItemListener{
   
    private final int WINDOWX = 600;
    private final int WINDOWY = 800;

    private RestaurantPanel restPanel = new RestaurantPanel(this);
    private JPanel infoPanel = new JPanel();
    private JLabel infoLabel = new JLabel(
    "<html><pre><i>(Click on a customer/waiter)</i></pre></html>");
    
    private JCheckBox stateCB = new JCheckBox();
	private JButton addTable = new JButton("Add Table");

    private Object currentPerson;

    private JPanel addCustomerPanel = new JPanel();
    //newly added panel in V4.1
    private JLabel addCustHeader = new JLabel("customer");
    private JPanel bottomPN = new JPanel();
    private JPanel custNamePN = new JPanel();///panel that adds a new customer
    private JLabel custNameLB = new JLabel("name: ");
    private JTextField custNameTF= new JTextField();
    private JPanel moneyPN = new JPanel();
    private JLabel moneyLB = new JLabel("money: ");
    private JTextField moneyTF = new JTextField();
    private JPanel foodPN = new JPanel();
    private JComboBox orderCoBo = new JComboBox();
    private JPanel waitPN = new JPanel();
    private JLabel waitLB = new JLabel("willing to wait?");
    private JCheckBox waitCB = new JCheckBox();
    private JPanel confirmPanel0 = new JPanel();
    private JButton confirmCust = new JButton("confirm");
//    private JButton deleteCust = new JButton("delete");
    
    private JPanel menuPN = new JPanel(); 
    private JLabel menuHeader = new JLabel("menu");
    private JPanel foodMenuPN = new JPanel();
    private JLabel foodMenuLB = new JLabel();
    private JComboBox foodCoBo = new JComboBox();
    private JPanel foodNamePN = new JPanel();
    private JLabel foodNameLB = new JLabel("name: ");
    private JTextField foodNameTF= new JTextField();
    private JPanel foodPricePN = new JPanel();
    private JLabel foodPriceLB = new JLabel("price: ");
    private JTextField foodPriceTF = new JTextField();
    private JPanel confirmPanel1 = new JPanel();
    private JButton confirmMenu = new JButton("confirm");
//    private JButton deleteMenu = new JButton("delete");
    
    private JPanel marketPN = new JPanel(); 
    private JLabel marketHeader = new JLabel("storage");
    private JPanel foodMarketPN = new JPanel();
    private JLabel foodMarketLB = new JLabel();
    private JComboBox marketCoBo = new JComboBox();
//    private JPanel foodNameMKPN = new JPanel();
//    private JLabel foodNameMKLB = new JLabel("name: ");
//    private JTextField foodNameMKTF= new JTextField();
    private JPanel foodAmountPN = new JPanel();
    private JLabel foodAmountLB = new JLabel("amount: ");
    private JTextField foodAmountTF = new JTextField();
    private JPanel foodMaxPN = new JPanel();
    private JLabel foodMaxLB = new JLabel("max: ");
    private JTextField foodMaxTF = new JTextField();
    private JPanel foodThresholdPN = new JPanel();
    private JLabel foodThresholdLB = new JLabel("threshold: ");
    private JPanel pTimePN = new JPanel();
    private JLabel pTimeLB = new JLabel("preparation time: (s)");
    private JTextField pTimeTF = new JTextField();
    private JTextField foodThresholdTF = new JTextField();
    private JPanel confirmPanel2 = new JPanel();
    private ArrayList<JPanel> marketPanels = new ArrayList<JPanel>();
    private ArrayList<JLabel> marketNames = new ArrayList<JLabel>();
    private ArrayList<JCheckBox> marketCBs= new ArrayList<JCheckBox>(); 
    private JButton confirmMKT = new JButton("confirm");
//    private JButton deleteMKT= new JButton("delete");
    
	Dimension rest = new Dimension(600, 300);
	Dimension info = new Dimension(600, 100);
	Dimension newCust = new Dimension(200, 150); //v4.1
	Dimension market = new Dimension(200, 270); //v4.1
	Dimension textField = new Dimension(100,30);
	Dimension bottom = new Dimension(600,270);
	
	boolean newMenuItem = true;
	String menuItemSelected;
	String marketItemSelected;
    /** Constructor for RestaurantGui class.
     * Sets up all the gui components. */
    public RestaurantGui(){

	super("Restaurant Application");

	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(50,50, WINDOWX, WINDOWY);

	getContentPane().setLayout(new BoxLayout((Container)getContentPane(),BoxLayout.Y_AXIS));

//	Dimension rest = new Dimension(WINDOWX, (int)(WINDOWY*.6));
//	Dimension info = new Dimension(WINDOWX, (int)(WINDOWY*.25));
//	Dimension newCust = new Dimension(WINDOWX, (int)(WINDOWY*.6)); //v4.1


	restPanel.setPreferredSize(rest);
	restPanel.setMinimumSize(rest);
	restPanel.setMaximumSize(rest);
	infoPanel.setPreferredSize(info);
	infoPanel.setMinimumSize(info);
	infoPanel.setMaximumSize(info);
	infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
	addCustomerPanel.setPreferredSize(newCust); //V4.1
	addCustomerPanel.setMinimumSize(newCust);
	addCustomerPanel.setMaximumSize(newCust);
	menuPN.setPreferredSize(newCust); //V4.1
	menuPN.setMinimumSize(newCust);
	menuPN.setMaximumSize(newCust);
	marketPN.setPreferredSize(market); //V4.1
	marketPN.setMinimumSize(market);
	marketPN.setMaximumSize(market);
	bottomPN.setPreferredSize(bottom); //V4.1
	bottomPN.setMinimumSize(bottom);
	bottomPN.setMaximumSize(bottom);

	setTFSize(custNameTF);
	setTFSize(moneyTF);
	setTFSize(foodNameTF);
	setTFSize(foodPriceTF);
	setTFSize(foodAmountTF);
	setTFSize(foodMaxTF);
	setTFSize(foodThresholdTF);
	setTFSize(pTimeTF);
	//setTFSize(foodNameMKTF);
	
	stateCB.setVisible(false);
	stateCB.addActionListener(this);

	infoPanel.setLayout(new GridLayout(1,2, 30,0));
	infoPanel.add(infoLabel);
	infoPanel.add(stateCB);
	
	custNamePN.setLayout(new BoxLayout(custNamePN,BoxLayout.X_AXIS));
	custNamePN.add(custNameLB);
	custNamePN.add(custNameTF);
	moneyPN.setLayout(new BoxLayout(moneyPN,BoxLayout.X_AXIS));
	moneyPN.add(moneyLB);
	moneyPN.add(moneyTF);
	foodPN.setLayout(new BoxLayout(foodPN,BoxLayout.X_AXIS));
	foodPN.add(orderCoBo);
	orderCoBo.addItem(new String("Let us decide the order"));
	for(int i=0;i<restPanel.getMenu().getSizeOfMenu();i++)
	{
		orderCoBo.addItem(restPanel.getMenu().choices.get(i));
	}
	waitPN.setLayout(new BoxLayout(waitPN,BoxLayout.X_AXIS));
	waitPN.add(waitLB);
	waitPN.add(waitCB);	
	confirmPanel0.setLayout(new BoxLayout(confirmPanel0,BoxLayout.X_AXIS));
	confirmPanel0.add(confirmCust);
//	confirmPanel0.add(deleteCust);
	addCustomerPanel.setLayout(new BoxLayout(addCustomerPanel, BoxLayout.Y_AXIS));
	addCustomerPanel.add(addCustHeader);
	addCustomerPanel.add(custNamePN);
	addCustomerPanel.add(moneyPN);
	addCustomerPanel.add(foodPN);
	addCustomerPanel.add(waitPN);
	addCustomerPanel.add(confirmPanel0);
	
	foodMenuPN.setLayout(new BoxLayout(foodMenuPN,BoxLayout.X_AXIS));
	foodMenuPN.add(foodMenuLB);
	foodMenuPN.add(foodCoBo);
	foodCoBo.addItem(new String("new Food:"));
	for(int i=0;i<restPanel.getMenu().getSizeOfMenu();i++)
	{
		foodCoBo.addItem(restPanel.getMenu().choices.get(i));
	}
	foodNamePN.setLayout(new BoxLayout(foodNamePN,BoxLayout.X_AXIS));
	foodNamePN.add(foodNameLB);
	foodNamePN.add(foodNameTF);
	foodPricePN.setLayout(new BoxLayout(foodPricePN,BoxLayout.X_AXIS));
	foodPricePN.add(foodPriceLB);
	foodPricePN.add(foodPriceTF);
	confirmPanel1.setLayout(new BoxLayout(confirmPanel1,BoxLayout.X_AXIS));
	confirmPanel1.add(confirmMenu);
//	confirmPanel1.add(deleteMenu);
	menuPN.setLayout(new BoxLayout(menuPN, BoxLayout.Y_AXIS));
	menuPN.add(menuHeader);
	menuPN.add(foodMenuPN);
	menuPN.add(foodNamePN);
	menuPN.add(foodPricePN);
	menuPN.add(confirmPanel1);
	
	foodMarketPN.setLayout(new BoxLayout(foodMarketPN,BoxLayout.X_AXIS));
	foodMarketPN.add(foodMarketLB);
	foodMarketPN.add(marketCoBo);
	for(int i=0;i<restPanel.getMenu().getSizeOfMenu();i++)
	{
		marketCoBo.addItem(restPanel.getMenu().choices.get(i));
	}
//	marketCoBo.addItem(new String("new Food:"));
//	foodNameMKPN.setLayout(new BoxLayout(foodNameMKPN,BoxLayout.X_AXIS));
//	foodNameMKPN.add(foodNameMKLB);
//	foodNameMKPN.add(foodNameMKTF);
	foodAmountPN.setLayout(new BoxLayout(foodAmountPN,BoxLayout.X_AXIS));
	foodAmountPN.add(foodAmountLB);
	foodAmountPN.add(foodAmountTF);
	foodMaxPN.setLayout(new BoxLayout(foodMaxPN,BoxLayout.X_AXIS));
	foodMaxPN.add(foodMaxLB);
	foodMaxPN.add(foodMaxTF);
	foodThresholdPN.setLayout(new BoxLayout(foodThresholdPN,BoxLayout.X_AXIS));
	foodThresholdPN.add(foodThresholdLB);
	foodThresholdPN.add(foodThresholdTF);
	pTimePN.setLayout(new BoxLayout(pTimePN,BoxLayout.X_AXIS));
	pTimePN.add(pTimeLB);
	pTimePN.add(pTimeTF);
	confirmPanel2.setLayout(new BoxLayout(confirmPanel2,BoxLayout.X_AXIS));
	confirmPanel2.add(confirmMKT);
//	confirmPanel2.add(deleteMKT);
	marketPN.setLayout(new BoxLayout(marketPN, BoxLayout.Y_AXIS));
	marketPN.add(marketHeader);
	marketPN.add(foodMarketPN);
	//marketPN.add(foodNameMKPN);
	marketPN.add(foodAmountPN);
	marketPN.add(foodMaxPN);
	marketPN.add(foodThresholdPN);
	marketPN.add(pTimePN);
	
	setUpMarketCB();
	
	marketPN.add(confirmPanel2);
	
	bottomPN.setLayout(new BoxLayout(bottomPN, BoxLayout.X_AXIS));
	bottomPN.add(addCustomerPanel);
	bottomPN.add(menuPN);
	bottomPN.add(marketPN);
	getContentPane().add(restPanel);
	getContentPane().add(addTable);
	getContentPane().add(infoPanel);
	getContentPane().add(bottomPN);//v4.1
	
	addTable.addActionListener(this);
	addActLis();
    }

    private void setTFSize(JTextField tf)
    {
    	tf.setPreferredSize(textField);
    	tf.setMinimumSize(textField);
    	tf.setMaximumSize(textField);
    }
    /** This function takes the given customer or waiter object and 
     * changes the information panel to hold that person's info.
     * @param person customer or waiter object */
    public void updateInfoPanel(Object person){
	stateCB.setVisible(true);
	currentPerson = person;
	
	if(person instanceof CustomerAgent){
	    CustomerAgent customer = (CustomerAgent) person;
	    stateCB.setText("Hungry?");
	    stateCB.setSelected(customer.isHungry());
	    stateCB.setEnabled(!customer.isHungry());
	    infoLabel.setText(
	    "<html><pre>     Name: " + customer.getName() + " </pre></html>");

	}else if(person instanceof WaiterAgent){
	    WaiterAgent waiter = (WaiterAgent) person;
	    stateCB.setText("On Break?");
	    stateCB.setSelected(waiter.isOnBreak());
	    stateCB.setEnabled(true);
	    infoLabel.setText(
	    "<html><pre>     Name: " + waiter.getName() + " </html>");
	}	   

	infoPanel.validate();
    }

    public void addMenuItemToComboBox(String tmpName)
    {
    	foodCoBo.addItem(new String(tmpName));
    	
    }
    public void addMarketItemToComboBox(String tmpName)
    {
    	marketCoBo.addItem(new String(tmpName));
    }
    /** Action listener method that reacts to the checkbox being clicked */
    public void actionPerformed(ActionEvent e){

	if(e.getSource() == stateCB){
	    if(currentPerson instanceof CustomerAgent){
		CustomerAgent c = (CustomerAgent) currentPerson;
		c.setHungry();
		stateCB.setEnabled(false);

	    }else if(currentPerson instanceof WaiterAgent){
		WaiterAgent w = (WaiterAgent) currentPerson;
		w.setBreakStatus(stateCB.isSelected());
	    }
	}
	else if (e.getSource() == addTable)
	{
		try {
			System.out.println("[Gautam] Add Table!");
			//String XPos = JOptionPane.showInputDialog("Please enter X Position: ");
			//String YPos = JOptionPane.showInputDialog("Please enter Y Position: ");
			//String size = JOptionPane.showInputDialog("Please enter Size: ");
			//restPanel.addTable(10, 5, 1);
			//restPanel.addTable(Integer.valueOf(YPos).intValue(), Integer.valueOf(XPos).intValue(), Integer.valueOf(size).intValue());
			restPanel.addTable();
		}
		catch(Exception ex) {
			System.out.println("Unexpected exception caught in during setup:"+ ex);
		}
	}
	else if (e.getSource() == confirmCust)
	{
		String tmpName = custNameTF.getText();
		double tmpMoney = Double.parseDouble(moneyTF.getText());
		String order = orderCoBo.getSelectedItem().toString();
		boolean tmpIfWait= waitCB.isSelected();
		restPanel.addCustomer(tmpName, tmpMoney, order,tmpIfWait);
	}
	else if (e.getSource() == confirmMenu)
	{
		String tmpName = foodNameTF.getText();
		double tmpPrice =  Double.parseDouble(foodPriceTF.getText());
		if(newMenuItem)
		{
			restPanel.addItemToMenu(tmpName,tmpPrice);
			addMenuItemToComboBox(tmpName);
			addMarketItemToComboBox(tmpName);
		}
		else
		{
			restPanel.updateItemToMenu(tmpName,tmpPrice);
		}
	}
		else if (e.getSource()== confirmMKT)
	{
		int tmpAmount = Integer.parseInt(foodAmountTF.getText());
		int tmpMax = Integer.parseInt(foodMaxTF.getText());
		int tmpThreshold = Integer.parseInt(foodThresholdTF.getText());
		int prepTime = Integer.parseInt(pTimeTF.getText());
		ArrayList<Boolean> marketsBool = new ArrayList<Boolean>();
		for(int i=0;i<5;i++)
		{
			marketsBool.add(marketCBs.get(i).isSelected());
		}
		restPanel.updateItemToCook(marketItemSelected, tmpAmount, tmpMax, tmpThreshold, prepTime,marketsBool);
		//restPanel.update
	}
	    
    }

	public void valueChanged(ListSelectionEvent e) { //Senses any changes in the list selection.
		
	}
    /** Message sent from a customer agent to enable that customer's 
     * "I'm hungery" checkbox.
     * @param c reference to the customer */
    public void setCustomerEnabled(CustomerAgent c){
	if(currentPerson instanceof CustomerAgent){
	    CustomerAgent cust = (CustomerAgent) currentPerson;
	    if(c.equals(cust)){
		stateCB.setEnabled(true);
		stateCB.setSelected(false);
	    }
	}
    }
    public void addActLis()
    {
    	orderCoBo.addActionListener(this);
    	waitCB.addActionListener(this);
    	confirmCust.addActionListener(this);
    	foodCoBo.addActionListener(this);
    	foodCoBo.addItemListener(this);
    	confirmMenu.addActionListener(this);
    	marketCoBo.addActionListener(this);
    	marketCoBo.addItemListener(this);
    	for(int i=0;i<marketCBs.size();i++)
    	{
    		marketCBs.get(i).addActionListener(this);
    	}
		confirmMKT.addActionListener(this);
    }
	public void setUpMarketCB()
	{
		for(int i=0;i<2;i++)
		{
			JPanel tmpPanel = new JPanel();
			tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.X_AXIS));
			JLabel tmpLabel1= new JLabel("Market "+2*i);
			marketCBs.add(new JCheckBox());
			tmpPanel.add(tmpLabel1);
			tmpPanel.add(marketCBs.get(2*i));
			JLabel tmpLabel2= new JLabel("Market "+(2*i+1));
			marketCBs.add(new JCheckBox());
			tmpPanel.add(tmpLabel2);
			tmpPanel.add(marketCBs.get(2*i+1));
			marketPN.add(tmpPanel);
		}
		JPanel tmpPanel = new JPanel();
		tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.X_AXIS));
		JLabel tmpLabel1= new JLabel("Market "+4);
		marketCBs.add(new JCheckBox());
		tmpPanel.add(tmpLabel1);
		tmpPanel.add(marketCBs.get(4));
		marketPN.add(tmpPanel);
	}
    /** Main routine to get gui started */
    public static void main(String[] args){
	RestaurantGui gui = new RestaurantGui();
	gui.setVisible(true);
	gui.setResizable(false);
    }

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource()==foodCoBo)
		{
			if (foodCoBo.getSelectedIndex() == 0) //Disables the delete button in the first option to create a new part and sets all fields to empty or default.
			{
//				deleteMenu.setEnabled(false);
				foodNameTF.setText("");
				foodPriceTF.setText("");
				newMenuItem=true;
			}
			else if (foodCoBo.getSelectedIndex() > 0)
			{
				newMenuItem=false;
				menuItemSelected = foodCoBo.getSelectedItem().toString();
				foodNameTF.setText(menuItemSelected);
				foodPriceTF.setText(String.valueOf(restPanel.getMenu().getPrice(menuItemSelected.toString())));
				//System.out.println("Food Name: " + menuItemSelected);
			}						
		}
		if ( e.getSource()==marketCoBo)
		{
				marketItemSelected = marketCoBo.getSelectedItem().toString();
				foodAmountTF.setText(String.valueOf(restPanel.getFoodAmount(marketItemSelected)));
				foodMaxTF.setText(String.valueOf(restPanel.getFoodMax(marketItemSelected)));
				foodThresholdTF.setText(String.valueOf(restPanel.getFoodThreshold(marketItemSelected)));
				pTimeTF.setText(String.valueOf(restPanel.getPrepTime(marketItemSelected)));
				for(int i=0;i<5;i++)
				{

					marketCBs.get(i).setSelected(restPanel.getMarketAvailability(i,marketItemSelected));
					
				}
				//System.out.println("Food Name: " + menuItemSelected);					
		}
	}
}
