package cn.qwsin.Recommend;

import cn.qwsin.Graph.Graph;
import cn.qwsin.common.MyMath;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BiGraph {
    private Graph<Integer> graph;//保存二部图
    private ArrayList<ArrayList<Double>> rating;//评分矩阵
    private int userNum;//用户数量
    private int movieNum;//电影数量

    public BiGraph(){
        graph = new Graph<>();
    }

    public void loadData(String filePath){
        rating = LoadData.load(filePath);
        if(rating!=null) {
            userNum = rating.size();
            movieNum = rating.get(0).size();
            //建立二部图，user编号0 ~ userNum-1,movie编号userNum ~ userNum+movieNum-1
            for(int i=0;i<userNum;++i)
                for(int j=0;j<movieNum;++j) if(rating.get(i).get(j)!=-1.0){
                    graph.addEdge(i,userNum+j);
                }
        }
    }

    //对某个用户进行扩散推荐，heat表示是否是热传导方式
    public void diffusion(int userID, Double[] val,boolean heat){
        Arrays.fill(val,0.0);
        for(int u=userNum;u<userNum+movieNum;++u) val[u]=0.0;//先全部当作没买过
        for(int v : graph.to(userID)) {
            val[v] = rating.get(userID).get(v - userNum)*4;//设置初值
//            System.out.println(v+" "+val[v]);
        }
        for(int clk=0;clk<1;++clk) {
            for (int u = userNum; u < userNum + movieNum; ++u)
                if (Math.abs(val[u]) > 1e-9) {//枚举商品结点
                    for (int v : graph.to(u)) {
                        if(heat) val[v] += val[u] / graph.getDegree(v);
                        else val[v] += val[u] / graph.getDegree(u);
                    }
                    val[u] = 0.0;//归零
                }
            for (int u = 0; u < userNum; ++u)
                if (Math.abs(val[u]) > 1e-9) {
                    for (int v : graph.to(u)) {
                        if(heat) val[v] += val[u] / graph.getDegree(v);
                        else val[v] += val[u] / graph.getDegree(u);
                    }
                    val[u] = 0.0;
                }
        }
    }

    //对所有用户进行扩散算法，heat表示是不是热传导，因为两个算法差别非常非常小。
    public void diffusionAll(boolean heat){
        Double[] val = new Double[userNum+movieNum];//记录权值

        double sumrho=0;
        for(int i=0;i<userNum;++i){
            diffusion(i,val,heat);
            double[] x = new double[movieNum];
            for(int j=0;j<movieNum;++j){
                x[j] = val[j+userNum];
            }
            double rho=0,xMean=MyMath.mean(x),yMean=MyMath.mean(rating.get(i));

            for(int j=0;j<x.length;++j){
                rho+=(x[j]-xMean)*(rating.get(i).get(j)-yMean);
            }
            double fm = 0;
            for(int j=0;j<x.length;++j){
                fm += (x[j]-xMean)*(x[j]-xMean);
                fm += MyMath.sqr(rating.get(i).get(j)-yMean);
            }
            rho /= fm;
            sumrho += rho;
            System.out.printf("第%d个用户的相关系数:%.6f\n",i,rho);
        }
        System.out.println("总相关系数:"+sumrho/userNum);
    }
}
