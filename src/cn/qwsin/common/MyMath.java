package cn.qwsin.common;

public class MyMath {
    public static double sqr(double x){return x*x;}
    public static double getDistanceSqr(double[] p1, double[] p2,int dim){
        double dis=0;
        for(int i=0;i<dim;++i){
            dis += MyMath.sqr(p1[i]-p2[i]);
        }
        return dis;
    }
    public static double getDistance(double[] p1,double[] p2,int dim){
        return Math.sqrt(getDistanceSqr(p1,p2,dim));
    }
}
