package cn.qwsin.common;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class MyMath {
    public static double sqr(double x){return x*x;}
    //欧几里得距离
    public static double getDistanceSqr(double[] p1, double[] p2){
        double dis=0;
        for(int i=0;i<p1.length;++i){
            dis += MyMath.sqr(p1[i]-p2[i]);
        }
        return dis;
    }
    public static double getDistance(double[] p1, double[] p2){
        return Math.sqrt(getDistanceSqr(p1,p2));
    }

    //使用修正过的公式计算相关性，要求传入的数组长度相等
    public static double CaculateCorrelation(double[] x, double[] y){
        if(x.length!=y.length) {
            System.out.println("传入数组长度不等");
            return Double.NaN;
        }
        //计算存在相等关系的pair数
        int n1=0,n2=0;
        Map<Double,Integer> count = Count.mapCount(x);
        for(Map.Entry<Double,Integer> entry : count.entrySet()){
            n1+=(entry.getValue()-1)*entry.getValue()/2;
        }
        count = Count.mapCount(y);
        for(Map.Entry<Double,Integer> entry : count.entrySet()){
            n2+=(entry.getValue()-1)*entry.getValue()/2;
        }
        //计算逆序对和正序对数
        int equal=0,nx=0,n_=0;
        for(int i=0;i<x.length;++i){
            for(int j=i+1;j<y.length;++j){
                if((x[i]<x[j] && y[i]<y[j]) || (x[i]>x[j] && y[i]>y[j])) ++nx;
                else if((x[i]<x[j] && y[i]>y[j]) || (x[i]>x[j] && y[i]<y[j])) ++n_;
            }
        }
        return (nx-n_)/Math.sqrt((double)(x.length*(x.length-1)/2-n1)*((double)(y.length-1)*y.length/2-n2));
//        return 2*(nx-n_)/(double)x.length/(x.length-1);
    }
    public static double log2(double x){
        return Math.log(x)/Math.log(2);
    }

    //平均数
    public static double mean(double[] a){
        double mean=0;
        for(double x : a) mean+=x;
        return mean / a.length;
    }
    public static double mean(ArrayList<Double> a){
        double res=0;
        for(double x : a) res += x;
        return res/a.size();
    }


    //相关系数计算
    public static double coefOfAssociation(double[] x,double[] y){
        if(x.length!=y.length) return Double.NaN;
        double rho=0,xMean=mean(x),yMean=mean(y);
        for(int i=0;i<x.length;++i){
            rho+=(x[i]-xMean)*(y[i]-yMean);
        }
        return rho/(x.length-1)/stdDeviation(x)/stdDeviation(y);
    }

    //计算方差
    public static double variance(double[] a){
        double sigma2=0,mean=mean(a);
        for(double x : a) sigma2+=sqr(mean-x);
        sigma2/=a.length-1;//注意除以n-1不是n
        return sigma2;
    }

    //计算标准差
    public static double stdDeviation(double[] a){
        return Math.sqrt(variance(a));
    }

    //矩阵转置
    public static double[][] rot(double[][] M){
        int n=M.length;
        int m=n>0?M[0].length:0;
        double[][] res = new double[m][n];
        if(n==0 || m==0) return res;
        for(int i=0;i<n;++i)
            for(int j=0;j<m;++j)
                res[j][i]=M[i][j];
        return res;
    }

    //矩阵相乘
    public static double[][] mul(double[][] A, double[][] B){
        int an=A.length;int am=an>0?A[0].length:0;
        int bn=B.length;int bm=bn>0?B[0].length:0;
        double[][] res = new double[an][bm];
        if(am!=bn) {
            System.out.println("矩阵相乘出错");
            return new double[1][1];
        }
        for(int i=0;i<an;++i)
            for(int j=0;j<bm;++j) {
                res[i][j]=0.0;
                for (int k = 0; k < am; ++k) {
                    res[i][j] += A[i][k]*B[k][j];
                }
            }
        return res;
    }

    //数乘以矩阵
    public static double[][] mul(double x, double[][] A){
        int n=A.length;
        int m=A.length>0?A[0].length:0;
        double[][] res = new double[n][m];
        for(int i=0;i<n;++i) {
            for (int j = 0; j < m; ++j)
                res[i][j] = x * A[i][j];
        }
        return res;
    }

    //矩阵加法
    public static double[][] add(double[][] A, double[][] B){
        int an=A.length;int am=an>0?A[0].length:0;
        int bn=B.length;int bm=bn>0?B[0].length:0;
        if(an!=bn || am!=bm) {
            System.out.println("加法维数不同");
            return new double[1][1];
        }

        double[][] res = new double[an][am];
        for(int i=0;i<an;++i)
            for(int j=0;j<am;++j){
                res[i][j]=A[i][j]+B[i][j];
            }
        return res;
    }

    //矩阵减法
    public static double[][] minus(double[][] A, double[][] B){
        int an=A.length;int am=an>0?A[0].length:0;
        int bn=B.length;int bm=bn>0?B[0].length:0;
        if(an!=bn || am!=bm) {
            System.out.println("减法维数不同");
            return new double[1][1];
        }

        double[][] res = new double[an][am];
        for(int i=0;i<an;++i)
            for(int j=0;j<am;++j){
                res[i][j]=A[i][j]-B[i][j];
            }
        return res;
    }

    //随机一个矩阵
    public static double[][] getRandMatrix(int n,int m){
        double[][] res = new double[n][m];
        Random random = new Random();
        for(int i=0;i<n;++i)
            for(int j=0;j<m;++j)
                res[i][j]=random.nextDouble();
        return res;
    }
}
