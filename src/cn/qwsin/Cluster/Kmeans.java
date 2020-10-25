package cn.qwsin.Cluster;

import cn.qwsin.common.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;

public class Kmeans {
    private ArrayList<double[]> data;
    private int K;//类别数量
    private int dim;//数据维数

    public Kmeans(int K) {
        this.K = K;
        this.data = new ArrayList<>();
    }

    public Kmeans(int K,int dim,ArrayList<double[]> data){
        this.K = K;
        this.data = data;
        this.dim = dim;
    }

    public void load(String filePath) {
        data = ReadFile.loadData(filePath,100);
        dim = data.size()>0?data.get(0).length:0;
        System.out.println("载入数据成功");
    }

    private int findNearest(double[][] center, double[] point){
        double m = Double.MAX_VALUE;
        int id = -1;
        for (int j = 0; j < K; ++j) {
            double dis = MyMath.getDistanceSqr(center[j], point);
            if (dis < m) {
                m = dis;
                id = j;
            }
        }
        return id;
    }

    public void cluster() {
        double[][] center = new double[K][dim];
        Random random = new Random();
        for (int i = 0; i < K; ++i) {
            for (int j = 0; j < dim; ++j)
                center[i][j] = random.nextDouble() * 100;
        }

        boolean done = false;
        int clk=0;
        while (!done) {
            System.out.printf("第%d次迭代\n",++clk);
            ArrayList<ArrayList<double[]>> clusters  = new ArrayList<>();//可视化部分
            for(int i=0;i<center.length;++i) clusters.add(new ArrayList<>());

            double[][] nextCenter = new double[K][dim];//记录和，计算下次的中心点
            int[] count = new int[K];//记录属于每个中心点的点数量，方便求平均
            for (double[] point : data) {
                int id=findNearest(center,point);
                for (int j = 0; j < dim; ++j)
                    nextCenter[id][j] += point[j];
                ++count[id];
                clusters.get(id).add(point);
            }

            for (int i = 0; i < K; ++i) {
                for (int j = 0; j < dim; ++j)
                    //count为0的话，直接除会出错
                    if(count[i]!=0)
                        nextCenter[i][j] /= count[i];
                    else
                        nextCenter[i][j] = random.nextDouble()*100;
            }

            double MDelta=-1;//记录最小距离的平方
            for(int i=0;i<K;++i){
                double dis=MyMath.getDistanceSqr(nextCenter[i],center[i]);
                MDelta=Math.max(MDelta,dis);
                System.out.printf("%d 号点移动%.2f距离\n",i,dis);
                center[i]=nextCenter[i];
            }

            //终止条件
            if(Math.sqrt(MDelta)<1e-9){done = true;}

            double[][][] datas = new double[K][][];
            for(int i=0;i<K;++i){
                double[][] cluster = new double[clusters.get(i).size()][];
                for(int j=0;j<cluster.length;++j){
                    cluster[j]=clusters.get(i).get(j);
                }
                datas[i]=cluster;
            }

            System.out.println("cluster mean:");
            for (double[] point : center) {
                for (double x : point) {
                    System.out.print(x + " ");
                }
                System.out.println();
            }
            PicUtility.show(datas,K);
            try{
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private int findNearestStock(double[][] center, double[] point){
        double m = -1000000;
        int id = -1;
        for (int j = 0; j < K; ++j) {
            double dis = MyMath.coefOfAssociation(center[j], point);
            if (dis > m) {
                m = dis;
                id = j;
            }
        }
        return id;
    }

    public void clusterStock(ArrayList<String> names) {
        double[][] center = new double[K][dim];
        Random random = new Random();
        for (int i = 0; i < K; ++i) {
            double[] tmp = new double[dim];
            for (int j = 0; j < dim; ++j)
                tmp[j] = random.nextDouble();
            center[i]=tmp;
        }

        boolean done = false;
        int clk=0;
        while (!done && clk<200) {
            System.out.printf("第%d次迭代\n",++clk);
            ArrayList<ArrayList<double[]>> clusters  = new ArrayList<>();//可视化部分
            for(int i=0;i<center.length;++i) clusters.add(new ArrayList<>());

            double[][] nextCenter = new double[K][dim];//记录和，计算下次的中心点
            int[] count = new int[K];//记录属于每个中心点的点数量，方便求平均
            for (double[] point : data) {
                int id=findNearestStock(center,point);
                for (int j = 0; j < dim; ++j)
                    nextCenter[id][j] += point[j];
                ++count[id];
                clusters.get(id).add(point);
            }

            for (int i = 0; i < K; ++i) {
                for (int j = 0; j < dim; ++j)
                    //count为0的话，直接除会出错
                    if(count[i]!=0)
                        nextCenter[i][j] /= count[i];
                    else
                        nextCenter[i][j] = random.nextDouble();
            }

            double MDelta=-1;//记录最小距离的平方
            for(int i=0;i<K;++i){
                double dis=MyMath.getDistance(nextCenter[i],center[i]);
                MDelta=Math.max(MDelta,dis);
                System.out.printf("%d 号点移动%.2f距离\n",i,dis);
                center[i]=nextCenter[i];
            }

            //终止条件
            if(Math.sqrt(MDelta)<1e-9){done = true;}
        }

        //输出到文件
        try {
            PrintStream ps = new PrintStream("D:/log.txt");
            System.setOut(ps);
        }catch (Exception e){
            e.printStackTrace();
        }
        for(int i=0;i<K;++i){
//            System.out.printf("第%d个板块:",i);
//            System.out.printf("[");
            for(int j=0;j<data.size();++j){
                if(findNearestStock(center,data.get(j))==i){
                    System.out.printf(",%s",names.get(j));
                }
            }
            System.out.printf("]");
            System.out.println();
        }
    }
}
