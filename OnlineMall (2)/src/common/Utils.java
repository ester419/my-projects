package common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	
	public static String serverIP = "127.0.0.1";
	public static int serverPort = 8765;
	
	public static String getCurrentFormatTime(){
		SimpleDateFormat df = new SimpleDateFormat("[MM-dd HH:mm:ss]: ");//设置日期格式
		return df.format(new Date());
	}
	
	public static void sendMessage(Message msg,InetAddress address,int port) throws Exception{
		byte[] data = new byte[1024*1024];
		//设置对象输入流
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		ObjectOutputStream bo = new ObjectOutputStream(bs);
		bo.writeObject(msg);
		//将流转化为字节数组
		data = bs.toByteArray();  
		//设置数据包套接字，并发送
		DatagramSocket sendSocket = new DatagramSocket();
		DatagramPacket sendPack = new DatagramPacket(data, data.length,address,port);
		sendSocket.send(sendPack);
	}
	
	/*
	 * 发送数据报时，注意不需要重复绑定端口
	 * */
	public static Message receiveMessage(DatagramSocket receiveSocket) throws Exception{
		//绑定随机的端口，然后接受服务器的信息
		byte[] data = new byte[1024*1024];
		DatagramPacket receivePack = new DatagramPacket(data, data.length);
		receiveSocket.receive(receivePack);

		//解析数据包中的Message对象
		ByteArrayInputStream bis = new ByteArrayInputStream(receivePack.getData());
		ObjectInputStream os = new ObjectInputStream(bis);
		return (Message)os.readObject();
	}
}
