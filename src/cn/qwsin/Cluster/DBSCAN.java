package cn.qwsin.Cluster;

import cn.qwsin.common.*;

import java.io.PrintStream;
import java.util.*;

public class DBSCAN {
    private double e;//半径
    private int minp;//密度阈值，含自己
    private int dim;//维数
    private ArrayList<Dpoint> data;

    class Dpoint{
        double[] v;
        Set<double[]> neighbor;
        Dpoint(int len){
            v = new double[len];
            neighbor = new HashSet<>();
        }
        boolean isKey(){return neighbor.size()>=minp;}
    }

    public DBSCAN(double e,int minp,int dim){
        this.e=e;
        this.minp=minp;
        this.dim=dim;
        this.data=new ArrayList<>();
    }

    public DBSCAN(double e,int minp,int dim,ArrayList<double[]> dt){
        this.e=e;
        this.minp=minp;
        this.dim=dim;
        this.data=new ArrayList<>();
        for (double[] doubles : dt) {//转化为Dpoint表示
            Dpoint dpoint = new Dpoint(dim);
            if (dim >= 0) System.arraycopy(doubles, 0, dpoint.v, 0, dim);
            data.add(dpoint);
        }
    }

    public void loadFile(String filePath){
        ArrayList<double[]> tmp = ReadFile.loadData(filePath,100);
        dim = tmp.size()>0?tmp.get(0).length:0;
        for (double[] doubles : tmp) {//转化为Dpoint表示
            Dpoint dpoint = new Dpoint(dim);
            if (dim >= 0) System.arraycopy(doubles, 0, dpoint.v, 0, dim);
            data.add(dpoint);
        }
        System.out.println("载入数据成功");
    }

    private int findSet(int x,int[] p){
        return p[x]==x?x:(p[x]=findSet(p[x],p));
    }

    //并查集合并
    private void Merge(int x, int y,int[] p){
        x=findSet(x,p);
        y=findSet(y,p);
        if(x == y) return ;
        data.get(x).neighbor.addAll(data.get(y).neighbor);
        data.get(y).neighbor.clear();
        p[y]=x;
    }

    public void clusterStock(ArrayList<String> names){
        //输出到文件
        try {
            PrintStream ps = new PrintStream("D:/log.txt");
            System.setOut(ps);
        }catch (Exception e){
            e.printStackTrace();
        }
        for(int i=0;i<data.size();++i){//枚举每个点
            Dpoint dp1 = data.get(i);
            dp1.neighbor.add(dp1.v);//自己放进自己的neighbor
            for(int j=0;j<data.size();++j) if(i!=j){//枚举其他的点，检查是否在范围内
                Dpoint dp2 = data.get(j);
                if(MyMath.coefOfAssociation(dp1.v,dp2.v) >= e){//符合相关性要求
                    dp1.neighbor.add(dp2.v);//加入集合
                }
            }
        }

        int[] p = new int[data.size()];
        for(int i=0;i<data.size();++i) p[i]=i;//初始化并查集

        for(int i=0;i<data.size();++i) if(data.get(i).isKey()){
            for(int j=0;j<data.size();++j)
                if(data.get(j).isKey() && MyMath.coefOfAssociation(data.get(i).v,data.get(j).v)>=e){
                    Merge(i,j,p);//合并互相在e范围内的关键点
                }
        }

        int cnt=0;
        Set<String> vis = new HashSet<>();
        for(int i=0;i<data.size();++i) if(p[i]==i){
            ArrayList<String> tmp = new ArrayList<>();
            for(double[] t : data.get(i).neighbor){
                int id=-1;
                for(int j=0;j<data.size();++j) if(Arrays.equals(data.get(j).v, t)){
                    id=j;break;
                }
                String name = names.get(id);
                if(vis.contains(name)) continue;
                vis.add(name);
                tmp.add(name);
            }
            if(tmp.size()==0) continue;
//            System.out.printf("第%d个集合:",++cnt);
            tmp.sort(String::compareTo);
            System.out.println(tmp);
        }
    }

    public void cluster(){
        for(int i=0;i<data.size();++i){//枚举每个点
            Dpoint dp1 = data.get(i);
            dp1.neighbor.add(dp1.v);//自己放进自己的neighbor
            for(int j=0;j<data.size();++j) if(i!=j){//枚举其他的点，检查是否在范围内
                Dpoint dp2 = data.get(j);
                if(MyMath.getDistance(dp1.v,dp2.v) <= e){
                    dp1.neighbor.add(dp2.v);//加入集合
                }
            }
        }

        int[] p = new int[data.size()];
        for(int i=0;i<data.size();++i) p[i]=i;//初始化并查集

        for(int i=0;i<data.size();++i) if(data.get(i).isKey()){
            for(int j=0;j<data.size();++j)
                if(data.get(j).isKey() && MyMath.getDistance(data.get(i).v,data.get(j).v)<= e){
                    Merge(i,j,p);//合并互相在e范围内的关键点
                }
        }

        //可视化部分
        int count=0;//先数出最终有多少个集合
        for(int i=0;i<data.size();++i) if(p[i]==i) ++count;

        Set<double[]> vis = new HashSet<>();
        double[][][] datas = new double[count][][];
        int pos=0;
        for(int i=0;i<data.size();++i) if(p[i]==i){
            int sz=data.get(i).neighbor.size();
            double[][] tmp = new double[sz][dim];
            int j=0;
            for(double[] point : data.get(i).neighbor){
                if(vis.contains(point)) continue;
                tmp[j++]=point;
                vis.add(point);
            }
            datas[pos]=tmp;
            ++pos;
        }
        PicUtility.show(datas,count);
    }
}
