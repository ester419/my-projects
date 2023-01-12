

public class T {      //记录最优剖分
	int n;            
	Edge[] edge;             //记录最优求解策略，即剖分的各根弦
	double sumweight;        //剖分后总权值
	public T(int n,double sumweight){
		this.n=n;
		this.sumweight=sumweight;   
		edge=new Edge[n];
		for(int i=0;i<n;i++){
				edge[i]=new Edge();
			}
		}
	}

