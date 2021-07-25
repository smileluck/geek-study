package top.zsmile.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import top.zsmile.spring.beanload.annotation.AnnotationBean;
import top.zsmile.spring.beanload.xml.XmlBean;
import top.zsmile.spring.entity.School1;

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
