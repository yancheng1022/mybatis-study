1.mybatis的使用： 
  原始方法：
    1.根据配置文件(数据源+注册sql映射文件  --和spring整合的时候数据源一般交给spring管理)创建一个sqlSessionFactory对象
    2.编写sql映射文件 （namespace可随意）
    3.获取sqlSession实例
    4.调用sqlSession方法执行增删改查(sqlSession.selectOne()参数包括mapper的namespace和入参)

  接口式编程：
    1.根据配置文件(数据源+注册sql映射文件)创建一个sqlSessionFactory对象
    2.创建接口
    3.编写sql映射文件 （namespace 和 id 分别对应）
    4.调用sqlSession.getMapper(接口类) 通过动语代理创建接口实现
    5.调用接口的方法
 
2.mybatis配置文件（配置的先后顺序要注意）
    （1）settings  包含很多重要的设置项   name：设置项名   value：设置项取值（如驼峰规则）
    （2）typeAliases  别名处理器，为java类型起别名 type：指定要起别名的类型全类名 默认别名是小写 也可通过alias指定
    （3）package  批量起别名  name：包名（为当前包及以下的类起一个默认别名）  默认类名小写
             （如果存在包中包有一样的类名，可通过@Alias 注解 给另一个重起一个）
         建议不用别名，这样好找
    （4）environments （必须有TransactionManager和datasource 两个标签） id：当前环境的唯一标识
    （5）mappers 将sql映射注册  两个属性：resource（mapper映射文件位置）和class（和映射文件同名并在同一包下）
    （6）package 批量注册 name 包名
   
3.mapper映射文件
    (1)insert后获取自增主键的值：<insert useGeneratedKeys="true" keyProperty="id">
    (2)mybatis参数处理： 1）单个参数不做任何处理 2）多个参数时，默认会将参数封装成map。key为param1，param2或0，1
                        这样不能见名知意，可以使用命名参数的形式 接口中@param("id")可以在xml中通过id取
    (3)多个参数可以直接传map（不经常使用的情况下）  但经常使用，编写一个TO（transfer object）数据传输对象（比如分页）
4.@mapkey("id")  告诉mybatis封装map的时候用哪个属性作为key

5.resultMap：自定义结果集映射规则 （type：自定义规则的Java类型  id：唯一id方便引用）

6.联合查询 （1）result采用级联属性<column column="d_id" property="dept.id">
          (2)association 指定联合的javabean对象<association property="dept" javaType="com.xx">
          (3)association 分步查询 <association property="dept" select=com.xxx.getDeptById column="d_id">
             select:调用select方法查出的结果(是另一个表中的查询)  column：指定将哪一列的值传给这个方法
    
7.collection指定关联集合封装规则：<collection property="emps" ofType="com.xx">id..result</collection>

8.懒加载的实现：在上面association 分步查询的基础上加两个配置<setting name="lazyLoadingEnabled" value="true">
<setting name="aggressiveLazyLoading" value="false">
(1)lazyLoadingEnabled 
此属性控制是否启用延迟加载功能，是全局配置，默认值为false，要想在项目中启用延迟加载功能，需要将这个属性设置为true。
(2)aggressiveLazyLoading 
此属性控制触发延迟加载属性的方式，是全局配置，默认值为true，表示只要有一个延迟加载的属性被使用，所有延迟加载的属性都会加载；false表示按需要加载。

9.mybatis动态sql  (OGNL表达式)： 

   if,where,trim,choose,when,set，foreach,bind,aql
    
(1)<if test="id != null"> 

(2)关于多条件中and可能产生的问题：用<where>标签括住多个if判断条件，并且把and放在条件的前边

(3)对于where不能解决后面多and的情况，可以用trim标签<trim prefix="where" suffixOverrides="and">

(4)<choose><when test="id!=null">id=#{id}</when></choose>

(5)对于更新时可能出现的逗号问题，可以使用<set>标签（同样可以和where 一样用trim代替）

(6)select * from user where id in 
    <foreach collection="ids" item="id" separator="," open="(" close=")">#{item.id}</foreach>
    
(7)mybatis的两个内置参数：_parameter:代表整个参数  _databaseId:如果配置了databaseIdProvider标签，_databaseId就代表了当前数据库的别名

(8)bind 可以将OGNL表达式的值绑定到一个变量中，方便后来引用这个变量的值（比如like查询）
<bind name="_lasrname" value="'%'+lastname+'%'">

(9)<sql>标签：抽取可重用的sql字段，方便后面引用
<sql id="insertColumn">employee_id,last_name,email</sql>
然后使用<include refid="insertColumn"></include>

11.mybatis的两级缓存：一级缓存（本地缓存)：sqlSession 级别的缓存，与数据库同一次会话期间查询到的数据放在本地缓存中，以后要获取相同的数据，直接从缓存中拿
  二级缓存（全局缓存）


12.一级缓存失效的情况：（1）sqlSession不同 （2）sqlSession相同，查询条件不同（3）sqlSession相同，两次查询间执行了增删改操作（4）手动清除缓存（sqlsession.clearCache()）

13.二级缓存:(全局缓存)基于nameSpace级别的缓存，一个namespace对应一个二级缓存 。如果会话关闭，一级缓存中的内容会保存到二级缓存中，新的会话查询信息，就可以
参照二级缓存中的内容

14.使用二级缓存：（1）开启全局二级缓存（默认开启）<setting name="cacheEnable" value="true">（2）mapper.xml中配置二级缓存<cache ></chche>  （3）pojo需要实现序列化接口

15.<cache>中的属性：（1）eviction:缓存回收策略（2）flushInterval缓存刷新间隔（3）readOnly只读，默认false （只读情况下会将缓存中的引用交给用户，不安全。 非只读情况下，会用序列化&反序列化技术克隆一份数据，速度快）（4）size：缓存存放多少元素

16.mybatis二级缓存回收策略（eviction）：(1)LRU 最近最少使用，移除长时间不适用的对象 (2)FIFO 按照进入缓存的顺序移除（3）SOFT软引用（4）弱引用

17.缓存原理：会话查到数据时会添加到一级缓存（sqlSession），会话关闭时，会将一级缓存中的数据添加到二级缓存（nameSpace）。当查缓存中的数据时，先是在二级缓存中查找，再在一级缓存中查找（缓存的本质就是map）

18.mybatis逆向工程（MGB）：mybatis-generator ：根据表生成对应的映射文件，接口，bean类

19.mybatis分页插件 PageHelper （1）引入jar包 （2）mybatis配置文件配置plugin （3）PageHelper.startPage(1,10)

