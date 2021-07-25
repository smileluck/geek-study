[toc]

---



# 题目

**1.（选做）**使 Java 里的动态代理，实现一个简单的 AOP。
**2.（必做）**写代码实现 Spring Bean 的装配，方式越多越好（XML、Annotation 都可以）, 提交到 GitHub。
**3.（选做）**实现一个 Spring XML 自定义配置，配置一组 Bean，例如：Student/Klass/School。

**4.（选做，会添加到高手附加题）**
4.1 （挑战）讲网关的 frontend/backend/filter/router 线程池都改造成 Spring 配置方式；
4.2 （挑战）基于 AOP 改造 Netty 网关，filter 和 router 使用 AOP 方式实现；
4.3 （中级挑战）基于前述改造，将网关请求前后端分离，中级使用 JMS 传递消息；
4.4 （中级挑战）尝试使用 ByteBuddy 实现一个简单的基于类的 AOP；
4.5 （超级挑战）尝试使用 ByteBuddy 与 Instrument 实现一个简单 JavaAgent 实现无侵入下的 AOP。

**5.（选做）**总结一下，单例的各种写法，比较它们的优劣。
**6.（选做）**maven/spring 的 profile 机制，都有什么用法？
**7.（选做）**总结 Hibernate 与 MyBatis 的各方面异同点。
**8.（必做）**给前面课程提供的 Student/Klass/School 实现自动配置和 Starter。
**9.（选做**）学习 MyBatis-generator 的用法和原理，学会自定义 TypeHandler 处理复杂类型。
**10.（必做）**研究一下 JDBC 接口和数据库连接池，掌握它们的设计和用法：
1）使用 JDBC 原生接口，实现数据库的增删改查操作。
2）使用事务，PrepareStatement 方式，批处理方式，改进上述操作。
3）配置 Hikari 连接池，改进上述操作。提交代码到 GitHub。

**附加题（可以后面上完数据库的课再考虑做）：**
(挑战) 基于 AOP 和自定义注解，实现 @MyCache(60) 对于指定方法返回值缓存 60 秒。
(挑战) 自定义实现一个数据库连接池，并整合 Hibernate/Mybatis/Spring/SpringBoot。
(挑战) 基于 MyBatis 实现一个简单的分库分表 + 读写分离 + 分布式 ID 生成方案。




# 作业1 



[源代码地址]（https://github.com.cnpmjs.org/smileluck/geek-study/tree/main/project/src/main/java/top/zsmile/aop/）



## Cglib

实现源文件：CglibTest.java

```java
public class CglibTest {
    public static void main(String[] args) {
        CglibProxy proxy = new CglibProxy();
        PersonMan1 personMan1 = (PersonMan1) proxy.getProxy(PersonMan1.class);

        System.out.println("动态代理类型：" + personMan1.getClass().getName());
        String say = personMan1.say();
        System.out.println("personSay: " + say);
    }
}
```

```java
public class CglibProxy implements MethodInterceptor {
    private Enhancer enhancer = new Enhancer();
    public Object getProxy(Class clazz){
        //设置父类
        enhancer.setSuperclass(clazz);
        // 设置回调对象
        enhancer.setCallback(this);
        return enhancer.create();
    }
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("前置代理");
        //通过代理类调用父类中的方法
        Object result = methodProxy.invokeSuper(o, objects);

        System.out.println("后置代理");
        return result;
    }
}
```





## Proxy

实现源文件：ProxyTest.java

```java
public class ProxyTest {
    public static void main(String[] args) {
        Person person = new PersonMan();

        InvokePersonHandler invokePersonHandler = new InvokePersonHandler(person);
        ClassLoader classLoader = person.getClass().getClassLoader();
        Class<?>[] interfaces = person.getClass().getInterfaces();

        Person person1 = (Person) Proxy.newProxyInstance(classLoader, interfaces, invokePersonHandler);

        System.out.println("动态代理类型：" + person1.getClass().getName());
        String s = person1.sayHello("123");
        System.out.println("sayHello:" + s);
    }
}
```

```java
public class InvokePersonHandler implements InvocationHandler {

    private Object person;

    public InvokePersonHandler(Object person){
        this.person = person;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("执行前");

        Object invoke = method.invoke(person, args);

        System.out.println("执行后");
        return invoke;
    }
}
```



# 作业2



[源代码地址]（https://github.com.cnpmjs.org/smileluck/geek-study/tree/main/project/src/main/java/top/zsmile/spring/）

## 测试代码

```java
public class SpringDemo01 {


    public static void main(String[] args) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("ApplicationBeanContext.xml");
        School1 school22 = (School1) applicationContext.getBean("School22");
        System.out.println("getBean By Name :" + school22.kaixue());

        AnnotationBean s1 = applicationContext.getBean(AnnotationBean.class);
        System.out.println("getBean By Class ,Annotation @Resource:" + s1.getSchool1().kaixue());

        XmlBean bean = applicationContext.getBean(XmlBean.class);
        System.out.println("getBean By Class ,Xml set：" + bean.getStudent().getId() + "," + bean.getStudent().getName());
        System.out.println("getBean By Class ,Xml constructor：" + bean.getStudent200().getId() + "," + bean.getStudent200().getName());
    }
}
```

## xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                        http://www.springframework.org/schema/beans/spring-beans-3.2.xsd
                        http://www.springframework.org/schema/context
                        http://www.springframework.org/schema/context/spring-context-3.2.xsd http://www.springframework.org/schema/aop https://www.springframework.org/schema/aop/spring-aop.xsd">

    <context:component-scan base-package="top.zsmile.spring.**"/>

    <!--set方式-->
    <bean id="student100" class="top.zsmile.spring.entity.Student">
        <property name="id" value="100"/>
        <property name="name" value="s100"/>
    </bean>

    <!--constructor-->
    <bean id="student200" class="top.zsmile.spring.entity.Student">
        <constructor-arg value="200" type="int"/>
        <constructor-arg value="s200" type="java.lang.String"/>
    </bean>

</beans>
```

```java
@Configuration
@Data
@ComponentScan
public class XmlBean {

    @Resource(name = "student100")
    private Student student;

    @Resource(name = "student200")
    private Student student200;
}
```

## annotation

```java
@Component
public class SchoolFactory {

    @Bean("School22")
    private School1 createSchool() {
        return new School1();
    }
}
```

```java
@Component
@Data
@ComponentScan
public class AnnotationBean {

    @Resource(name = "School22")
    private School1 school1;
}
```



# 作业8

[源代码地址]（https://github.com.cnpmjs.org/smileluck/geek-study/tree/main/boot-project/src/main/java/top/zsmile/spring）

[测试地址]（https://github.com.cnpmjs.org/smileluck/geek-study/tree/main/boot-project/src/test/java/top/zsmile/spring）

## additional-spring-configuration-metadata

```json
{
  "groups": [
    {
      "name": "zsmile.springboot",
      "type": "top.zsmile.spring.MyAutoConfiguration"
    }
  ],
  "properties": [
    {
      "name": "zsmile.springboot.enabled",
      "type": "java.lang.Boolean",
      "description": "whether enable or not",
      "defaultValue": true
    },
    {
      "name": "zsmile.springboot.student",
      "type": "top.zsmile.spring.entity.Student",
      "sourceType": "top.zsmile.spring.prop.SpringBootPropertiseConfiguration",
      "description": "class"
    },
    {
      "name": "zsmile.springboot.student.id",
      "type": "java.lang.Integer",
      "sourceType": "top.zsmile.spring.entity.Klass",
      "description": "class.id"
    },
    {
      "name": "zsmile.springboot.student.name",
      "type": "java.lang.String",
      "sourceType": "top.zsmile.spring.entity.Klass",
      "description": "class.name"
    }
  ]
}
```



## autoConfig

```java
package top.zsmile.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import top.zsmile.spring.entity.Klass;
import top.zsmile.spring.entity.School;
import top.zsmile.spring.entity.Student;
import top.zsmile.spring.prop.SpringBootPropertiseConfiguration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(SpringBootPropertiseConfiguration.class)
@ConditionalOnProperty(prefix = "zsmile.springboot", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = {"top.zsmile.spring.entity", "top.zsmile.spring.prop"})
public class MyAutoConfiguration {

    @Autowired
    private SpringBootPropertiseConfiguration springBootPropertiseConfiguration;

    @Bean(name = "student100")
    public Student student() {
        Student student = springBootPropertiseConfiguration.getStudent();
        return student;
    }

    @Bean
    public Klass klass() {
        Klass klass = new Klass();
        List<Student> students = new ArrayList<>();
        students.add(springBootPropertiseConfiguration.getStudent());
        klass.setStudents(students);
        return klass;
    }

    @Bean
    public School school() {
        return new School();
    }
}
```



# 作业10

## jdbc（不带事务）

[源代码地址]（https://github.com.cnpmjs.org/smileluck/geek-study/tree/main/project/src/main/java/top/zsmile/sql/jdbc/JdbcTest）

```java
public class JdbcTest {

    public static void main(String[] args) {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/basis_all", "root", "root");
            createTable(connection);
            int id = insert(connection);
            select(connection);
            update(connection, id);
            select(connection);
            delete(connection, id);
            select(connection);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void createTable(Connection connection) throws SQLException {

        CallableStatement callableStatement = null;
        try {
            callableStatement = connection.prepareCall("CREATE TABLE IF NOT EXISTS `test_log`( `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                    "  `name` varchar(255) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`id`))");
            boolean execute = callableStatement.execute();
            System.out.println("创建数据库成功 " + execute);
        } finally {
            if (callableStatement != null) {
                callableStatement.close();
            }
        }
    }


    public static int insert(Connection connection) throws SQLException {
        int num = -1;
        CallableStatement callableStatement = null;
        try {
            callableStatement = connection.prepareCall("insert into test_log(name) value(?)");
            callableStatement.setString(1, "name" + new Random().nextInt(1000));

            callableStatement.executeUpdate();

            ResultSet generatedKeys = callableStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                num = generatedKeys.getInt(1);
                System.out.println("insert Data, update column id " + num);
            }
            return num;
        } finally {
            if (callableStatement != null) {
                callableStatement.close();
            }
        }
    }

    public static void update(Connection connection, int id) throws SQLException {

        CallableStatement callableStatement = null;
        try {
            callableStatement = connection.prepareCall("update test_log set name=? where id = ?");
            callableStatement.setString(1, "update-name" + new Random().nextInt());
            callableStatement.setInt(2, id);

            callableStatement.executeUpdate();
            System.out.println("update Data, id = " + id);
        } finally {
            if (callableStatement != null) {
                callableStatement.close();
            }
        }
    }


    public static void delete(Connection connection, int id) throws SQLException {

        CallableStatement callableStatement = null;
        try {
            callableStatement = connection.prepareCall("delete from test_log where id = ?");
            callableStatement.setInt(1, id);

            callableStatement.executeUpdate();
            System.out.println("delete Data, id = " + id);
        } finally {
            if (callableStatement != null) {
                callableStatement.close();
            }
        }
    }

    public static void select(Connection connection) throws SQLException {
        CallableStatement callableStatement = null;
        ResultSet resultSet = null;
        try {
            callableStatement = connection.prepareCall("select * from test_log");
            resultSet = callableStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                System.out.println("id:" + id + "," + "name:" + name);
            }
            callableStatement.close();
        } finally {
            if (callableStatement != null) {
                callableStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }


}
```

## jdbc （带事务）

[源代码地址]（https://github.com.cnpmjs.org/smileluck/geek-study/tree/main/project/src/main/java/top/zsmile/sql/jdbc/JdbcTest2）

```java
package top.zsmile.sql.jdbc;

import java.sql.*;
import java.util.Random;

public class JdbcTest2 {

    public static void main(String[] args) {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/basis_all", "root", "root");
            connection.setAutoCommit(false);

            createTable(connection);

            insert2Column(connection);
            select(connection);

            insert2ColumnRoll(connection);
            select(connection);

            int id = insert(connection);
            select(connection);

            if (id != -1) {
                update(connection, id);
                select(connection);
                delete(connection, id);
                select(connection);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {

            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void createTable(Connection connection) {

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `test_log`( `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                    "  `name` varchar(255) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`id`))");

            preparedStatement.execute();
            connection.commit();
            System.out.println("创建数据库成功 ");
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void insert2Column(Connection connection) throws SQLException {
        PreparedStatement prepareStatement = null;
        PreparedStatement prepareStatement1 = null;
        try {
            prepareStatement = connection.prepareStatement("insert into test_log(name) value(?)");
            prepareStatement.setString(1, "name" + new Random().nextInt(1000));
            prepareStatement.executeUpdate();

            prepareStatement1 = connection.prepareStatement("insert into test_log(name) value(?)");
            prepareStatement1.setString(1, "name" + new Random().nextInt(1000));
            prepareStatement1.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            if (prepareStatement != null) {
                try {
                    prepareStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void insert2ColumnRoll(Connection connection) throws SQLException {
        PreparedStatement prepareStatement = null;
        PreparedStatement prepareStatement1 = null;
        try {
            prepareStatement = connection.prepareStatement("insert into test_log(name) value(?)", Statement.RETURN_GENERATED_KEYS);
            prepareStatement.setString(1, "name" + new Random().nextInt(1000));
            prepareStatement.executeUpdate();

            ResultSet generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                System.out.println("insert Data, will roll column id " + generatedKeys.getInt(1));
            }

            if (true) {
                System.out.println("手动异常");
                throw new SQLException("error");
            }
            prepareStatement1 = connection.prepareStatement("insert into test_log(name) value(?)");
            prepareStatement1.setString(1, "name" + new Random().nextInt(1000));
            prepareStatement1.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            if (prepareStatement != null) {
                try {
                    prepareStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int insert(Connection connection) throws SQLException {
        int num = -1;
        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = connection.prepareStatement("insert into test_log(name) value(?)", Statement.RETURN_GENERATED_KEYS);
            prepareStatement.setString(1, "name" + new Random().nextInt(1000));

            prepareStatement.executeUpdate();
            connection.commit();
            ResultSet generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                num = generatedKeys.getInt(1);
                System.out.println("insert Data, update column id " + num);
            }
            return num;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return num;
        } finally {
            if (prepareStatement != null) {
                try {
                    prepareStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void update(Connection connection, int id) throws SQLException {
        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = connection.prepareStatement("update test_log set name=? where id = ?");
            prepareStatement.setString(1, "update-name" + new Random().nextInt());
            prepareStatement.setInt(2, id);

            prepareStatement.executeUpdate();
            connection.commit();
            System.out.println("update Data, id = " + id);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            if (prepareStatement != null) {
                try {
                    prepareStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void delete(Connection connection, int id) throws SQLException {

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("delete from test_log where id = ?");
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
            connection.commit();
            System.out.println("delete Data, id = " + id);

        } catch (
                SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void select(Connection connection) throws SQLException {
        CallableStatement callableStatement = null;
        ResultSet resultSet = null;
        try {
            callableStatement = connection.prepareCall("select * from test_log");
            resultSet = callableStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                System.out.println("id:" + id + "," + "name:" + name);
            }
            callableStatement.close();
        } finally {
            if (callableStatement != null) {
                callableStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }


}
```



## hikari

[源代码地址]（https://github.com.cnpmjs.org/smileluck/geek-study/tree/main/project/src/main/java/top/zsmile/sql/hikari/HikariTest）

```java
package top.zsmile.sql.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.Random;

public class HikariTest {

    public static void main(String[] args) {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/basis_all");
            hikariConfig.setUsername("root");
            hikariConfig.setPassword("root");

            HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);

            connection = hikariDataSource.getConnection();

            createTable(connection);
            int id = insert(connection);
            select(connection);
            update(connection, id);
            select(connection);
            delete(connection, id);
            select(connection);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void createTable(Connection connection) throws SQLException {

        CallableStatement callableStatement = null;
        try {
            callableStatement = connection.prepareCall("CREATE TABLE IF NOT EXISTS `test_log`( `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                    "  `name` varchar(255) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`id`))");
            boolean execute = callableStatement.execute();
            System.out.println("创建数据库成功 " + execute);
        } finally {
            if (callableStatement != null) {
                callableStatement.close();
            }
        }
    }


    public static int insert(Connection connection) throws SQLException {
        int num = -1;
        CallableStatement callableStatement = null;
        try {
            callableStatement = connection.prepareCall("insert into test_log(name) value(?)");
            callableStatement.setString(1, "name" + new Random().nextInt(1000));

            callableStatement.executeUpdate();

            ResultSet generatedKeys = callableStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                num = generatedKeys.getInt(1);
                System.out.println("insert Data, update column id " + num);
            }
            return num;
        } finally {
            if (callableStatement != null) {
                callableStatement.close();
            }
        }
    }

    public static void update(Connection connection, int id) throws SQLException {

        CallableStatement callableStatement = null;
        try {
            callableStatement = connection.prepareCall("update test_log set name=? where id = ?");
            callableStatement.setString(1, "update-name" + new Random().nextInt());
            callableStatement.setInt(2, id);

            callableStatement.executeUpdate();
            System.out.println("update Data, id = " + id);
        } finally {
            if (callableStatement != null) {
                callableStatement.close();
            }
        }
    }


    public static void delete(Connection connection, int id) throws SQLException {

        CallableStatement callableStatement = null;
        try {
            callableStatement = connection.prepareCall("delete from test_log where id = ?");
            callableStatement.setInt(1, id);

            callableStatement.executeUpdate();
            System.out.println("delete Data, id = " + id);
        } finally {
            if (callableStatement != null) {
                callableStatement.close();
            }
        }
    }

    public static void select(Connection connection) throws SQLException {
        CallableStatement callableStatement = null;
        ResultSet resultSet = null;
        try {
            callableStatement = connection.prepareCall("select * from test_log");
            resultSet = callableStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                System.out.println("id:" + id + "," + "name:" + name);
            }
            callableStatement.close();
        } finally {
            if (callableStatement != null) {
                callableStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }
}
```