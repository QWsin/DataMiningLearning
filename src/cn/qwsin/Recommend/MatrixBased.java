package cn.qwsin.Recommend;

import java.util.ArrayList;

import static cn.qwsin.common.MyMath.*;

public class MatrixBased {
    private ArrayList<ArrayList<Double>> rating;//分数矩阵
    private int u;//用户数量
    private int m;//电影数量
    private int n;//特征数量,最开始给出
    private double[][] D;//分数矩阵
    private double[][] U;//用户-特征矩阵
    private double[][] M;//电影-特征矩阵
    private double[][] predict;//结果矩阵
    private double alpha;//学习率
    private double lambda;//正则化参数

    public MatrixBased(int n,double alpha,double lambda){
        this.n=n;
        this.alpha=alpha;
        this.lambda=lambda;
        rating = new ArrayList<>();
    }

    public void loadData(String filePath){
        rating = LoadData.load(filePath);
        if(rating!=null) {
            u = rating.size();
            m = u>0?rating.get(0).size():0;
            D = new double[u][m];
            for(int i=0;i<u;++i)
                for(int j=0;j<m;++j)
                    D[i][j]=rating.get(i).get(j);
        }
        U = getRandMatrix(u,n);
        M = getRandMatrix(m,n);
    }

    //训练
    public void train(int trainTime){
        double[][] mp = new double[m][n];
        double[][] up = new double[u][n];
        double[][] R;
        double pre=Double.MAX_VALUE;
        for(int o=0;o<trainTime;++o){
//            if(o>100) alpha*=;
            R = minus(D,mul(U,rot(M)));//R=D-U*rot(M)

            //对U矩阵更新
            U= minus(U, mul(2*alpha, minus(mul(lambda,U), mul(R,M))));//U=U-2*alpha*(-RM+lambda*U);
            //M矩阵更新
            M= minus(M, mul(2*alpha, minus(mul(lambda,M), mul(rot(R),U))));//M=M-2*alpha*(-rot(R)*U+lambda*M);

//            for(int j=0;j<m;++j)
//                for(int k=0;k<n;++k){
//                    mp[j][k]=0.0;
//                    for(int i=0;i<u;++i) if(D[i][j]!=-1){
//                        mp[j][k] += (getPredict(i,j)-D[i][j])*U[i][k];
//                    }
//                    mp[j][k] += lambda * M[j][k];
//                }
//
//            for(int i=0;i<u;++i)
//                for(int k=0;k<n;++k){
//                    up[i][k]=0.0;
//                    for(int j=0;j<m;++j) if(D[i][j]!=-1){
//                        up[i][k] += (getPredict(i,j)-D[i][j])*M[j][k];
//                    }
//                    up[i][k] += lambda * U[i][k];
//                }
//
//            for(int j=0;j<m;++j)
//                for(int k=0;k<n;++k)
//                    M[j][k] -= alpha * mp[j][k];
//
//            for(int i=0;i<u;++i)
//                for(int k=0;k<n;++k)
//                    U[i][k] -= alpha * up[i][k];

            double cur=calcL(R);
//            if(cur>pre) break;
//            pre=cur;
            System.out.printf("第%d次:%.6f\n",o,cur);
        }
        predict = mul(U,rot(M));
        show();
    }

    private double getPredict(int i,int j){
        double ans=0;
        for(int k=0;k<n;++k)
            ans += U[i][k]*M[j][k];
        return ans;
    }


    private double sum(double[][] A){
        int n=A.length;
        int m=n>0?A[0].length:0;
        double res=0;
        for (double[] doubles : A)
            for (int j = 0; j < m; ++j)
                res += sqr(doubles[j]);
        return res;
    }


    private double calcL(double[][] R){
        return sum(R)+lambda*(sum(U)+sum(M));
    }

    private void show(){
        System.out.println("--------用户-特征矩阵---------");
        for(int i=0;i<u;++i) {
            for (int j = 0; j < n; ++j){
                System.out.printf("%.1f ",U[i][j]);
            }
            System.out.println();
        }
        System.out.println("--------电影-特征矩阵---------");
        for(int i=0;i<m;++i) {
            for (int j = 0; j < n; ++j){
                System.out.printf("%.1f ",M[i][j]);
            }
            System.out.println();
        }
    }

    public double query(int i,int j){
        if(i<0||i>u||j<0||j>m) {
            System.out.println("查询出错");
            return Double.NaN;
        }
        return predict[i][j];
    }
}
