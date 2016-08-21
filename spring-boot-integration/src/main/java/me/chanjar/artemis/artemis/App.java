package me.chanjar.artemis.artemis;

import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

/**
 * Hello world!
 */
@SpringBootApplication(exclude = { ArtemisAutoConfiguration.class })
public class App {

  public static void main(String[] args) {

    ApplicationContext ctx = SpringApplication.run(App.class, args);

    System.out.println();
    System.out.println("===================");
    System.out.println("Bean definition list");
    System.out.println("===================");
    String[] beanNames = ctx.getBeanDefinitionNames();
    Arrays.sort(beanNames);
    for (String beanName : beanNames) {
      Object bean = ctx.getBean(beanName);
      System.out.println(beanName + " : " + bean.toString());
    }

    System.out.println();
    System.out.println("===================");
    System.out.println("Singleton bean list");
    System.out.println("===================");
    beanNames = ((SingletonBeanRegistry) ctx.getAutowireCapableBeanFactory()).getSingletonNames();
    Arrays.sort(beanNames);
    for (String beanName : beanNames) {
      Object bean = ctx.getBean(beanName);
      System.out.println(beanName + " : " + bean.toString());
    }
    System.out.println();

    JmsRunner jmsRunner = ctx.getBean(JmsRunner.class);
    jmsRunner.run();
  }

}
