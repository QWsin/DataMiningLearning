# 各文件夹介绍
未包含独立文件夹的java代码是测试时使用的，其余是算法代码

|--Classify    分类算法

|----DecisionTree.java    决策树与随机森林算法，包含CART和ID3两种算法

|----KNN.java    KNN/K邻近算法

|--Cluster    聚类算法

|----DBSCAN.java    密度聚类

|----Kmeans.java    K均值算法

|----PicUtility.java    聚类结果画图工具

|--common    一些工具函数

|----Count.java    统计相关函数

|----Find.java    寻找相关函数

|----Init.java    初始化相关函数（比如初始化一个n*m的ArrayList)

|----MyMath.java    基础的数学函数

|----ReadFile.java    读取文件同时做相关操作（比如读取一行并转化为double数组）

|--FeatureSelection    特征筛选

|----Weka.java    调用Weka的特征筛选包

|--FrequentPatternMining    频繁模式挖掘

|----Apriori.java    Apriori算法

|--Graph    自制的图相关内容

|----Graph.java    保存图的结构体，包含加边等基础操作

|--InstanceOutput    调试所用

|----Output.java    打印数据，打印步骤等

|--MissingValues    缺失值填充

|----SetRecord    对Set<Record>类型进行缺失值填充

|----Weka    对Weka的Instance类型数据进行缺失值填充

|--Normalize    工具，进行化归操作

|----ListArray    将ArrayList内数据缩放到指定范围

|----Weka    使用Weka的归一化处理算法

|--Recommend    推荐算法

|----BiGraph    二部图推荐算法，包含物质扩散和热传导两个方法

|----LoadData    工具，将评分列表转为矩阵

|----MatrixBased    基于模型的协同过滤（矩阵因子分解），存在一定问题。后来通过Matlab实现的，代码附在文件末尾

|----UserItemCF    基于邻域的协同过滤（基于用户的、基于项的）

|--SortVertex    顶点排序（影响力排序）

|----Comentropy    信息熵模型

|----Degree    包括基于度的（认为度大则影响力大）、H-index排序，Kshell排序

|----Probability    概率模型（每个点v做一次计算，感染点以概率p感染邻居，将所有点概率加起来作为v的权值）

|--SpreadSimulation    传播仿真

|----SIR    SIR传播仿真