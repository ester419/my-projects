package common;

import java.io.Serializable;

public class Goods implements Serializable{
	
	private static final long serialVersionUID = -1554129347645007834L;
	private String goodsID;
	private String goodsName;
	private double price;
	
	public Goods(String goodsID, String goodsName, double price) {
		this.goodsID = goodsID;
		this.goodsName = goodsName;
		this.price = price;
	}
	
	public Goods(String goodsID) {
		this.goodsID = goodsID;
	}
	
	public String getGoodID() {
		return goodsID;
	}
	
	public String getGoodName() {
		return goodsName;
	}
	
	public double getPrice() {
		return price;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null)
			return false;
		//如果用户的姓名一样，则它们相同
		if(obj instanceof Goods){
			return goodsID.equals(((Goods)obj).goodsID);
		}			
		return false;
	}
}
