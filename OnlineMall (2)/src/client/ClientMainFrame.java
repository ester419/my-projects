package client;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import common.CMD;
import common.Client;
import common.Command;
import common.Goods;
import common.Message;
import common.Shop;
import common.Utils;

public class ClientMainFrame extends JFrame {


	private static final long serialVersionUID = 7952439640530949282L;
	private JPanel contentPane;
	private JTextField textFieldUserName;
	
	//���ڱ��ز���ʱ���ͻ��˵Ķ˿ں�Ҫ��һ��
	private static int clientPort = new Random().nextInt(10000)+1024;
	//ÿ���ͻ���ֻ��һ���������ݰ��׽���
	private static DatagramSocket receiveSocket = null;
	private boolean enterShopFlag = false;
	private boolean connectFlag = false;
	private boolean addGoodsFlag = true;
	private Shop myShop = new Shop(null);
	private String userName;
	private Vector<Shop> shopList = new Vector<Shop>();

	
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.out.println(InetAddress.getLocalHost());
					ClientMainFrame frame = new ClientMainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientMainFrame() {
		setTitle("Client : Off");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textFieldUserName = new JTextField();
		textFieldUserName.setBounds(88, 10, 133, 21);
		contentPane.add(textFieldUserName);
		textFieldUserName.setColumns(10);
		
		final JButton btnConnect = new JButton("connect");
		btnConnect.setBounds(228, 9, 93, 23);
		contentPane.add(btnConnect);
		
		final JButton btnQuit = new JButton("quit");
		btnQuit.setEnabled(false);
		btnQuit.setBounds(331, 9, 93, 23);
		contentPane.add(btnQuit);
		
		JLabel lblNewLabel_1 = new JLabel("Message Records");
		lblNewLabel_1.setBounds(10, 45, 113, 15);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Command");
		lblNewLabel_2.setBounds(10, 199, 73, 15);
		contentPane.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("ShopName        UserName        UserID");
		lblNewLabel_3.setBounds(340, 199, 350, 15);
		contentPane.add(lblNewLabel_3);
		
		final JButton btnSend = new JButton("Send");
		btnSend.setEnabled(false);
		btnSend.setBounds(340, 263, 93, 57);
		contentPane.add(btnSend);
		
		final JComboBox<String> comboBoxReceiver = new JComboBox<String>();
		comboBoxReceiver.setBounds(340, 225, 240, 21);
		contentPane.add(comboBoxReceiver);
		
		JLabel lblNewLabel_4 = new JLabel("User Name");
		lblNewLabel_4.setBounds(10, 10, 73, 15);
		contentPane.add(lblNewLabel_4);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 70, 560, 119);
		contentPane.add(scrollPane);
		
		final JTextArea textAreaMsgRecords = new JTextArea();
		textAreaMsgRecords.setEditable(false);
		scrollPane.setViewportView(textAreaMsgRecords);
		
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(10, 224, 298, 96);
		contentPane.add(scrollPane_3);
		
		final JTextArea textAreaSentence = new JTextArea();
		scrollPane_3.setViewportView(textAreaSentence);
		
		//��ֹ�����С��
		setResizable(false);
		//ֻ��һ�ζ˿ڣ���ֹ�ظ���
		try {
			receiveSocket = new DatagramSocket(clientPort);
		} catch (SocketException e2) {
			e2.printStackTrace();
		}
		
		btnConnect.addActionListener(new ActionListener() {
			//���������¼�������
			public void actionPerformed(ActionEvent e) {
								
				userName = textFieldUserName.getText();
				//δ�����û���
				if(userName.equals("")){
					JOptionPane.showMessageDialog(textAreaMsgRecords,"δ�����û���");
					return;
				}
				
				/*������ӷ�������������ť��Ҫ��������:
				 * 1.���߷�������ǰ�û�����ip��ַ���˿ںŵ���Ϣ��������˸��㷢��Ϣ����������֪�������ķ���
				 * 2.���ݷ��������ͻصĵ�ǰ���ߵ��û��б�ˢ�¿ͻ��˵��û��б�
				 */		
				try {

					//���û���������û��ĵ�ַ��Ϣ�ۺϳ�msg
					Message msg = new Message(userName, CMD.CMD_USERLOGIN, new Client(userName,InetAddress.getLocalHost(),clientPort));
					Utils.sendMessage(msg,InetAddress.getByName(Utils.serverIP),Utils.serverPort);
				}catch (Exception e1) {
						JOptionPane.showMessageDialog(textAreaMsgRecords,e1.getMessage());
						e1.printStackTrace();
					}
					
				//ÿ�����ӷ�����������ֹͣ��ǩ
				connectFlag = true;
				//֮����Ҫ��ͣ�ĵȴ��������˵���Ϣ��������ֹͣ��������Ϣ�ȣ��������ö��߳�
				new Thread(new Runnable() {
					@Override
					public void run() {	
	
						while(connectFlag){
							//��ȡ�������Ļظ�����
							Message msg = null;
							try {
								msg = Utils.receiveMessage(receiveSocket);
								System.out.println(msg.getCmd());
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							//���ݲ�ͬ����Ϣ��������ͬ�ķ�Ӧ
							switch(msg.getCmd()){
							
							case CMD_USERALREADYEXISTS:
								//�û��Ѿ�����
				            	JOptionPane.showMessageDialog(textAreaMsgRecords,"�����˺��Ѿ��ڱ𴦵�¼");
				            	return;
				            	
							case CMD_LOGINSUCCESS:
								btnSend.setEnabled(true);
								btnConnect.setEnabled(false);
								btnQuit.setEnabled(true);
								textFieldUserName.setEditable(false);
								String tmp = textAreaMsgRecords.getText();
								if(!tmp.equals(""))
									tmp += "\n";
								textAreaMsgRecords.setText(tmp + Utils.getCurrentFormatTime() + msg.getClient().getClientName() + "�ɹ���¼������\n");
								setTitle("Client : ON");
								//��Ȼ�ͷ����������ˣ���ô�˳�ʱ������߷�������ȡ��FrameĬ�ϵ��˳�����
								setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
								break;
								
							case CMD_SERVERSTOP:
								btnSend.setEnabled(false);
								textFieldUserName.setEditable(true);
								btnConnect.setEnabled(true);
								btnQuit.setEnabled(false);
								setTitle("Client : Off");
								comboBoxReceiver.removeAllItems();
								textAreaMsgRecords.setText(textAreaMsgRecords.getText()+"\n"+Utils.getCurrentFormatTime()+"������ֹͣ����");
								//�������ӱ�־Ϊֹͣfalse����ʾ���ٽ��ձ��ģ��ͻ���ֹͣ
								connectFlag = false;
								
								break;
								
							//�����û����ߣ�������֪ͨ
							case CMD_USERQUIT:
								System.out.print("quit");
								Client tc = msg.getClient();
								textAreaMsgRecords.setText(textAreaMsgRecords.getText()+"\n"+Utils.getCurrentFormatTime()+tc.getClientName()+"�˳���¼");
								//�����û��б������˵�
								@SuppressWarnings("unchecked")
								Vector<Client> v1 = (Vector<Client>)msg.getCarrier();
								//�������ԭ�����Ȼ����и���
							    break;
							case CMD_SENDSHOPLIST:
								//�����û��б������˵����̵���Ϣ
								Vector<Shop> receiveShop = (Vector<Shop>)msg.getCarrier();
								for (Shop v : receiveShop) {
								shopList.add(v);
								}
								//�������ԭ�����Ȼ����и���
								comboBoxReceiver.removeAllItems();
								for(Shop shop : shopList){	
									comboBoxReceiver.addItem(shop.getShopName() + "         " + shop.getOwner().getClientName() + "        " + shop.getOwner().getClientIp().getHostAddress().toString());
								}
								break;	
							//���չ���Ա�û���������Ϣ
							case CMD_SENDCOMMAND:
								//��ȡ������������
								Command command = (Command) msg.getCarrier();
								String cmd = command.getText();
								if (cmd.equals("/opennewshop")) {
									addGoodsFlag = true;
									tmp = "���̵��Ѿ���ͨ,������������Ʒ��Ϣ��" + "\n";
									textAreaMsgRecords.setText(textAreaMsgRecords.getText()+tmp);
									try {
									myShop.setOwner(new Client(userName,InetAddress.getLocalHost(),clientPort));
									String shopName = myShop.getOwner().getClientIp().getHostAddress().toString();
									myShop.setShopName(shopName);
									comboBoxReceiver.removeAllItems();
									comboBoxReceiver.addItem(myShop.getShopName() + "        " + myShop.getOwner().getClientName() + "        " + shopName);
								}catch (Exception e) {
									JOptionPane.showMessageDialog(textAreaMsgRecords,e.getMessage());
									e.printStackTrace();
									}
								}else if (cmd.equals("/enter")) {
									enterShopFlag = true;
									try {
										//����Ϣ��ʽ���ݣ�����û��ĵ�ַ��Ϣ�ۺϳ�msg
										Message msg1 = new Message(myShop, CMD.CMD_SENDSHOP, null);
										Utils.sendMessage(msg1, InetAddress.getByName(Utils.serverIP), Utils.serverPort);
									}catch (Exception e1) {
											JOptionPane.showMessageDialog(textAreaMsgRecords,e1.getMessage());
											e1.printStackTrace();
											}
								}else if (cmd.equals("/close")){
									tmp = "�����̵��ѱ��ر�" + "\n";
									textAreaMsgRecords.setText(textAreaMsgRecords.getText()+tmp);
									for (Client c : myShop.getCustomers()) {
										try {
											Message msg4 = new Message(null, CMD.CMD_CLOSE, new Client(userName,InetAddress.getLocalHost(),clientPort));
											Utils.sendMessage(msg4, c.getClientIp(), c.getClientPort());
										}catch (Exception e1) {
											JOptionPane.showMessageDialog(textAreaMsgRecords,e1.getMessage());
											e1.printStackTrace();
											}
									}
								}else if ((cmd.equals("/goods") && enterShopFlag)) {
									try {
										Message msg4 = new Message(myShop.getGoods(), CMD.CMD_SENDGOODS, new Client(userName,InetAddress.getLocalHost(),clientPort));
										Utils.sendMessage(msg4, InetAddress.getByName(Utils.serverIP), Utils.serverPort);
									}catch (Exception e1) {
											JOptionPane.showMessageDialog(textAreaMsgRecords,e1.getMessage());
											e1.printStackTrace();
											}
								}else if (cmd.equals("/customers") && enterShopFlag == true) {
									try {
									Message msg3 = new Message(myShop.getCustomers(), CMD.CMD_SENDCUSTOMERS, null);
									Utils.sendMessage(msg3, InetAddress.getByName(Utils.serverIP), Utils.serverPort);
								    }catch (Exception e1) {
									JOptionPane.showMessageDialog(textAreaMsgRecords,e1.getMessage());
									e1.printStackTrace();
									}
								}else if (cmd.matches("/msg.*")){
									textAreaMsgRecords.setText(cmd.substring(5) + "\n");
								}
								break;
								
							case CMD_ENTERSHOP:
								tmp = "�˿�" + ((Client)msg.getCarrier()).getClientName() + "�����������̵�\n";
								textAreaMsgRecords.setText(tmp);
								myShop.addCustomers((Client)msg.getCarrier());
								break;
							
							case CMD_SENDGOODS:
								textAreaMsgRecords.setText("");
								String s = "";
								textAreaMsgRecords.setText("GoodName\t\tGoodID\t\tGoodPrice\n");
								Vector <Goods> goodsAvailable = (Vector <Goods>)msg.getCarrier();
								for (Goods g : goodsAvailable) {
									s += g.getGoodName() + "\t\t" + g.getGoodID() + "\t\t" + g.getPrice() + "\n";
								}
								textAreaMsgRecords.setText(textAreaMsgRecords.getText() + s);
								break;
								
							case CMD_SENDCUSTOMERS:
								textAreaMsgRecords.setText("");
								s = "";
								textAreaMsgRecords.setText("CustomerName\tCustomerID\n");
								Vector <Client> customers = (Vector <Client>)msg.getCarrier();
								for (Client c : customers) {
									s += c.getClientName() + "\t\t" + c.getClientIp().getHostAddress() + "\n";
								}
								textAreaMsgRecords.setText(textAreaMsgRecords.getText() + s);
								break;
								
							case CMD_LEAVESHOP:
								myShop.removeCustomer((Client)msg.getCarrier());
								textAreaMsgRecords.setText("�˿�" + ((Client)msg.getCarrier()).getClientName() + "�뿪������̵�");
								break;
								
							case CMD_CLOSE:
								tc = msg.getClient();
								tmp = tc.getClientName() + "���̳��ѱ��ر�\n";
								textAreaMsgRecords.setText(tmp);
							    break;
							case CMD_NOTENTERWARNING:
								textAreaMsgRecords.setText("����û�н����κ��̵�");
								break;
							case CMD_SENDGOODSSOLD:
								textAreaMsgRecords.setText("");
								String s1 = "";
								double sum = 0;
								textAreaMsgRecords.setText("The number of goods sold\t\tTurnover\n");
								Vector <Goods> goodsSold = (Vector <Goods>)msg.getCarrier();
								for (Goods g : goodsSold) {
									sum += g.getPrice();
								}
								s1 = goodsSold.size() + "\t\t\t" + sum;
								textAreaMsgRecords.setText(textAreaMsgRecords.getText() + s1);
								}
							} 
						}
					}).start();
			}});
		
		//�˳���ť����Ӧ����
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String userName = textFieldUserName.getText();
				//������������˳���Ϣ
				try {
					//���û���������û��ĵ�ַ��Ϣ�ۺϳ�msg
					Message msg = new Message(null, CMD.CMD_USERQUIT, new Client(userName,InetAddress.getLocalHost(),clientPort));
					Utils.sendMessage(msg,InetAddress.getByName(Utils.serverIP),Utils.serverPort);
				}catch (Exception e1) {
						JOptionPane.showMessageDialog(textAreaMsgRecords,e1.getMessage());
						e1.printStackTrace();
						}
				//�ָ���ؿؼ���״̬
				btnSend.setEnabled(false);
				btnConnect.setEnabled(true);
				btnQuit.setEnabled(false);
				textAreaMsgRecords.setText(textAreaMsgRecords.getText()+"\n"+Utils.getCurrentFormatTime()+userName+"�˳���¼");
				setTitle("Client : Off");
				textFieldUserName.setEditable(true);
				comboBoxReceiver.removeAllItems();
				//�˳�����
				System.exit(0);
			}
		});
		
		//������Ϣ����Ӧ����
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//��ȡ�û��������Ϣ
				String cmd = textAreaSentence.getText();
				//�û�δ�����κ���Ϣ
				if(cmd.equals("")){
					JOptionPane.showMessageDialog(textAreaMsgRecords,"�ı���Ϊ�գ����ܷ���");
					return;
				}
				if (cmd.equals("/shops")) {
					try {
					Message msg = new Message(null, CMD.CMD_SENDSHOPLIST, new Client(userName,InetAddress.getLocalHost(),clientPort));
					Utils.sendMessage(msg,InetAddress.getByName(Utils.serverIP),Utils.serverPort);
					}catch (Exception e1) {
						JOptionPane.showMessageDialog(textAreaMsgRecords,e1.getMessage());
						e1.printStackTrace();
						}
				}else {
				String receiver = ((String)comboBoxReceiver.getSelectedItem()).split("\\s+")[1];
				Shop receiverShop = new Shop(receiver);
				String userName = textFieldUserName.getText();
				receiverShop.setShopName(((String)comboBoxReceiver.getSelectedItem()).split("\\s+")[2]);
				try {
					//���������ݣ�����û��ĵ�ַ��Ϣ�ۺϳ�msg���͸�������
					Message msg = new Message(new Command(receiver, cmd), CMD.CMD_SENDMESSAGE, new Client(userName,InetAddress.getLocalHost(),clientPort));
					Utils.sendMessage(msg,InetAddress.getByName(Utils.serverIP),Utils.serverPort);
				}catch (Exception e1) {
						JOptionPane.showMessageDialog(textAreaMsgRecords,e1.getMessage());
						e1.printStackTrace();
						}
				}
				if (cmd.matches("/addgoods.*")) {
					String[] s3 = cmd.split(" ");
					String goodID = s3[1];
					String goodName = s3[2];
					double goodPrice = Double.valueOf(s3[3]);
					myShop.addGoods(new Goods(goodID, goodName, goodPrice));
				}else if (cmd.equals("/leave")) {
					textAreaMsgRecords.setText("");
				}
				
			}
		});
		
	}
	
}
