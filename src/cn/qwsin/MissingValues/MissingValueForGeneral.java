package cn.qwsin.MissingValues;

import cn.qwsin.Classify.DecisionTree;
import cn.qwsin.common.Find;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MissingValueForGeneral {
    public static void fillMissing(Set<DecisionTree.Record> records, ArrayList<String> attributes, DecisionTree tree){
        for (String name : attributes) {
            if (tree.isContinuous(name)) {
                //处理连续值
                double sum = 0;
                int count = 0;
                for (DecisionTree.Record record : records) {
                    if (!(record.attrCont.get(name) == Double.MAX_VALUE)) {
                        sum += record.attrCont.get(name);
                        count++;
                    }
                }
                sum /= count;//计算平均数，用平均数填充
                for (DecisionTree.Record record : records) {
                    if (record.attrCont.get(name) == Double.MAX_VALUE) {
                        record.attrCont.put(name, sum);
                    }
                }
            } else {
                //处理离散值
                Map<String,Integer> count = new HashMap<>();
                for(DecisionTree.Record record : records){
                    if(!record.attrDisc.get(name).equals("?")){
                        String value=record.attrDisc.get(name);
                        if(count.containsKey(value)){
                            int k=count.get(value);
                            count.put(value,k+1);
                        }else{
                            count.put(value,1);
                        }
                    }
                }

                String maxName = Find.findMost(count);//获得“众数”
                for(DecisionTree.Record record : records){
                    if(record.attrDisc.get(name).equals("?")){
                        record.attrDisc.put(name,maxName);
                    }
                }
            }
        }
    }
}
