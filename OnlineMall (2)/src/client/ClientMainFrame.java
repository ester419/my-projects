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
	
	//由于本地测试时，客户端的端口号要不一致
	private static int clientPort = new Random().nextInt(10000)+1024;
	//每个客户端只有一个接收数据包套接字
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
		
		//禁止最大最小化
		setResizable(false);
		//只绑定一次端口，防止重复绑定
		try {
			receiveSocket = new DatagramSocket(clientPort);
		} catch (SocketException e2) {
			e2.printStackTrace();
		}
		
		btnConnect.addActionListener(new ActionListener() {
			//启动服务事件监听器
			public void actionPerformed(ActionEvent e) {
								
				userName = textFieldUserName.getText();
				//未输入用户名
				if(userName.equals("")){
					JOptionPane.showMessageDialog(textAreaMsgRecords,"未输入用户名");
					return;
				}
				
				/*点击连接服务器服务器按钮，要做两件事:
				 * 1.告诉服务器当前用户名，ip地址，端口号等信息。如果有人给你发信息，服务器就知道该往哪发。
				 * 2.根据服务器发送回的当前在线的用户列表，刷新客户端的用户列表
				 */		
				try {

					//将用户名，命令，用户的地址信息综合成msg
					Message msg = new Message(userName, CMD.CMD_USERLOGIN, new Client(userName,InetAddress.getLocalHost(),clientPort));
					Utils.sendMessage(msg,InetAddress.getByName(Utils.serverIP),Utils.serverPort);
				}catch (Exception e1) {
						JOptionPane.showMessageDialog(textAreaMsgRecords,e1.getMessage());
						e1.printStackTrace();
					}
					
				//每次连接服务器后，设置停止标签
				connectFlag = true;
				//之后需要不停的等待服务器端的消息（服务器停止，接收信息等），所以用多线程
				new Thread(new Runnable() {
					@Override
					public void run() {	
	
						while(connectFlag){
							//获取服务器的回复报文
							Message msg = null;
							try {
								msg = Utils.receiveMessage(receiveSocket);
								System.out.println(msg.getCmd());
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							//根据不同的消息，作出不同的反应
							switch(msg.getCmd()){
							
							case CMD_USERALREADYEXISTS:
								//用户已经存在
				            	JOptionPane.showMessageDialog(textAreaMsgRecords,"您的账号已经在别处登录");
				            	return;
				            	
							case CMD_LOGINSUCCESS:
								btnSend.setEnabled(true);
								btnConnect.setEnabled(false);
								btnQuit.setEnabled(true);
								textFieldUserName.setEditable(false);
								String tmp = textAreaMsgRecords.getText();
								if(!tmp.equals(""))
									tmp += "\n";
								textAreaMsgRecords.setText(tmp + Utils.getCurrentFormatTime() + msg.getClient().getClientName() + "成功登录服务器\n");
								setTitle("Client : ON");
								//既然和服务器连接了，那么退出时必须告诉服务器，取消Frame默认的退出功能
								setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
								break;
								
							case CMD_SERVERSTOP:
								btnSend.setEnabled(false);
								textFieldUserName.setEditable(true);
								btnConnect.setEnabled(true);
								btnQuit.setEnabled(false);
								setTitle("Client : Off");
								comboBoxReceiver.removeAllItems();
								textAreaMsgRecords.setText(textAreaMsgRecords.getText()+"\n"+Utils.getCurrentFormatTime()+"服务器停止服务");
								//设置连接标志为停止false，表示不再接收报文，客户端停止
								connectFlag = false;
								
								break;
								
							//其他用户下线，服务器通知
							case CMD_USERQUIT:
								System.out.print("quit");
								Client tc = msg.getClient();
								textAreaMsgRecords.setText(textAreaMsgRecords.getText()+"\n"+Utils.getCurrentFormatTime()+tc.getClientName()+"退出登录");
								//更新用户列表下拉菜单
								@SuppressWarnings("unchecked")
								Vector<Client> v1 = (Vector<Client>)msg.getCarrier();
								//首先清空原来的项，然后进行更新
							    break;
							case CMD_SENDSHOPLIST:
								//更新用户列表下拉菜单的商店信息
								Vector<Shop> receiveShop = (Vector<Shop>)msg.getCarrier();
								for (Shop v : receiveShop) {
								shopList.add(v);
								}
								//首先清空原来的项，然后进行更新
								comboBoxReceiver.removeAllItems();
								for(Shop shop : shopList){	
									comboBoxReceiver.addItem(shop.getShopName() + "         " + shop.getOwner().getClientName() + "        " + shop.getOwner().getClientIp().getHostAddress().toString());
								}
								break;	
							//接收管理员用户发来的消息
							case CMD_SENDCOMMAND:
								//获取服务器的命令
								Command command = (Command) msg.getCarrier();
								String cmd = command.getText();
								if (cmd.equals("/opennewshop")) {
									addGoodsFlag = true;
									tmp = "新商店已经开通,您可以设置商品信息了" + "\n";
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
										//将消息格式内容，命令，用户的地址信息综合成msg
										Message msg1 = new Message(myShop, CMD.CMD_SENDSHOP, null);
										Utils.sendMessage(msg1, InetAddress.getByName(Utils.serverIP), Utils.serverPort);
									}catch (Exception e1) {
											JOptionPane.showMessageDialog(textAreaMsgRecords,e1.getMessage());
											e1.printStackTrace();
											}
								}else if (cmd.equals("/close")){
									tmp = "您的商店已被关闭" + "\n";
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
								tmp = "顾客" + ((Client)msg.getCarrier()).getClientName() + "进入了您的商店\n";
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
								textAreaMsgRecords.setText("顾客" + ((Client)msg.getCarrier()).getClientName() + "离开了你的商店");
								break;
								
							case CMD_CLOSE:
								tc = msg.getClient();
								tmp = tc.getClientName() + "的商城已被关闭\n";
								textAreaMsgRecords.setText(tmp);
							    break;
							case CMD_NOTENTERWARNING:
								textAreaMsgRecords.setText("您还没有进入任何商店");
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
		
		//退出按钮的响应函数
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String userName = textFieldUserName.getText();
				//向服务器发送退出消息
				try {
					//将用户名，命令，用户的地址信息综合成msg
					Message msg = new Message(null, CMD.CMD_USERQUIT, new Client(userName,InetAddress.getLocalHost(),clientPort));
					Utils.sendMessage(msg,InetAddress.getByName(Utils.serverIP),Utils.serverPort);
				}catch (Exception e1) {
						JOptionPane.showMessageDialog(textAreaMsgRecords,e1.getMessage());
						e1.printStackTrace();
						}
				//恢复相关控件的状态
				btnSend.setEnabled(false);
				btnConnect.setEnabled(true);
				btnQuit.setEnabled(false);
				textAreaMsgRecords.setText(textAreaMsgRecords.getText()+"\n"+Utils.getCurrentFormatTime()+userName+"退出登录");
				setTitle("Client : Off");
				textFieldUserName.setEditable(true);
				comboBoxReceiver.removeAllItems();
				//退出程序
				System.exit(0);
			}
		});
		
		//发送消息的响应函数
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//获取用户输入的消息
				String cmd = textAreaSentence.getText();
				//用户未输入任何消息
				if(cmd.equals("")){
					JOptionPane.showMessageDialog(textAreaMsgRecords,"文本框为空，不能发送");
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
					//将命令内容，命令，用户的地址信息综合成msg发送给服务器
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
