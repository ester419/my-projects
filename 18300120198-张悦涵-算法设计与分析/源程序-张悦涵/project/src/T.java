

public class T {      //��¼�����ʷ�
	int n;            
	Edge[] edge;             //��¼���������ԣ����ʷֵĸ�����
	double sumweight;        //�ʷֺ���Ȩֵ
	public T(int n,double sumweight){
		this.n=n;
		this.sumweight=sumweight;   
		edge=new Edge[n];
		for(int i=0;i<n;i++){
				edge[i]=new Edge();
			}
		}
	}

