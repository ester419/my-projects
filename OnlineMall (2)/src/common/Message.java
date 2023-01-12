package common;

import java.io.Serializable;

/*
 * Socket传输中，设定消息的格式，便于解析。
 * 实现Serializable接口便于传输
 * */
public class Message  implements Serializable{
	
	private static final long serialVersionUID = 1L;  
	
	//载体，不同的命令对应不同的载体
	private Object carrier;
	private CMD cmd;
	private Client client;
	
	public Message(Object carrier, CMD cmd, Client client) {
		super();
		this.carrier = carrier;
		this.cmd = cmd;
		this.client = client;
	}

	public Object getCarrier() {
		return carrier;
	}

	public CMD getCmd() {
		return cmd;
	}

	public Client getClient() {
		return client;
	}
	
	
	
	
	

}
