package cn.qwsin.Normalize;

import java.util.ArrayList;

public class ListArray {
    //dataSet: 数据集
    //dim: 维数
    //len: 归一化区间长度 len=1时，转化到[0,1]
    public static ArrayList<double[]> Normalize(ArrayList<double[]> dataSet,int dim,double len){
        ArrayList<double[]> data = new ArrayList<>(dataSet);
        if(data.size() == 0) return new ArrayList<>();
        double[] M = new double[dim];
        double[] m = new double[dim];
        for(int i=0;i<dim;++i){
            m[i]=M[i]=data.get(0)[i];
        }

        for(int i=1;i<data.size();++i){
            for(int j=0;j<dim;++j){
                m[j]=Math.min(m[j],data.get(i)[j]);
                M[j]=Math.max(M[j],data.get(i)[j]);
            }
        }

        for(int i=0;i<data.size();++i){
            double[] tmp = new double[dim];
            for(int j=0;j<dim;++j){
                tmp[j]=(data.get(i)[j]-m[j])/(M[j]-m[j])*len;
            }
            data.set(i,tmp);
        }

        return data;
    }
}
