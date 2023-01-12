package common;

import java.io.Serializable;
import java.net.InetAddress;


public class Client implements Serializable{
	
	private static final long serialVersionUID = -4255412944764507834L;
	
	//用户名
	String clientName;
	//用户IP地址
	InetAddress clientIpAddress;
	//用户端口
	int clientPort;	
	private Shop shop = null;
	private String shopFlag = "";
	private boolean enterShopFlag = false;
	
	public Client() {
		
	}

	public Client(String clientName, InetAddress clientIpAddress, int clientPort) {
		super();
		this.clientName = clientName;
		this.clientIpAddress = clientIpAddress;
		this.clientPort = clientPort;
	}

	public String getClientName() {
		return clientName;
	}

	public InetAddress getClientIp() {
		return clientIpAddress;
	}

	public int getClientPort() {
		return clientPort;
	}
	
	public Shop getShop() {
		return shop;
	}
	
	public String getShopFlag() {
		return shopFlag;
	}
	
	public void openNewShop(String shopName) {
		Shop shop = new Shop(shopName);
		this.shop = shop;
		shopFlag = "*";
	}
	
	public void setShopFlag(String flag) {
		this.shopFlag = flag;
	}
	
	public void setEnterShopFlag(boolean flag) {
		enterShopFlag = flag;
	}
	
	public boolean getEnterShopFlag() {
		return enterShopFlag;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null)
			return false;
		//如果用户的姓名一样，则它们相同
		if(obj instanceof Client){
			return clientName.equals(((Client)obj).clientName);
		}			
		return false;
	}
	
	
}

