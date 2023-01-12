package common;

//协议设计
public enum CMD {
	CMD_USERLOGIN,			//用户登录
	CMD_UPDATEUSERLIST,		//更新用户列表
	CMD_SENDMESSAGE,		//发送消息
	CMD_USERALREADYEXISTS,	//当前用户已经存在
	CMD_SERVERSTOP,			//服务器停止
	CMD_USERQUIT,           //客户端退出
	CMD_SENDCOMMAND,        //发送命令
	CMD_SENDSHOP,           //返回商店
	CMD_SENDGOODS,          //返回商品
	CMD_SENDCUSTOMERS,      //返回顾客
	CMD_ENTERSHOP,          //进入商店
	CMD_SENDSHOPLIST,       //返回商店列表
	CMD_LEAVESHOP,          //离开商店
	CMD_LOGINSUCCESS,       //登录成功
	CMD_CLOSE,              //关闭商店
	CMD_NOTENTERWARNING,    //没有进入商店警告
	CMD_SENDGOODSSOLD       //返回已售商品信息
	};
