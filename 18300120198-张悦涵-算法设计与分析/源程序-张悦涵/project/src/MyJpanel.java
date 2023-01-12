

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

class MyPanel extends JPanel { 
	/**
	 * 
	 */
	private static final long serialVersionUID = -3498830365099706375L;
	Edge[][] edge;
	T[][] t;
	int n;
	double weight;
	public MyPanel(Edge[][] edge, T[][] t,int n,double weight) {
		this.edge=edge;
		this.t=t;
		this.n=n;
		this.weight=weight;
	}
	public void paint(Graphics g) {  //绘制凸边形并进行绘制最优剖分结果
		super.paint(g); 
		g.setColor(Color.blue); 
		g.drawLine(edge[0][n-1].v1.x*20+20,edge[0][n-1].v1.y*20+20,edge[0][n-1].v2.x*20+20,edge[0][n-1].v2.y*20+20); 
		for(int i=0;i<n-1;i++){
			g.drawLine(edge[i][i+1].v1.x*20+20,edge[i][i+1].v1.y*20+20,edge[i][i+1].v2.x*20+20,edge[i][i+1].v2.y*20+20); 
		}	
		 g.setColor(Color.red); 
		 for(int i=0;i<n-3;i++){
			 g.drawLine(t[0][n-1].edge[i].v1.x*20+20,t[0][n-1].edge[i].v1.y*20+20,t[0][n-1].edge[i].v2.x*20+20,t[0][n-1].edge[i].v2.y*20+20);
			}
		 g.setColor(Color.gray);
		 g.drawString("此凸多边形最小弦长三角剖分如上图所示，权函数之和是"+weight,35,200);
	}
 }

