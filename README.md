#####本项目最初是用于Playtech summer internship任务。后经改进，具有如下功能：

本项目创建了一个用于数据接受及统计的工具。本工具可以从指定端口监听由服务器发送来的数据，将这些数据经过计算聚合之后存储到本地，远程用户可以向本机发送指令查询存储的数据，本机将会以json的形式将其返回。详情如下：
###一、数据聚合：
###1. 接受数据：
由服务器发送来的数据为
> MetricPath MetricValue Timestamp

的格式，例如
> local.random.diceroll 321 1461163460175
###2. 数据存储：
接收到的数据按照每一秒metric value的平均值和最大值以及每分钟metric value的平均值和最大值分别存储，存储位置与metric path相同，如从local.random.diceroll接受到的数据，最后的存储形式就为：

> - local/random/diceroll.1SecondAvg
> - local/random/diceroll.1SecondMax
> - local/random/diceroll.1MinuteAvg
> - local/random/diceroll.1MinuteMax

每个存储下来的文件中的每一行包括timestamp和计算过的value，如：
1461141000 123
1461142000 134
1461143000 101

###二、数据查询：
查询的输入形式为url的形式，具体格式如下：

/aggr/query

参数:

	pattern=<string>, 必须–路径的正则表达式.
	start=<timestamp>, 必须–开始时间
	end=<timestamp>, 必须 –结束时间

比如，
> http://localhost:9090/aggr/query?pattern=local.random.*.1SecondAvg&start=1461162542693&end=1461162682693

查询的就是localhost:9090这台主机上所有路径为local.random开头的每秒钟接受到数据的平均值，时间为1461162542693到1461162682693之间。

数据的返回形式为json，具体参数如下：

	name=<string>，必须 存储的文件名
	datapoints，一个数组，有两个元素，第一个是timestamp，第二个是value

如：
> [{"name":"local.random.diceroll.1SecondMax","datapoints":[[1461163460175,123],[1461163461175,145],[1461163462175,101]]},{"name":"local.random.diceroll.1SecondAvg","datapoints":[[1461163460175,12],[1461163461175,20],[1461163462175,13]]}]

##程序实现：
#####说明：

1. 程序的实现使用Java语言
1. 程序使用maven作为项目管理工具，所需依赖都可与配置文件中找到
1. 使用jetty作为web容器

###一、数据聚合：
1. 使用socket编程TCP连接来模拟服务器（数据处理）与客户端（发送数据）
2. 服务器端为com.playtech.summerinternship.collectinfo.Server，客户端为com.playtech.summerinternship.collectinfo.Client，默认发送地址为localhost端口8888，可以模拟发送local.random.diceroll 321 1461140237格式的数据。
3. com.playtech.summerinternship.Data存储接收到的数据信息。
###二、数据查询：
1. 本部分使用了jersey，主要是在com.playtech.summerinternship.rest.DataQueryService中使用，主要是用来从url中提取出查询的参数以及将bean转换为json字符串；
2. com.playtech.summerinternship.GetDataPoints作为一个工具类，主要是从DataQueryService中接受查询参数，使用这些参数找到相应的数据。该类将会返回一个bean的ArrayList，该ArrayList将会在稍后被转换为json数组；
3. com.playtech.summerinternship.DataForQuery也是一个bean，它用来存放所要查询的聚合之后数据，之后被转换为json的就是这个bean。

####备注：
1. 最近正在考虑将该项目改为多线程工具，但由于目前知识储备还不够丰富，遇到一些问题无法解决，正在想法攻克。
2. 对于按照时间存储数据，我的方法太过繁琐，听说Linux上有专门的工具可以控制程序按时间运行，正在研究，如果有人知道还请不吝告知
3. 我知道本项目结构比较混乱，主要是一开始没打算做大（其实现在也不大），想着几行代码完事，包什么的也没有细分，如果之后要完善，需要对项目结构进行调整。