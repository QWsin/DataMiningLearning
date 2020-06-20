package cn.qwsin.MissingValues;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import cn.qwsin.InstanceOutput.Output;

public class Weka {
    public static void fillMissing(Instances instances){
        int dim = instances.numAttributes();/*特征数量*/
        int num = instances.numInstances();/*数据组数*/

        for (int i = 0; i < dim; ++i) {
            /*判断是否标称型，如果是，采取众数填充法，否则使用平均数填充*/
            if (instances.attribute(i).isNominal()) {
                int kinds = instances.attribute(i).numValues();/*标称值种类数*/
                int[] cnt = new int[kinds];
                for (int j = 0; j < num; ++j)
                    if (!instances.instance(j).isMissing(i))
                        ++cnt[(int) instances.instance(j).value(i)];/*标称型数据返回整数，这里直接类型强转*/
                int pos = 0;
                for (int j = 1; j < kinds; ++j) if (cnt[j] > cnt[pos]) pos = j;
                for (int j = 0; j < num; ++j)
                    if (instances.instance(j).isMissing(i)) instances.instance(j).setValue(i, pos);
            } else {
                //如果是数值型，就取平均值
                double mean = 0;//表示均值，先存放和
                int count = 0;//表示有多少未缺失数据
                for (int j = 0; j < num; ++j)
                    if (!instances.instance(j).isMissing(i)) {
                        mean += instances.instance(j).value(i);
                        ++count;
                    }
                mean /= count;//计算均值
                for (int j = 0; j < num; ++j)
                    if (instances.instance(j).isMissing(i)) instances.instance(j).setValue(i, mean);
            }
        }
    }
    public static void main(String[] args) throws Exception {
        String filePath = "C:\\Users\\QWsin\\Documents\\weka-data\\test.arff";
        Output output = new Output();
        output.printStep("读取数据...");

        DataSource source = new DataSource(filePath);
        Instances instances = source.getDataSet();

        //预先打印出来看一下，以便发现前后区别
        output.printAttribute(instances);
        output.printStep("补充缺失值...");
        fillMissing(instances);

        output.printStep("打印到屏幕");
        output.printAttribute(instances);
        output.printStep("输出到文件...");
//        ConverterUtils.DataSink.write(filePath.substring(0,filePath.length()-5)+"Filled.arff", instances);
        output.printStep("Finished.");
    }
}
