package common;

import java.io.Serializable;

//将用户在TextArea中输入的命令和combobox中选取的消息接收客户端封装
public class Command implements Serializable{
	
	private static final long serialVersionUID = -1356206790228754726L;
	
	private String receiver;
	private String text;
	private String sendTime;
	
	public String getReceiver() {
		return receiver;
	}
	public String getText() {
		return text;
	}
	public String getSendTime(){
		return sendTime;
	}
	
	public Command(String receiver, String text) {
		super();
		this.receiver = receiver;
		this.text = text;
	}
	
	
}
