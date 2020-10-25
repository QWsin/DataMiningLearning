package cn.qwsin.SortVertex;

import cn.qwsin.Graph.Graph;
import javafx.util.Pair;

import java.util.*;

public class Probability<T> {
    //b是感染概率
    public Pair<Map<T,Double>,ArrayList<T>> sortByProb(Graph<T> graph, double b){
        Queue<T> q = new LinkedList<>();
        Map<T,Double> sum = new HashMap<>();
        for(T s : graph.getVertex()){//枚举每一个点
            Map<T,Integer> deep = new HashMap<>();//标记层数，方便BFS
            Map<T,Double> unif_s = new HashMap<>();
            for(T v : graph.getVertex()){
                unif_s.put(v,1d);//初始化
                deep.put(v,0);
            }
            unif_s.put(s,0d);//初始点不被感染的概率为0，即一定被感染

            q.offer(s);deep.put(s,1);//起点第一层
            while(!q.isEmpty()){
                T u = q.poll();
                if(1-unif_s.get(u) < 1e-6){//剪枝，已经没有什么大影响了
                    continue;
                }
                //枚举下一层的点，标记之后放入队列
                for(T v : graph.to(u)) if(deep.get(v)==0 || deep.get(v)==deep.get(u)+1){
                    if(deep.get(v)==0){
                        q.offer(v);
                        deep.put(v,deep.get(u)+1);
                    }
                    //计算下一层的unif_s值
                    unif_s.put(v,unif_s.get(v)*(1-(1-unif_s.get(u))*b));
                }
            }

            //统计概率和
            double ans = 0;
            for(T u : graph.getVertex()){
                ans += 1-unif_s.get(u);
            }
            sum.put(s,ans);
        }
        //按照概率排序
        ArrayList<T> res = new ArrayList<>(graph.getVertex());
        res.sort((o1, o2) -> -sum.get(o1).compareTo(sum.get(o2)));
        return new Pair<>(sum,res);
    }

}
