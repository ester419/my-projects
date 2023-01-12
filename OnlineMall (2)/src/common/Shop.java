package common;

import java.io.Serializable;
import java.util.Vector;

public class Shop implements  Serializable{
	
	private static final long serialVersionUID = -1554129347645007834L;
	
	private Vector<Goods> goods = new Vector<Goods>();
	private Vector<Client> customer = new Vector<Client>();
	private Vector<Goods> goodsSold = new Vector<Goods>();
	private String shopName;
	private Client owner;
	
	public Shop(String shopName){
		this.shopName = shopName;
	}
	
	public String getShopName() {
		return shopName;
	}
	
	public Client getOwner() {
		return owner;
	}
	
	public void setGoods(Vector<Goods> goods) {
		this.goods = goods;
	}
	
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	
	public void setOwner(Client c) {
		 owner = c;
	}
	
	public void addGoods(Goods goods) {
		this.goods.add(goods);
	}
	
	public void addGoodsSold(Goods goods) {
		this.goodsSold.add(goods);
	}
	
	public void addCustomers(Client customer) {
		this.customer.add(customer);
	}
	
	public void removeCustomer(Client customer) {
		this.customer.remove(this.customer.removeElement(customer));
	}
	
	public Vector<Goods> getGoods() {
		return goods;
	}
	
	public Vector<Goods> getGoodsSold(){
		return goodsSold;
	}
	
	public Vector<Client> getCustomers(){
		return customer;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null)
			return false;
		//如果用户的姓名一样，则它们相同
		if(obj instanceof Shop){
			return shopName.equals(((Shop)obj).shopName);
		}			
		return false;
	}

}
