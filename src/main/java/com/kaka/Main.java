package com.kaka;

import com.kaka.bean.User;
import com.kaka.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import sun.misc.FpUtils;

import java.io.IOException;
import java.io.InputStream;

/*
  原始方法：
    1.根据配置文件(数据源+注册sql映射文件)创建一个sqlSessionFactory对象
    2.编写sql映射文件 （namespace可随意）
    3.获取sqlSession实例
    4.调用sqlSession方法执行增删改查(sqlSession.selectOne()参数包括mapper的namespace和入参)

  接口式编程：
    1.根据配置文件(数据源+注册sql映射文件)创建一个sqlSessionFactory对象
    2.创建接口
    3.编写sql映射文件 （namespace 和 id 分别对应）
    4.调用sqlSession.getMapper(接口类) 通过动语代理创建接口实现
    5.调用接口的方法

*/
public class Main {

    public static void main(String[] args) throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        //获取sqlSession实例
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //原始方案
        User user1 = sqlSession.selectOne("com.kaka.mapper.UserMapper.getUserById", 1);
        System.out.println(user1);

        //面向接口的编程
        //(会为接口自动创建一个代理对象，用代理对象去进行增删改查)
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user2 = userMapper.getUserById(1);
        System.out.println(user2);

    }
}
