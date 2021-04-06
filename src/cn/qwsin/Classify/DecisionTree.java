package cn.qwsin.Classify;

import cn.qwsin.common.Find;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

import static cn.qwsin.MissingValues.SetRecord.fillMissing;

public class DecisionTree {

    private Set<Record> records;//保存记录
    private ArrayList<String> attributes;//保存属性
    private Set<String> attributesSet;//set形式的属性，有的地方会用到
    private Map<String,Integer> idOfLabel;//记录某种属性对应的编号
    private Map<String,String> categoryOfAttr;//标记某种属性连续or离散
    private Map<String,ArrayList<Double>> dividePoint;//标记连续属性的分界点
    private Map<String,Set<String>> ValueOfAttrs;//属性的值
    private String[] Labels;//记录属性名
    private int N;//表示连续属性统一划分为多少段
    private Random random;

    private TreeNode root;//保存树根
    private ArrayList<TreeNode> forest;//保存随机森林树根

    public DecisionTree(){
        records = new HashSet<>();
        idOfLabel = new HashMap<>();
        categoryOfAttr = new HashMap<>();
        dividePoint = new HashMap<>();
        attributesSet = new HashSet<>();
        forest = new ArrayList<>();
        random = new Random();
        ValueOfAttrs = new HashMap<>();
        N = 11;
    }

    //Record类表示数据集中一条记录
    public class Record{
        int ID;//数据编号
        public String label;//类别
        public String answer;//对于测试集，记录答案，以便分析效果
        public Map<String,String> attrDisc;//离散的属性值
        public Map<String,Double> attrCont;//连续的属性值
        Record(){
            attrDisc = new HashMap<>();
            attrCont = new HashMap<>();
        }
    }

    //AttributeField类表示某种属性
    public class AttributeField{
        String name;
        String attrValueDisc;//存放离散属性值
        String attrValueCont;//存放连续属性值
        AttributeField(){
            attrValueDisc = null;
            attrValueCont = null;
        }
    }

    //treeNode类表示Decision tree上的一个结点
    public class TreeNode {
        Map<AttributeField, TreeNode> children;//存储儿子节点
        String attrName;//该节点往下分割时使用的属性名称
        Set<Record> records;//该节点包含的记录数
        String label;//叶子节点的属性标识
        TreeNode(){
            children = new HashMap<>();
        }
        boolean isLeaf(){return children==null || children.size()==0;}
    }

    //flag 0:train 1:test
    private Record getRecord(String s,int flag){
        Record record = new Record();
        String[] value=s.trim().split("[,\t ]");
        for(int i=0;i<value.length-1;++i){
            if(isContinuous(attributes.get(i))){
                //可能出现value为"?"的情况，这种情况使用Double.MAX_VALUE表示
                if(value[i].trim().equals("?")){
                    record.attrCont.put(attributes.get(i), Double.MAX_VALUE);
                }else {
                    record.attrCont.put(attributes.get(i), new Double(value[i].trim()));
                }
            }
            else{
                record.attrDisc.put(attributes.get(i),value[i].trim());//放入特征值
            }
        }
        if(flag==0)record.label=value[value.length-1].trim();
        else record.answer=value[value.length-1].trim();//记录标准答案
        return record;
    }

    //统计各个类别在记录中出现的次数
    private Map<String, Integer> countLabel(Set<Record> records){
        Map<String, Integer> count = new HashMap<>();
        for(Record record: records){
            if(count.containsKey(record.label)){//该类别出现过，计数在原有基础上+1
                int t=count.get(record.label);
                count.put(record.label,t+1);
            }else{//该类别未出现过，记为出现一次
                count.put(record.label,1);
            }
        }
        return count;
    }

    //信息熵计算（根据类别计算，与属性的值无关）
    private double entropy(Set<Record> records){
        Map<String, Integer> count=countLabel(records);
        //对于每种类别，累加值
        double sum=0;
        for(Map.Entry<String,Integer> entry: count.entrySet()){
            double pk=(double)entry.getValue()/records.size();//计算出现概率
            sum+=pk*(Math.log(pk)/ Math.log(2));
        }
        return sum*(-1);
    }

    //根据某种属性值将记录划分（由于多处会用到所以构建函数）
    private Map<String,Set<Record>> divByAttr(Set<Record> records, String attributeName){
        Map<String,Set<Record>> divSets = new HashMap<>();//一个属性值对应一个记录集合
        for(Record record: records){
            String value=null;
            //离散值直接处理，连续值划分成离散值处理
            if(!isContinuous(attributeName)) {
                value = record.attrDisc.get(attributeName);//该记录对应属性的值
            }
            else {
                value = getContValue(attributeName,record.attrCont.get(attributeName));
            }
            if(divSets.containsKey(value)){//之前出现过，在原来的基础上添加一个再放入
                Set<Record> t=divSets.get(value);
                t.add(record);
                divSets.put(value,t);
            }else{//之前没有出现，新建一个放入
                Set<Record> t = new HashSet<>();
                t.add(record);
                divSets.put(value,t);
            }
        }
        return divSets;
    }

    public Map<String,Set<Record>> divByAttr_CART(Set<Record> records, String attr, String value){
        Map<String,Set<Record>> divSets=new HashMap<>();
        divSets.put(value,new HashSet<>());//加入两个集合（特意使用特殊符号避免与原本的值重复）
        divSets.put("@else",new HashSet<>());
        for(Record record : records){
            //相同，放入第一个集合
            if(getValue(attr,record).equals(value)){
                divSets.get(value).add(record);
            }else{//不同，放入第二个集合
                divSets.get("@else").add(record);
            }
        }
        return divSets;
    }


    //按照某个属性划分之后，信息增益的大小
    private double gainPartition(Set<Record> records, String attributeName){
        double gain=entropy(records);
        Map<String,Set<Record>> count = divByAttr(records,attributeName);

        //累加每种值的贡献
        for(Map.Entry<String,Set<Record>> entry: count.entrySet()){
            gain-=(double)entry.getValue().size()/records.size()*entropy(entry.getValue());
        }
        return gain;
    }

    //找出信息熵增益最大的那个属性，返回该属性名称
    private String findMaxPartition(Set<Record> records, Set<String> attrNames){
        double maxGain=0;//记录最大信息增益和对应属性名
        String maxGainName="";
        //枚举可用属性名称，寻找信息增益最大的那一个
        for(String attrName : attrNames){
            double gain= gainPartition(records,attrName);
            if(gain > maxGain){
                maxGain=gain;
                maxGainName=attrName;
            }
        }
        return maxGainName;
    }


    //判断是否所有记录都属于同一类别
    private boolean haveSameLabel(Set<Record> records){
        int first=1;
        String tmp = null;
        for(Record record : records){
            if(first == 1){
                tmp=record.label;
                first=0;
            }else{
                if(!record.label.equals(tmp)) return false;
            }
        }
        return true;
    }

    //判断是否某些记录中对于某些属性，值是一样的
    private boolean haveSameValue(Set<Record> records, Set<String> attrNames){
        int first=1;
        Record tmp=null;
        for(Record record : records){
            if(first==1){
                //取第一个作为参照
                first=0;
                tmp=record;
            }
            else{
                //遍历属性，判断是否都一样，只要有一个不同就不满足条件（只有一条记录时一定满足条件）
                for(String attr : attrNames) {
                    if(!getValue(attr,tmp).equals(getValue(attr,record))){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //找出records中出现次数最多的label，有多个label出现次数一样多时，随机返回一个
    private String findTheMostLabel(Set<Record> records){
//        Map<String,Integer> vis = ;
        return Find.findMost(countLabel(records));
    }

    //获取连续属性的值，从0开始标号
    private String getContValue(String attrName, double value){
        ArrayList<Double> sep=dividePoint.get(attrName);
        for(int i=1;i<sep.size();++i){//对每个分界点依次比较
            if(value < sep.get(i)){
                return String.valueOf(i);
            }
        }
        return String.valueOf(N-1);//都没找到就是最后一个区域的
    }

    //快速获取某一个记录的某个属性值，免去判断离散或连续
    private String getValue(String attrName, Record record){
        if(isContinuous(attrName)){
            return getContValue(attrName,record.attrCont.get(attrName));
        }else{
            return record.attrDisc.get(attrName);
        }
    }

    //计算基尼系数
    private double Gini(Set<Record> records){
        Map<String,Integer> count = countLabel(records);
        double result = 1;
        for(Map.Entry<String,Integer> entry : count.entrySet()){
            double tmp = (double)entry.getValue()/records.size();
            result -= tmp*tmp;
        }
        return result;
    }

    //按照某种属性划分的Gini系数
    private Pair<Double,String> Gini(Set<Record> records, String attrName, Set<String> v){
        double result = Double.MAX_VALUE;
        String value="";
        for(String s : v){//遍历每个属性值
            //划分为两部分
            Set<Record> tmp = new HashSet<>();//等于这个属性值的事务
            Set<Record> left = new HashSet<>();//不等于这个属性值的事务
            for(Record record : records){//把每个事务按照是否等于该属性值划分
                String curv = getValue(attrName,record);
                if(curv.equals(s)) tmp.add(record);
                else left.add(record);
            }
            double ans=(double)tmp.size()/records.size()*Gini(tmp)+(double)left.size()/records.size()*Gini(left);
            if(ans < result){
                result = ans;
                value = s;
            }
        }
        return new Pair<>(result,value);
    }

    //给定记录、属性、和属性的可选值，计算最小的基尼指数对应的属性名称，返回属性名和对应的值
    private Pair<String,String> findMinGini(Set<Record> records, Set<String> attrs, Map<String,Set<String>> values){
        double result = Double.MAX_VALUE;
        String value = "";
        String answer = "";
        for(String attr : attrs){//枚举属性
            Pair<Double,String> cur = Gini(records, attr, values.get(attr));
            if(cur.getKey() < result){
                result = cur.getKey();
                value = cur.getValue();
                answer = attr;
            }
        }
        return new Pair<>(answer,value);
    }

    //从一个列表里随机选出m个元素
    private Set<String> randomChoose(ArrayList<String> set,int m){
        Set<String> result = new HashSet<>();
        Set<Integer> vis = new HashSet<>();
        for(int i=0;i<m;++i){
            int pos = random.nextInt(set.size());
            while(vis.contains(pos)){
                pos = random.nextInt(set.size());
            }
            vis.add(pos);
            result.add(set.get(pos));
        }
        return result;
    }


    //判断某种属性是否是连续的
    public boolean isContinuous(String attrName){
        return categoryOfAttr.get(attrName).equals("continuous");
    }

    //建单独的树
    private TreeNode rootTree_ID3(Set<Record> records,Set<String> attrNames, int dep){
        System.out.println("------");
        TreeNode root=new TreeNode();
        root.records=records;
        //同属一个类别，标记后返回
        if(haveSameLabel(records)) {
            for (Record record : records) {
                root.label = record.label;
                System.out.println("dep:"+dep+" 叶子节点,类别"+root.label);
                return root;
            }
        }

        //没有可用来划分的属性或者所有记录在可用属性都有相同的值
        if(attrNames.size() == 0 || haveSameValue(records,attrNames)){
            root.label=findTheMostLabel(records);
            System.out.println("dep:"+dep+" 叶子节点,类别"+root.label);
            return root;
        }

        String name = findMaxPartition(records,attrNames);

        if(name.equals("")){
            name = attrNames.stream().findFirst().orElse(name);
        }
        root.attrName=name;
        System.out.println("dep:"+dep+" 选择的属性:"+root.attrName);

        //构建新的可用属性集合（去除本节点使用的）
        Set<String> newAttrNames = new HashSet<>(attrNames);

        newAttrNames.remove(name);
        //按照信息增益最大的属性划分集合，并且每一个集合产生一个儿子结点
        Map<String,Set<Record>> divSets=divByAttr(records,name);
        for(Map.Entry<String,Set<Record>> entry: divSets.entrySet()){
            AttributeField attributeField = new AttributeField();
            attributeField.name=name;
            if(!isContinuous(name)) {
                attributeField.attrValueDisc = entry.getKey();
                System.out.println("attrValueDisc:"+attributeField.attrValueDisc);
            }
            else{
                attributeField.attrValueCont = entry.getKey();
                System.out.println("attrValueCont:"+attributeField.attrValueCont);
            }
            root.children.put(attributeField,rootTree_ID3(entry.getValue(),newAttrNames,dep+1));
        }
        return root;
    }

    private TreeNode rootTree_CART(Set<Record> records,Set<String> attrs,Map<String,Set<String>> values){
        TreeNode root=new TreeNode();
        root.records=records;
        //同属一个类别，标记后返回
        if(haveSameLabel(records)) {
            for (Record record : records) {
                root.label = record.label;
                return root;
            }
        }

        //所有记录在可用属性都有相同的值
        if(haveSameValue(records,attrs)){
            root.label=findTheMostLabel(records);
            return root;
        }

        //Key为属性名，value为值
        Pair<String,String> pair = findMinGini(records,attrs,values);

        if(pair.getKey().equals("")){
            System.out.println("pair.getKey为空");
//            pair.getKey() = attributesSet.stream().findFirst().orElse(name);
        }
        root.attrName=pair.getKey();

        //构建新的可用属性集合（去除本节点使用的）
        Set<String> newAttrNames = new HashSet<>(attrs);
        Map<String,Set<String>> newValues = new HashMap<>(values);
        if(values.get(root.attrName).size() == 2){//使用的属性只有两个值了，应该将这个属性删除
            newAttrNames.remove(root.attrName);
            //此处偷懒，不删values中的键值对，因为后面也不会用到
        }
        else{
            //属性剩余值多于两个，删除属性值，不删除属性
            newValues.get(root.attrName).remove(pair.getValue());
        }

        //按照信息增益最大的属性划分集合，按照等于值和不等于，划分为两个儿子节点
        Map<String,Set<Record>> divSets = divByAttr_CART(records,pair.getKey(),pair.getValue());

        for(Map.Entry<String,Set<Record>> entry: divSets.entrySet()){
            AttributeField attributeField = new AttributeField();
            attributeField.name=pair.getKey();
            if(!isContinuous(pair.getKey())) {
                attributeField.attrValueDisc = entry.getKey();
            }
            else{
                attributeField.attrValueCont = entry.getKey();
            }
//            root.children.put(attributeField,rootTree_CART(entry.getValue(),newAttrNames));
        }
        return root;
    }


    //构建随机森林
    private TreeNode rootForest(Set<Record> records,int m,int deep){
        TreeNode root=new TreeNode();
        root.records=records;
        //同属一个类别，标记后返回
        if(haveSameLabel(records)) {
            for (Record record : records) {
                root.label = record.label;
                return root;
            }
        }
        Set<String> chosen = randomChoose(attributes,m);

        //所有记录在随机选择的属性都有相同的值
        if(haveSameValue(records,chosen)){
            root.label=findTheMostLabel(records);
            return root;
        }

        Map<String,Set<String>> values = new HashMap<>();
        for(String attr : chosen){
            values.put(attr,ValueOfAttrs.get(attr));
        }

        Pair<String,String> pair = findMinGini(records,chosen,values);
        root.attrName=pair.getKey();
        Map<String,Set<Record>> divSets=divByAttr_CART(records,pair.getKey(),pair.getValue());

        //和普通建树一样的划分过程
        for(Map.Entry<String,Set<Record>> entry: divSets.entrySet()){
            AttributeField attributeField = new AttributeField();
            attributeField.name=root.attrName;
            if(!isContinuous(root.attrName)) {
                attributeField.attrValueDisc = entry.getKey();
            }
            else{
                attributeField.attrValueCont = entry.getKey();
            }
            root.children.put(attributeField,rootForest(entry.getValue(),m,deep+1));
        }
        return root;
    }

    //对一条新记录预测其分类
    private String classify(TreeNode root,Record record){
        if(root.isLeaf()){
            return root.label;
        }
        //连续的属性需要划分为离散值
        String value = getValue(root.attrName,record);//该条新记录中该节点分支属性的值

        //value该分支到哪个儿子
        int pass=0;//记录分支中是否有"@else"字符串，为下一步做准备
        for(Map.Entry<AttributeField, TreeNode> entry : root.children.entrySet()){
            if(!isContinuous(root.attrName) && entry.getKey().attrValueDisc.equals(value)){//判断该分支属性值是否和该记录相同
                return classify(entry.getValue(), record);
            }
            else if(isContinuous(root.attrName) && entry.getKey().attrValueCont.equals(value)){
                return classify(entry.getValue(), record);
            }
            String cv=entry.getKey().attrValueCont;
            String dv=entry.getKey().attrValueDisc;
            if(cv!=null && cv.equals("@else")) ++pass;
            else if(dv!=null && dv.equals("@else")) ++pass;
        }

        //如果到达这里，说明没有匹配上，而有else的情况下（CART建树），就一定是往else的分支走
        if(pass==1){
            for(Map.Entry<AttributeField, TreeNode> entry : root.children.entrySet()){
                String cv=entry.getKey().attrValueCont;
                String dv=entry.getKey().attrValueDisc;
                if((cv!=null && cv.equals("@else")) || (dv!=null && dv.equals("@else"))){
                    return classify(entry.getValue(), record);
                }
            }
        }

        //仍然没有匹配上，就随机划分(划分给第一个)，因为这说明训练集中没有对应的值
//        System.out.printf("分类出现错误，出现了训练时没有出现的属性值，属性名称:%s,属性值:%s\n",root.attrName,value);
        for(Map.Entry<AttributeField, TreeNode> entry : root.children.entrySet()){
            return classify(entry.getValue(), record);
        }
        return "";
    }

    //打印记录，调试语句
    private void printRecords(){
        for(Record record : records){
            for(Map.Entry<String,String> entry : record.attrDisc.entrySet()){
                System.out.print(entry.getKey()+":"+entry.getValue()+", ");
            }
            for(Map.Entry<String,Double> entry : record.attrCont.entrySet()){
                System.out.print(entry.getKey()+":"+entry.getValue()+", ");
            }
            System.out.println();
        }
    }

    //加载测试数据
    public Set<Record> loadTestRecord(String filePath){
        Set<Record> records = new HashSet<>();
        try{
            File file = new File(filePath);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String s;

            int cnt=0;
            while((s = br.readLine())!=null){//以下为特征
                if(s.length()==0) continue;
                Record record = getRecord(s,1);
                record.ID=++cnt;
                records.add(record);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        fillMissing(records,attributes,this);
        return records;
    }

    //加载训练数据
    public void loadTrainRecord(String filePath){
        Map<String,Double> minV=new HashMap<>();//记录每种连续属性的最大最小值
        Map<String,Double> maxV=new HashMap<>();
        try{
            File file = new File(filePath);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String s;
            //在trainData前面应该有对于哪些属性是连续属性的描述,并且与数据间存在一空行
            attributes = new ArrayList<>();
            while((s = br.readLine())!=null){
                if(s.length()==0) break;//空行，标识属性描述结束
                String name=s.substring(0,s.indexOf(":")).trim();
                attributes.add(name);
                if(s.substring(s.indexOf(":")+2,s.length()-1).equals("continuous")){
                    categoryOfAttr.put(name,"continuous");//标记为连续
                }
                else categoryOfAttr.put(name,"discrete");//标记为离散，具体值忽略
            }
            attributesSet = new HashSet<>(attributes);

            int cnt=0;
            while((s = br.readLine())!=null){//读取属性值
                if(s.length()==0) continue;//忽略空行
                Record record=getRecord(s,0);
                record.ID=++cnt;
                for(Map.Entry<String,Double> entry : record.attrCont.entrySet()){
                    if(entry.getValue()==Double.MAX_VALUE) continue;//略过缺失值
                    if(minV.containsKey(entry.getKey())){
                        minV.put(entry.getKey(),Math.min(minV.get(entry.getKey()),entry.getValue()));
                        maxV.put(entry.getKey(),Math.max(maxV.get(entry.getKey()),entry.getValue()));
                    }else{
                        minV.put(entry.getKey(),entry.getValue());
                        maxV.put(entry.getKey(),entry.getValue());
                    }
                }
                records.add(record);
            }

            //填充缺失值
            fillMissing(records,attributes,this);
        }catch (Exception e){
            e.printStackTrace();
        }
        //计算总共有多少种类别，并给每种类别分配编号
        int cnt=0;
        for(Record record : records){
            if(!idOfLabel.containsKey(record.label)){
                idOfLabel.put(record.label,cnt++);
            }
        }

        Labels = new String[idOfLabel.size()];
        for(Map.Entry<String,Integer> entry : idOfLabel.entrySet()){
            Labels[entry.getValue()]=entry.getKey();
        }

        //划分连续属性
        for(Map.Entry<String,Double> entry : minV.entrySet()){
            double m=entry.getValue();//该属性最小值
            double M=maxV.get(entry.getKey());//该属性最大值
            System.out.println("属性："+entry.getKey()+" 最小值:"+m+" 最大值:"+M);
            ArrayList<Double> divide = new ArrayList<>();
            for(int i=0;i<=N;++i){//N+1个点划分
                divide.add((m*(N-i)+M*i)/N);
            }
            System.out.println(divide);
            dividePoint.put(entry.getKey(),divide);
        }

        //统计每种属性的值
        for(Record record : records){
            for(String attr : attributesSet){
                if(!ValueOfAttrs.containsKey(attr)){//没有这个属性就加进去
                    ValueOfAttrs.put(attr,new HashSet<>());
                }
                ValueOfAttrs.get(attr).add(getValue(attr,record));//加入值
            }
        }
    }

    public void buildTree_ID3(){
        root=rootTree_ID3(records,attributesSet,1);
    }

    public void buildTree_CART(){
        root=rootTree_CART(records,attributesSet,ValueOfAttrs);
    }


    //k:要构造多少颗树,m:随机抽取多少个属性，随机森林只用CART方式建树
    public void buildRandomForest(int k,int m){
        for(int i=0;i<k;++i){
            Set<Record> recordSet = new HashSet<>();
            ArrayList<Record> tmp = new ArrayList<>(records);
            for(int j=0;j<records.size();++j)
                recordSet.add(tmp.get(random.nextInt(records.size())));
            forest.add(rootForest(recordSet,m,0));
            System.out.println(i+"-th tree is built.");
        }
    }

    //给测试数据投票
    public Set<Record> voteTest(Set<Record> testRecords){
        Set<Record> result = new HashSet<>(testRecords);
        for(Record record : result){
            Map<String,Integer> count = new HashMap<>();
            for (TreeNode treeNode : forest) {
                String label = classify(treeNode, record);
                if (count.containsKey(label)) {
                    int tmp = count.get(label);
                    count.put(label, tmp + 1);
                } else {
                    count.put(label, 1);
                }
            }
            record.label = Find.findMost(count);
        }
        return result;
    }

    //外部调用的函数，将test数据全部分类
    public Set<Record> classifyTest(Set<Record> testRecords){
        Set<Record> result = new HashSet<>(testRecords);
        for(Record record : result){
            record.label=classify(root, record);
        }
        return result;
    }

    //分析结果，打印混淆矩阵
    public void analyse(Set<Record> result){
        //先计算有多少正确的
        int ok=0;
        int[][] check = new int[idOfLabel.size()][idOfLabel.size()];//第一维实际，第二维预测
        for(Record record : result){
            ++check[idOfLabel.get(record.answer)][idOfLabel.get(record.label)];
            if(record.label.equals(record.answer)) ok+=1;
        }
        System.out.printf("测试集共%d个数据，共正确%d个，错误%d个，正确率%.3f%%(上方Label为测试集给出的答案)\n",result.size(),ok,result.size()-ok,(double)ok/result.size()*100);

        //打印首行
        System.out.print("\t");
        for (String name : Labels) System.out.print(name + "\t");
        System.out.println();

        for(int i=0;i<Labels.length;++i){
            System.out.print(Labels[i]+"\t");
            for(int j=0;j<Labels.length;++j)
                System.out.print(check[i][j]+"\t");
            System.out.println();
        }
//        printRecords();
    }
}
