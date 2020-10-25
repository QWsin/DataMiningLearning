package cn.qwsin.Graph;

import cn.qwsin.common.*;

import java.io.BufferedReader;
import java.util.*;

public class Graph<T> {
    private Map<T, Set<T>> G;

    public Graph(){
        G = new HashMap<>();
    }

    public Graph(String filePath){
        G = new HashMap<>();
        try{

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addEdge(T u,T v){
        if(!G.containsKey(u)) G.put(u,new HashSet<>());;
        if(!G.containsKey(v)) G.put(v,new HashSet<>());;
        G.get(u).add(v);
        G.get(v).add(u);
    }

    public void addVertex(T u){
        if(!G.containsKey(u)) G.put(u,new HashSet<>());;
    }

    public Set<T> to(T u){
        return G.get(u);
    }

    public Set<T> getVertex(){
        return G.keySet();
    }

    public int vertexSize(){return G.size();}

    //统计每个点的度数
    public Map<T,Integer> getDegreeAll(){
        Map<T,Integer> D = new HashMap<>();
        for(T v : getVertex()){
            D.put(v,to(v).size());//度数就是出边的数量
        }
        return D;
    }

    public int getDegree(T u){return G.get(u).size();}
}
