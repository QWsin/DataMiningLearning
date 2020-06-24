package cn.qwsin.common;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.util.Map;

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

    //使用修正过的公式计算相关性，要求传入的数组长度相等
    public static double CaculateCorrelation(int[] x, int[] y){
        if(x.length!=y.length) {
            System.out.println("传入数组长度不等");
            return Double.NaN;
        }
        //计算存在相等关系的pair数
        int n1=0,n2=0;
        Map<Integer,Integer> count = Count.mapCount(x);
        for(Map.Entry<Integer,Integer> entry : count.entrySet()){
            n1+=(entry.getValue()-1)*entry.getValue()/2;
        }
        count = Count.mapCount(y);
        for(Map.Entry<Integer,Integer> entry : count.entrySet()){
            n2+=(entry.getValue()-1)*entry.getValue()/2;
        }
        //计算逆序对和正序对数
        int equal=0,nx=0,n_=0;
        for(int i=0;i<x.length;++i){
            for(int j=i+1;j<y.length;++j){
                if((x[i]<x[j] && y[i]<y[j]) || (x[i]>x[j] && y[i]>y[j])) ++nx;
                else if(x[i]==x[j] || y[i]==y[j]) ++equal;
                else ++n_;
            }
        }
        return (nx-n_)/Math.sqrt((x.length-n1)*(y.length-n2));
    }
    public static double log2(double x){
        return Math.log(x)/Math.log(2);
    }


}
