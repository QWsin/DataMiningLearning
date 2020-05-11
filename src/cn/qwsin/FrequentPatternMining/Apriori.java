package cn.qwsin.FrequentPatternMining;

import java.util.*;

import static java.lang.Math.min;

public class Apriori {
    private int N;//事务总数
    private float minSup;//支持度
    private float minConf;//置信度
    private Map<Set<String>,Integer> data;//事务数据集，integer记录包含频繁模式个数，方便剪枝

    private Map<Integer,Set<Set<String>>> FP;//存储频繁模式结果
    private Map<Set<String>,Set<Set<String>>> associatedRules;//存储关联规则
    private Map<Set<String>,Float> sup;//存储支持度
    private Map<String,Integer> id;//给每个项编号，便于计算哈希值

    int[] bucket;//桶

    //打印频繁模式
    private void printFP(int _pos){
        Integer pos=_pos;
        System.out.println("----------------printFP-----------------");
        System.out.println(_pos+"项集");
        Set<Set<String>> set = FP.get(pos);
        for (Set<String> strings : set) {
            System.out.println(strings);
        }
    }

    //打印所有频繁模式
    private void printAllFP(){
        for(int i=1;i<=N;++i)
            if(FP.containsKey(i)) {
                printFP(i);
            }else return ;
    }


    public Apriori(Map<Set<String>,Integer> data,float minSup,float minConf){
        this.N = data.size();
        this.data = new HashMap<>(data);
        this.minSup = minSup;
        this.minConf = minConf;
        this.FP = new HashMap<>();
        this.associatedRules = new HashMap<>();
        this.sup = new HashMap<>();
        this.id = new HashMap<>();
        this.bucket = new int[min(this.N*this.N+1,2000003)];
    }

    //找出一项的频繁模式
    private Set<Set<String>> FP_one(){
        HashMap<Set<String>,Integer> count = new HashMap<>();//统计每种项的出现次数

        for (Map.Entry<Set<String>,Integer> entry: data.entrySet()) {
            for (String s : entry.getKey()) {
                int v = 0;
                Set<String> tmp = new HashSet<>();
                tmp.add(s);
                if (count.containsKey(tmp)) {//之前出现过，在之前的基础上+1
                    v = count.get(tmp);
                }
                else{//之前没有出现过，就编号
                    int cnt=id.size();
                    id.put(s,cnt+1);
                }
                count.put(tmp, v + 1);
            }
        }
        Set<Set<String>> fp_one = new HashSet<>();//保存频繁模式
        for(Map.Entry<Set<String>,Integer> entry:count.entrySet()){
            sup.put(new HashSet<>(entry.getKey()),(float)entry.getValue()/this.N);
            if(entry.getValue() >= this.minSup*this.N){
                fp_one.add(new HashSet<>(entry.getKey()));
            }
        }
        return fp_one;
    }

    //检测连接出的项，是否每个大小为k的子集都在k项频繁模式里，是返回true，否则返回false
    private boolean check(Set<String> items,Set<Set<String>> curFP){
        if(curFP.size() < items.size()-1){
            //剪枝：k项频繁模式一共不足k个，一定不满足条件
            return false;
        }
        Set<String> k_items=new HashSet<>(items);
        for(String item: items){
            k_items.remove(item);//依次删除一个，就可以遍历所有大小为k的子集
            if(!curFP.contains(k_items)){
                return false;
            }
            k_items.add(item);//加回去
        }
        return true;
    }

    /*
    TODO:添加桶，提前过滤某些不可能是频繁模式的模式
     */
    //在事务数据库中计算items项的出现次数
    private float getSupport(Set<String> items){
        if(sup.containsKey(items)) return sup.get(items);
        float res=0;
        for(Map.Entry<Set<String>,Integer> entry: data.entrySet()){
            //由于短路运算符，只有当事务包含items时才会++res
            if(entry.getKey().containsAll(items)){
                res += 1;
            }
        }
        Set<String> newItems = new HashSet<>(items);
        sup.put(newItems,res/this.N);
        return res/this.N;
    }

    //获取集合哈希值，用于桶
    private int getHash(Set<String> items){
        long ret=0;
        for(String s : items){
            ret = (ret * (id.size()+1) + id.get(s)) % bucket.length;
        }
        return (int)ret;
    }

    //搜索每个事务中的所有k项集，将其加入桶并计数
    /*
    private void dfsKItems(Iterator<String> it, Set<String> kItems,int k){
        if(kItems.size() == k){//已经够k个了，就不再往下了
            bucket[getHash(kItems)] += 1;
            return ;
        }

        //TODO:拷贝一个it的备份使得可以进行两次分支运算

        if(!it.hasNext()) return ;
        Iterator<String> newit = new HashSet<String>().iterator(it);
        String cur = it.next();
        dfsKItems(it,kItems,k);//不将当前加入
    }
    */

    //将bucket处理为装有所有k项的桶(dfsKItems未完成)
    /*
    private void fillBucket(int k){
        for(int i=0;i<bucket.length;++i) bucket[i]=0;
        Set<String> kItems = new HashSet<>();
        //对每项事务都进行搜索
        for(Map.Entry<Set<String>, Integer> entry : data.entrySet()){
            Iterator<String> it  = entry.getKey().iterator();
            dfsKItems(it,kItems,k);
        }
    }
    */

    //由k-1项频繁模式连接出k项，并检测是否满足支持度要求
    private Set<Set<String>> connectCheck(Set<Set<String>> curFP, int k){
//        if(k<6) fillBucket(k);（该函数未完成）
        Set<Set<String>> res = new HashSet<>();
        for (Set<String> set1 : curFP) {
            for (Set<String> set2 : curFP) {
                if (set1.equals(set2)) continue;
                Set<String> items = new HashSet<>(set1);
                items.retainAll(set2);//求两集合交
                if (items.size() < k-2) continue;

                items.addAll(set1);//求两集合并
                items.addAll(set2);
                if (check(items, curFP) && getSupport(items)>=minSup)
                    res.add(items);
            }
        }
        return res;
    }

    //简化数据集，没有k项频繁模式的数据集一定没有k+1项频繁模式
    private void simplifyData(Set<Set<String>> fp){
        for(Set<String> set: fp){
            for(Map.Entry<Set<String>,Integer> entry : data.entrySet()){
                if(entry.getKey().containsAll(set)) {//出现频繁模式
                    int cnt=entry.getValue();
                    data.put(entry.getKey(),cnt+1);
                }
            }
        }
        for(Iterator it = data.entrySet().iterator();it.hasNext();){
            Map.Entry<Set<String>,Integer> entry= (Map.Entry<Set<String>, Integer>) it.next();
            if(entry.getValue().equals(0)) {//这个事务没有出现频繁模式，那么在以后也不会出现，将其删除
                it.remove();
            }
            else {
                data.put(entry.getKey(),0);//归零，保证下次计数正确性
            }
        }
    }

    //寻找频繁模式
    public void findAllFrequentItem(){
        Set<Set<String>> fp_cur=this.FP_one();
        FP.put(1,fp_cur);

        for(int k=2;k<=N;++k)
        {
            System.out.println("当前事务集大小:"+data.size());
            fp_cur= connectCheck(fp_cur,k);
            if(fp_cur.isEmpty()) break;
            simplifyData(fp_cur);//删除没有k项频繁模式的事务集
            FP.put(k,fp_cur);
        }

        printAllFP();
    }

    //寻找频繁模式之后，找出强关联规则
    public void findAssociationRules(){
        for(int k=1;k<=N;++k){
            if(!FP.containsKey(k)) break;

            Set<Set<String>> fps=FP.get(k);//大小为i的所有频繁模式
            for(Set<String> items : fps){//取出一个频繁模式
                String[] items_s = new String[items.size()];
                items.toArray(items_s);//转换为数组，方便子集枚举

                for(int S=(1<<items.size())-2;S>=1;--S){//使用2进制进行子集枚举
                    Set<String> A = new HashSet<>();
                    for(int i=0;i<items.size();++i) {
                        if ((S & (1 << i)) != 0) A.add(items_s[i]);
                    }
                    float conf=(float) sup.get(items) / sup.get(A);
                    if(conf > minConf){
                        items.removeAll(A);//求一下差集
//                        System.out.println("Put in"+A+" "+items);
                        if(associatedRules.containsKey(A)){
                            Set<String> newItems = new HashSet<>(items);
                            associatedRules.get(A).add(newItems);
                        }
                        else{
                            Set<Set<String>> tmp = new HashSet<>();
                            Set<String> set = new HashSet<>(items);
                            tmp.add(set);
                            associatedRules.put(A,tmp);
                        }
                        items.addAll(A);
                    }
                }
            }
        }
        System.out.println("关联规则");
        System.out.println(associatedRules);
    }
}
