package server;


import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import common.CMD;
import common.Command;
import common.Client;
import common.Message;
import common.Utils;
import common.Goods;
import common.Shop;;

public class ServerMainFrame extends JFrame{


	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private static Vector<Client> userList = new Vector<Client>();	//保存登录用户信息
	private static Vector<Shop> shopList = new Vector<Shop>();
	//用于接收的upd套接字
	private static DatagramSocket receiveSocket = null; 
	private static boolean stopFlag = false;
	private String shopName = "";
	private String receiveName = "";
	private Shop shop1 = new Shop("");


	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerMainFrame frame = new ServerMainFrame();
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
	public ServerMainFrame() {
		setTitle("Server Stop");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 377);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Service");
		lblNewLabel.setBounds(10, 10, 54, 15);
		contentPane.add(lblNewLabel);
		
		final JButton btnStartService = new JButton("start service");
		btnStartService.setBounds(86, 6, 155, 23);		
		contentPane.add(btnStartService);
		
		final JButton btnEndService = new JButton("stop service");
		btnEndService.setEnabled(false);
		btnEndService.setBounds(269, 6, 155, 23);
		contentPane.add(btnEndService);
		
		JLabel lblNewLabel_1 = new JLabel("List");
		lblNewLabel_1.setBounds(10, 35, 54, 15);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Command");
		lblNewLabel_2.setBounds(10, 162, 131, 15);
		contentPane.add(lblNewLabel_2);
		
		//禁止最大最小化
		setResizable(false);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(12, 187, 300, 140);
		contentPane.add(scrollPane_2);
		
		final JTextArea textAreaCommand = new JTextArea();
		scrollPane_2.setViewportView(textAreaCommand);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setEnabled(false);
		scrollPane.setBounds(10, 70, 412, 90);
		contentPane.add(scrollPane);
		
		final JTextArea textAreaUserList = new JTextArea();
		scrollPane.setViewportView(textAreaUserList);
		textAreaUserList.setEditable(false);
		
		JLabel lblNewLabel_3 = new JLabel("List");
		lblNewLabel_3.setBounds(331, 199, 54, 15);
		contentPane.add(lblNewLabel_3);
		
		
		final JButton btnOK = new JButton("OK");
		btnOK.setEnabled(false);
		btnOK.setBounds(331, 263, 93, 57);
		contentPane.add(btnOK);
		
		final JComboBox<String> comboBoxReceiver = new JComboBox<String>();
		comboBoxReceiver.setBounds(331, 225, 93, 21);
		contentPane.add(comboBoxReceiver);
		
		
		//只绑定一次端口，防止重复绑定
				try {
					receiveSocket = new DatagramSocket(Utils.serverPort);
				} catch (SocketException e2) {
					e2.printStackTrace();
				}
				
				btnStartService.addActionListener(new ActionListener() {
					//启动服务事件监听器
					public void actionPerformed(ActionEvent e) {
						new Thread(new Runnable() {
							@Override
							public void run() {						
						btnOK.setEnabled(true);
						//开始执行服务器初始化过程
						userList = new Vector<Client>();
						//设置页面上的提示
						String tmp = textAreaCommand.getText();
						if(!tmp.equals(""))//防止重启服务器时出现的不协调显示
							tmp += "\n";
									
						
						setTitle("Server : Started");
						btnStartService.setEnabled(false);
						btnEndService.setEnabled(true);
						
						//将停止标志设为false
						stopFlag = false;
						
						while(true){
							try {					
								//绑定服务器端口，然后接受来自客户端的信息
								Message msg = Utils.receiveMessage(receiveSocket);
								Client tc = msg.getClient();
								
								//点击终止按钮后，服务器收到数据包后直接跳出
								if(stopFlag)
									break;
								
								//根据不同的命令执行不同的过程
								switch(msg.getCmd()){
								//转发用户的消息
								case CMD_SENDMESSAGE:
									//获取接收人和聊天内容
									Command command = (Command) msg.getCarrier();
									String receiverName = command.getReceiver();
									String text = command.getText();
									//找到接收人的ip地址和端口,由于只比较用户名，所以不用设置ip地址和端口号
									Client requestClient = userList.elementAt(userList.indexOf(new Client(msg.getClient().getClientName(),null,0)));
									Client receiveClient = userList.elementAt(userList.indexOf(new Client(receiverName,null,0)));
									Shop receiveShop = new Shop("");
									int index1;
									index1 = shopList.indexOf(new Shop(receiveClient.getClientIp().getHostAddress().toString()));
									System.out.println(index1);
									if (( index1 = shopList.indexOf(new Shop(receiveClient.getClientIp().getHostAddress().toString()))) >= 0) {
										System.out.println(index1);
									receiveShop = shopList.elementAt(index1);
									System.out.println(receiveShop);
									}
									//向客户端发送它请求的信息
									if (text.equals("/enter shop")) {
										receiveShop.addCustomers(requestClient);
										Message msg1 = new Message(requestClient, CMD.CMD_ENTERSHOP, null);
										Utils.sendMessage(msg1, receiveClient.getClientIp(), receiveClient.getClientPort());
										Message msg2 = new Message(receiveClient.getShop().getGoods(), CMD.CMD_SENDGOODS, receiveClient);
										Utils.sendMessage(msg2, requestClient.getClientIp(), requestClient.getClientPort());
										requestClient.setEnterShopFlag(true);
									}else if (text.matches("/addgoods.*")) {
										    int index2 = shopList.indexOf(new Shop(requestClient.getClientIp().getHostAddress().toString()));
										    if (index2 >= 0) {
										    Shop requestShop = shopList.elementAt(index2);
											String[] tmp1 = text.split(" ");
											String goodsID = tmp1[1];
											String goodsName = tmp1[2];
											double goodsPrice = Double.valueOf(tmp1[3]);
											requestShop.addGoods(new Goods(goodsID, goodsName, goodsPrice));
											System.out.println(requestShop.getCustomers());
											for (Client c : requestShop.getCustomers()) {
												Message msg8 = new Message(requestShop.getGoods(), CMD.CMD_SENDGOODS, null);
											    Utils.sendMessage(msg8, c.getClientIp(), c.getClientPort());
									        }
											Message msg9 = new Message(requestShop.getGoods(), CMD.CMD_SENDGOODS, null);
										    Utils.sendMessage(msg9, requestClient.getClientIp(), requestClient.getClientPort());
										    }
									}else if (text.equals("/leave")) {
										receiveShop.removeCustomer(requestClient);
										requestClient.setEnterShopFlag(false);
										Message msg10 = new Message(requestClient, CMD.CMD_LEAVESHOP, null);
										Utils.sendMessage(msg10, receiveClient.getClientIp(), receiveClient.getClientPort());
									}else if (text.matches("/buy.*")) {
										//向商店拥有者发送购买信息 
										String[] tmp2 = text.split(" ");
										String goodsID = tmp2[1];
										try {
											Goods good = receiveShop.getGoods().elementAt(receiveShop.getGoods().indexOf(new Goods(goodsID)));
											receiveShop.addGoodsSold(good);
											receiveShop.getGoods().remove(receiveShop.getGoods().indexOf(new Goods(goodsID)));
											Message msg3 = new Message(receiveShop.getGoods(), CMD.CMD_SENDGOODS, receiveClient);
											Utils.sendMessage(msg3, receiveClient.getClientIp(), receiveClient.getClientPort());
											Utils.sendMessage(msg3, requestClient.getClientIp(), requestClient.getClientPort());
										}catch (Exception e1) {
											JOptionPane.showMessageDialog(textAreaUserList,e1.getMessage());
											e1.printStackTrace();
											}
									}else if (text.equals("/customers")) {
												if (requestClient.getEnterShopFlag() == true) {
													Message msg4 = new Message(receiveShop.getCustomers(), CMD.CMD_SENDCUSTOMERS, null);
													Utils.sendMessage(msg4, requestClient.getClientIp(), requestClient.getClientPort());
											}else{
												int index = shopList.indexOf(new Shop(requestClient.getClientIp().getHostAddress().toString()));
												if (index == -1) {
													Message msg8 = new Message(null, CMD.CMD_NOTENTERWARNING, null);
													Utils.sendMessage(msg8, requestClient.getClientIp(), requestClient.getClientPort());
												}else {
													Shop requestShop = shopList.elementAt(index);
													Message msg4 = new Message(requestShop.getCustomers(), CMD.CMD_SENDCUSTOMERS, null);
													Utils.sendMessage(msg4, requestClient.getClientIp(), requestClient.getClientPort());
												}
											}	
									}else if (text.equals("/goods")) {
										if (requestClient.getEnterShopFlag() == true) {
											Message msg6 = new Message(receiveShop.getGoods(), CMD.CMD_SENDGOODS, null);
											Utils.sendMessage(msg6, requestClient.getClientIp(), requestClient.getClientPort());
									}else{
										int index = shopList.indexOf(new Shop(requestClient.getClientIp().getHostAddress().toString()));
										if (index == -1) {
											Message msg8 = new Message(null, CMD.CMD_NOTENTERWARNING, null);
											Utils.sendMessage(msg8, requestClient.getClientIp(), requestClient.getClientPort());
										}else {
										Shop requestShop = shopList.elementAt(shopList.indexOf(new Shop(requestClient.getClientIp().getHostAddress().toString())));
										Message msg7 = new Message(requestShop.getGoods(), CMD.CMD_SENDGOODS, null);
										Utils.sendMessage(msg7, requestClient.getClientIp(), requestClient.getClientPort());
									   }	
									}
								 }else if (text.equals("/statistics")){
									 int index3 =shopList.indexOf(new Shop(requestClient.getClientIp().getHostAddress().toString()));
									 if (index3 >= 0) {
									 Shop requestShop= shopList.elementAt(index3);
									 Message msg9 = new Message(requestShop.getGoodsSold(), CMD.CMD_SENDGOODSSOLD, null);
									 Utils.sendMessage(msg9, requestClient.getClientIp(), requestClient.getClientPort());
									 }
								 }
								break;

								case CMD_USERLOGIN:
									//如果当前用户名已经存在
									Message tm = null;
									if(userList.contains(tc)){								
										 tm = new Message(null,CMD.CMD_USERALREADYEXISTS, tc);
										 Utils.sendMessage(tm,tc.getClientIp(),tc.getClientPort());	
									}
									else{
										userList.add(tc);
										//如果登录成功，则将最新的在线用户列表发送给所有客户端（类似于好友上线提示）
										tm = new Message(null,CMD.CMD_LOGINSUCCESS, tc);
										Utils.sendMessage(tm, tc.getClientIp(), tc.getClientPort());	
										updateListUserList(textAreaUserList, userList);
										comboBoxReceiver.removeAllItems();
										for(Client c : userList){	
											comboBoxReceiver.addItem(c.getClientName());
										}
										
									}
									break;
									
								//用户退出的响应
								case CMD_USERQUIT:
			
									//从用户列表中删除该用户
									userList.remove(tc);
									updateListUserList(textAreaUserList, userList);
									//通知其他用户，该用户下线了
									for(Client c : userList){	
										tm = new Message(userList,CMD.CMD_USERQUIT,tc);
										Utils.sendMessage(tm,c.getClientIp(),c.getClientPort());
									}
									break;
									
								case CMD_SENDSHOP:
									textAreaUserList.setText("ShopName\t\tOwner\n");
									String s = ((Shop)msg.getCarrier()).getShopName() + "\t" + ((Shop)msg.getCarrier()).getOwner().getClientName();
									textAreaUserList.setText(textAreaUserList.getText() + s);
									break;
		
								case CMD_SENDGOODS:
									String name = msg.getClient().getClientName();
									tc = userList.elementAt(userList.indexOf(new Client(name,null,0)));
									tc.getShop().setGoods((Vector <Goods>)msg.getCarrier());	
									System.out.println(tc.getShop().getGoods());
									updateListGoodsList(textAreaUserList,tc.getShop().getGoods());
									break;
									
								case CMD_SENDCUSTOMERS:
									textAreaUserList.setText("CustomerName\tCustomerID\n");
									Vector<Client> customers = (Vector<Client>)msg.getCarrier();
									String s1 = "";
									for(Client c : customers){
										s1 += c.getClientName()+"\t\t"+c.getClientIp().getHostAddress();		
									}
									textAreaUserList.setText(textAreaUserList.getText() + s1);
									break;
									
									//向商店拥有者发送有人进来的消息，向请求用户发送商品信息
								case CMD_ENTERSHOP:
									Command cmd = (Command)msg.getCarrier();
									Client requestClient1 = msg.getClient();
									String receiveClientName = cmd.getReceiver();
									Client receiveClient1 = userList.elementAt(userList.indexOf(new Client(receiveClientName,null,0)));
									shopList.elementAt(shopList.indexOf(new Client(receiveClientName,null,0).getShop())).addCustomers(requestClient1);
									Message msg1 = new Message(requestClient1, CMD.CMD_ENTERSHOP, null);
									Utils.sendMessage(msg1, receiveClient1.getClientIp(), receiveClient1.getClientPort());
									Message msg2 = new Message(receiveClient1.getShop().getGoods(), CMD.CMD_SENDGOODS, receiveClient1);
									Utils.sendMessage(msg2, requestClient1.getClientIp(), requestClient1.getClientPort());
								
								case CMD_LEAVESHOP:
									String receiveClientName2 = ((Command)msg.getCarrier()).getReceiver();
									Client requestClient2 = msg.getClient();
									Client receiveClient2 = userList.elementAt(userList.indexOf(new Client(receiveClientName2,null,0)));
									shopList.elementAt(shopList.indexOf(new Client(receiveClientName2,null,0).getShop())).removeCustomer(requestClient2);
									Message msg4 = new Message(requestClient2, CMD.CMD_LEAVESHOP, null);
									Utils.sendMessage(msg4, receiveClient2.getClientIp(), receiveClient2.getClientPort());
									break;
									

								case CMD_SENDSHOPLIST:
									Message msg0 = new Message(shopList, CMD.CMD_SENDSHOPLIST, tc);
									Utils.sendMessage(msg0, tc.getClientIp(), tc.getClientPort());
									break;
								
								}
								
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(textAreaCommand,e1.getMessage());
								e1.printStackTrace();
							}
							
						
						}}}).start();		
							
					}
		});
		
		//由于停止按钮中不会发生阻塞，所以不用使用多线程
		btnEndService.addActionListener(new ActionListener() {
			//启动服务事件监听器
			public void actionPerformed(ActionEvent e) {
				//清空用户列表及相关区域
				textAreaCommand.setText(textAreaCommand.getText()+"\n"+Utils.getCurrentFormatTime()+"the server stop service");
				textAreaUserList.setText("");
				setTitle("Server : Stoped");
				
				//设置停止标记
				stopFlag = true;
				
				//向在线的所有用户发送服务器停止服务的消息
				if(!userList.isEmpty()){
					for(Client c : userList){
						Message tm = new Message(null, CMD.CMD_SERVERSTOP, null);
						try {
							Utils.sendMessage(tm,c.getClientIp(),c.getClientPort());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				btnStartService.setEnabled(true);
				btnEndService.setEnabled(false);		
			}
		});
		
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//获取管理员用户输入的消息
				String command = textAreaCommand.getText();
				//管理员未输入任何消息
				String receiver = (String) comboBoxReceiver.getSelectedItem();
				Client tc = new Client();
				
				//服务器发送“我要发命令”的消息
				if(command.equals("")){
					JOptionPane.showMessageDialog(textAreaCommand,"文本框为空，不能发送");
					return;
				}else if(command.equals("/opennewshop")) {
					try {
					tc = userList.elementAt(userList.indexOf(new Client(receiver, null, 0)));
					tc.openNewShop(tc.getClientIp().getHostAddress().toString());
					tc.getShop().setOwner(tc);
					shopList.add(tc.getShop());
					 Message msg = new Message(new Command(receiver, command), CMD.CMD_SENDCOMMAND, null);
					 Utils.sendMessage(msg, tc.getClientIp(), tc.getClientPort() );
					}catch(Exception e1) {
						JOptionPane.showMessageDialog(textAreaCommand,e1.getMessage());
						e1.printStackTrace();
					}
			
				}else if (command.matches("/close.*")) {
					shopName = command.split(" ")[1];
					command = command.split(" ")[0];
					shop1 = shopList.elementAt(shopList.indexOf(new Shop(shopName)));
					receiveName = shop1.getOwner().getClientName();
					shopList.remove(shop1);
					userList.elementAt(userList.indexOf(new Client(receiveName, null, 0))).setShopFlag("");
					try {
					Message msg = new Message(new Command(receiveName, command), CMD.CMD_SENDCOMMAND, null);
					tc = userList.elementAt(userList.indexOf(new Client(receiveName, null, 0)));
					Utils.sendMessage(msg, tc.getClientIp(), tc.getClientPort() );
					}catch (Exception e1) {
						JOptionPane.showMessageDialog(textAreaCommand,e1.getMessage());
						e1.printStackTrace();
						}
				}else if (command.matches("/enter.*")) {
					shopName = command.split(" ")[1];
					command = command.split(" ")[0];
					shop1 = shopList.elementAt(shopList.indexOf(new Shop(shopName)));
					receiveName = shop1.getOwner().getClientName();
					try {
					Message msg = new Message(new Command(receiveName, command), CMD.CMD_SENDCOMMAND, null);
					tc = userList.elementAt(userList.indexOf(new Client(receiveName, null, 0)));
					Utils.sendMessage(msg, tc.getClientIp(), tc.getClientPort() );
					}catch (Exception e1) {
						JOptionPane.showMessageDialog(textAreaCommand,e1.getMessage());
						e1.printStackTrace();
						}
				}else if (command.equals("/goods") | command.equals("/customers")){
					tc = userList.elementAt(userList.indexOf(new Client(receiveName, null, 0)));
					Message msg = new Message(new Command(receiveName, command), CMD.CMD_SENDCOMMAND, tc);
					try {
					Utils.sendMessage(msg, tc.getClientIp(), tc.getClientPort());
					}catch (Exception e1) {
						JOptionPane.showMessageDialog(textAreaCommand,e1.getMessage());
						e1.printStackTrace();
					}
				}else if (command.equals("/shops")){
					updateListShopList(textAreaUserList, shopList);
				}else if (command.equals("/users")) {
					updateListUserList(textAreaUserList, userList);
				}else if (command.equals("/leave")) {
					textAreaUserList.setText("");
				}
					else {
				
				try {
					//将消息格式内容，命令，用户的地址信息综合成msg
					 Message msg = new Message(new Command(receiver, command), CMD.CMD_SENDCOMMAND, null);
					 tc = userList.elementAt(userList.indexOf(new Client(receiver, null, 0)));
					 Utils.sendMessage(msg, tc.getClientIp(), tc.getClientPort() );
				}catch (Exception e1) {
					JOptionPane.showMessageDialog(textAreaCommand,e1.getMessage());
					e1.printStackTrace();
					}
				}
				
			}
		});
	}
	
	//更新界面上的用户列表区域
	private static void updateListUserList(JTextArea jta,Vector<Client> v){
		if(userList == null)
			return;
		String s = "UserName\t\tID\t\tShop\n";
		for(Client c : v){
			s += c.getClientName()+"\t\t"+c.getClientIp().getHostAddress()+"\t\t"+c.getShopFlag()+"\n";		
		}
		jta.setText(s);
		
	}
	
	private static void updateListShopList(JTextArea jta,Vector<Shop> v){
		if(userList == null)
			return;
		String s = "Shop\t\tUserName\t\tUserID\n";
		for(Shop sh : v){
			s += sh.getShopName() + "\t\t" + sh.getOwner().getClientName() + "\t\t" + sh.getOwner().getClientIp().getHostAddress() + "\n";		
		}
		jta.setText(s);	
	}
	
	private static void updateListGoodsList(JTextArea jta,Vector<Goods> v){
		String s = "GoodsID\t\tGoodsName\t\tGoodsPrice\n";
		for(Goods g : v){
				s += g.getGoodID() + "\t\t"+g.getGoodName() + "\t\t" + g.getPrice() + "\n";	
		}
		jta.setText(s);	
	}
	
}


