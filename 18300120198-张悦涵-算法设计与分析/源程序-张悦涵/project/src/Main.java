
import javax.swing.JFrame;
public class Main extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3584424279056750459L;
	MyPanel mp = null;
	public Main(Edge edge[][], T t[][], int n, double weight) {
		mp = new MyPanel(edge, t, n,weight);
		this.add(mp);
		this.setSize(1024, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);  //ͼ�ν������
	}		
	public static void main(String[] args) {		
		Vertex[] vertex10 = new Vertex[10];		
		for (int i = 0; i < 10; i++) {	
			vertex10[i] = new Vertex();
		}
		vertex10[0].x = 0;vertex10[0].y = 0;vertex10[1].x = 5;vertex10[1].y = 0;vertex10[2].x = 8;vertex10[2].y = 1;vertex10[3].x = 9;vertex10[3].y = 2;vertex10[4].x = 9;vertex10[4].y = 4;vertex10[5].x = 8;vertex10[5].y = 6;vertex10[6].x = 5;vertex10[6].y = 7;vertex10[7].x = 3;vertex10[7].y = 7;vertex10[8].x = 1;vertex10[8].y = 6;vertex10[9].x = 0;vertex10[9].y = 2;
		ConvexPloygon(10,vertex10);   //��������
	}
	private static void ConvexPloygon(int n,Vertex[] v){  //�����Ҫ����
		int d, i, j, k;
		int m = 0;
		double addweight = 0;
		Edge[][] edge = new Edge[n][n];	 //��¼����ı�
		T[][] t = new T[n][n];		//��¼����ֵ�Ķ�ά����
		for (d = 0; d < n; d++) {
			for (i = 0; i < d; i++) {
				edge[i][d] = new Edge();
				edge[i][d].v1 = v[i];
				edge[i][d].v2 = v[d];
				edge[i][d].weight = Math.sqrt(Math.pow(v[i].x- v[d].x, 2) + Math.pow(v[i].y - v[d].y, 2));
			}
			
		}
		
		double temp = 10000, sum = 0;
		for (d = 1; d < n; d++) {      //�����Խ���
			j = d;
			for (i = 0; i < n - d; i++) {
				m = d - 1;
				t[i][j] = new T(m, 0);
				if (d == 1) {          //��һ���Խ���
					addweight = 0;    
				} else {
					temp = 1000;
					for (k = i + 1; k < j; k++) {
						sum = t[i][k].sumweight + t[k][j].sumweight + edge[i][k].weight + edge[k][j].weight;  //�ֽ�Ϊ���������������ֵ
						
						if (sum < temp) {                                //�������kֵ�µķֽ��ԭ��¼������ֵС��������Ϊ�µ�����ֵ������¼��ָʽ
							temp = sum;
							
							if (m >= 2) {
								int l, s = 0;
								for (l = 1; l < k - i; l++) {
									t[i][j].edge[s] = t[i][k].edge[l - 1];
									s++;
								}
								for (l = 1; l < j - k; l++) {
									t[i][j].edge[s] = t[k][j].edge[l - 1];
									s++;                                       //edge��¼������ֵ�������ʷֵķ�ʽ
								}
							}
						}
					}
					addweight = edge[i][j].weight + temp;
				}
				t[i][j].sumweight = addweight;

				if (m > 0) {
					t[i][j].edge[m - 1].v1 = v[i];
					t[i][j].edge[m - 1].v2 = v[j];    //m>0ʱ����d>1ʱ������vi,vj����Ҳ����Ҫ��¼�ķָ
				}
				j++;
			}
		}
		
		new Main(edge, t, n, t[0][n - 1].sumweight);  //������������ʷ�
	}
}
