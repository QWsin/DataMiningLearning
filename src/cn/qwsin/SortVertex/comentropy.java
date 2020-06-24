package cn.qwsin.SortVertex;

import cn.qwsin.Graph.Graph;
import cn.qwsin.common.MyMath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class comentropy<T> {
    public ArrayList<T> sortByComent(Graph<T> graph){
        Map<T,Integer> D = graph.getDegree();
        Map<T,Double> p = new HashMap<>();
        for(T u : graph.getVertex()){
            double sum=0;
            for(T v : graph.to(u)){
                sum += D.get(v);
            }
            p.put(u,D.get(u)/sum);
        }

        Map<T,Double> h = new HashMap<>();
        for(T u : graph.getVertex()){
            double hi=0;
            for(T v : graph.to(u)){
                hi -= p.get(v)*MyMath.log2(p.get(v));
            }
            h.put(u,hi);
        }

        ArrayList<T> res = new ArrayList<>(graph.getVertex());
        res.sort((o1, o2) -> -h.get(o1).compareTo(h.get(o2)));
        return res;
    }
}
