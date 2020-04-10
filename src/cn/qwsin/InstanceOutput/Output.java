package cn.qwsin.InstanceOutput;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class Output {
    private int step=0;
    //将特征打印出来
    public void printAttribute(Instances instances) {
        int numOfAttributes = instances.numAttributes();
        for (int i = 0; i < numOfAttributes; ++i) {
            Attribute attribute = instances.attribute(i);
            System.out.print(attribute.name() + "     ");
        }
        System.out.println();
        //打印实例
        int numOfInstance = instances.numInstances();
        for (int i = 0; i < numOfInstance; ++i) {
            Instance instance = instances.instance(i);
            String[] ss=instance.toString().split(",");
            for(int j=0;j<ss.length;++j) {
                //这里使用%-12s是为了让特征对齐，以便观察
                System.out.print(String.format("%-12s",ss[j]));
                if(j!=ss.length-1) System.out.print(",");
                else System.out.println("");
            }
        }
    }
    //打印步骤，为了方便随时增加删除步骤，使用了计数器，避免每次步骤改动需要改动大量序号
    public void printStep(String s){
        System.out.println("step."+(++step)+" "+s);
    }
}
