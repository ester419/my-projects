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
		SimpleDateFormat df = new SimpleDateFormat("[MM-dd HH:mm:ss]: ");//�������ڸ�ʽ
		return df.format(new Date());
	}
	
	public static void sendMessage(Message msg,InetAddress address,int port) throws Exception{
		byte[] data = new byte[1024*1024];
		//���ö���������
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		ObjectOutputStream bo = new ObjectOutputStream(bs);
		bo.writeObject(msg);
		//����ת��Ϊ�ֽ�����
		data = bs.toByteArray();  
		//�������ݰ��׽��֣�������
		DatagramSocket sendSocket = new DatagramSocket();
		DatagramPacket sendPack = new DatagramPacket(data, data.length,address,port);
		sendSocket.send(sendPack);
	}
	
	/*
	 * �������ݱ�ʱ��ע�ⲻ��Ҫ�ظ��󶨶˿�
	 * */
	public static Message receiveMessage(DatagramSocket receiveSocket) throws Exception{
		//������Ķ˿ڣ�Ȼ����ܷ���������Ϣ
		byte[] data = new byte[1024*1024];
		DatagramPacket receivePack = new DatagramPacket(data, data.length);
		receiveSocket.receive(receivePack);

		//�������ݰ��е�Message����
		ByteArrayInputStream bis = new ByteArrayInputStream(receivePack.getData());
		ObjectInputStream os = new ObjectInputStream(bis);
		return (Message)os.readObject();
	}
}
