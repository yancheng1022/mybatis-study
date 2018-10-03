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
             select:调用select方法查出的结果  column：指定将哪一列的值传给这个方法
    