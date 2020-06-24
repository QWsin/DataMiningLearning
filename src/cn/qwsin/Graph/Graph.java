package cn.qwsin.Graph;

import weka.core.stemmers.Stemmer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Graph<T> {
    private Map<T, ArrayList<T>> G;

    public Graph(){
        G = new HashMap<>();
    }

    public Graph(String filePath){
        //TODO:从文件直接加载图
    }

    public void addEdge(T u,T v){
        if(!G.containsKey(u)) G.put(u,new ArrayList<>());;
        if(!G.containsKey(v)) G.put(v,new ArrayList<>());;
        G.get(u).add(v);
        G.get(v).add(u);
    }

    public void addVertex(T u){
        if(!G.containsKey(u)) G.put(u,new ArrayList<>());;
    }

    public ArrayList<T> to(T u){
        return G.get(u);
    }

    public Set<T> getVertex(){
        return G.keySet();
    }

    public int vertexSize(){return G.size();}

    //统计每个点的度数
    public Map<T,Integer> getDegree(){
        Map<T,Integer> D = new HashMap<>();
        for(T v : getVertex()){
            D.put(v,to(v).size());//度数就是出边的数量
        }
        return D;
    }
}
