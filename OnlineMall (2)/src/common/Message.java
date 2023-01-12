package common;

import java.io.Serializable;

/*
 * Socket�����У��趨��Ϣ�ĸ�ʽ�����ڽ�����
 * ʵ��Serializable�ӿڱ��ڴ���
 * */
public class Message  implements Serializable{
	
	private static final long serialVersionUID = 1L;  
	
	//���壬��ͬ�������Ӧ��ͬ������
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
