package cn.qwsin.SortVertex;

import cn.qwsin.Graph.Graph;
import javafx.util.Pair;

import java.util.*;

//按照度排序，包括三种：直接按度排序，H函数排序，Kshell排序
public class Degree<T> {

    //单纯按照度数排序
    public Pair<Map<T,Integer>,ArrayList<T>> sortByDegree(Graph<T> graph){
        Map<T,Integer> D = graph.getDegreeAll();
        ArrayList<T> v = new ArrayList<>(graph.getVertex());
        v.sort((o1, o2) -> -D.get(o1).compareTo(D.get(o2)));
        return new Pair<>(D,v);
    }

    //Kshell值排序
    public Pair<Map<T,Integer>,ArrayList<T>> sortByKshell(Graph<T> graph){
        Map<T,Integer> D = graph.getDegreeAll();

        int maxD=0;//先统计出最大度数
        for(Map.Entry<T,Integer> entry : D.entrySet()){
            maxD = Math.max(maxD,entry.getValue());
        }

        Queue<T> q = new LinkedList<>();//保存因为度数减少而变为待处理的点
        Set<T> vis = new HashSet<>();//标记点是否处理过

        Map<T,Integer> Kshell = new HashMap<>();
        for(int i=0;i<=maxD;++i)
        {//从小到大处理度数
            for(T u : graph.getVertex()) if(D.get(u)<=i && !vis.contains(u)){//枚举当前删除的点
                q.offer(u);
                vis.add(u);
                Kshell.put(u,i);
                D.put(u,D.get(u)-1);
                while(!q.isEmpty())
                {//还有待处理点的时候一直处理
                    T v = q.poll();
                    for(T t : graph.to(v)) {
                        D.put(t, D.get(t) - 1);//减少一度
                        if (D.get(t) <= i && !vis.contains(t)) {//减少之后已经小于等于当前处理的度数，需要处理
                            q.offer(t);
                            vis.add(t);//标记为处理过，防止下次从list中重复处理
                            Kshell.put(t, i);
                        }
                    }
                }
            }
        }
        //使用Kshell数组排序
        ArrayList<T> res = new ArrayList<>(graph.getVertex());
        res.sort((o1, o2) -> -Kshell.get(o1).compareTo(Kshell.get(o2)));

        return new Pair<>(Kshell,res);
    }

    //H-index排序
    public Pair<Map<T,Integer>,ArrayList<T>> sortByHindex(Graph<T> graph){
        Map<T,Integer> D = graph.getDegreeAll();//获取度数
        Map<T,Integer> H = new HashMap<>();
        for(T u : graph.getVertex()){//枚举每个点计算
            int l=1,r=graph.vertexSize(),mid,ans=0;//二分H值
            while(l<=r){
                mid = (l+r)/2;
                int count=0;
                for(T v : graph.to(u)){//计算mid是否符合要求
                    if(D.get(v) >= mid) ++count;
                }
                if(count >= mid) {ans=mid;l=mid+1;}
                else r=mid-1;
            }
            H.put(u,ans);
        }

        //使用H排序
        ArrayList<T> res = new ArrayList<>(graph.getVertex());
        res.sort((o1, o2) -> -H.get(o1).compareTo(H.get(o2)));
        return new Pair<>(H,res);
    }
}
