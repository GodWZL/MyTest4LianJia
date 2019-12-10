# MyTest4LianJia
这是一个关于链家二手房信息，并进行分析的个人练习项目

具体需求：
    ①：通过分析网站上二手房信息，分析杭州市的具体房价分布，以及二手房中位价与均价
    ②：通过分析二手房的上架时间，以及下架时间，判断网站的交易情况

数据获取部分：使用的是javaClient 与 jsoup 循环的获取链家网站的二手房数据，每天执行一次获取当天的实时数据

数据处理部分 ： 使用spark-core进行处理


