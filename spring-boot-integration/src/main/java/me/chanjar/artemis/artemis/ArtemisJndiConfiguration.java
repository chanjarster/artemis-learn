package me.chanjar.artemis.artemis;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.IllegalStateException;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.Context;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by qianjia on 16/8/20.
 */
@Configuration
@EnableConfigurationProperties(ArtemisJndiConfiguration.JndiProperties.class)
public class ArtemisJndiConfiguration implements ApplicationContextAware, InitializingBean {

  @Autowired
  private JndiProperties jndiProperties;

  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void afterPropertiesSet() throws Exception {

    AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
    if (!SingletonBeanRegistry.class.isAssignableFrom(beanFactory.getClass())) {
      throw new IllegalStateException("ApplicationContext: " + applicationContext.getClass().toString() + " doesn't implements SingletonBeanRegistry, cannot register Artemis JMS object at runtime");
    }

    JndiTemplate jndiTemplate = createJndiTemplate();

    SingletonBeanRegistry beanDefinitionRegistry = (SingletonBeanRegistry) beanFactory;

    Map<String, String> properties = jndiProperties.getJndi();
    for (String key : properties.keySet()) {

      if (key.startsWith(JndiProperties.CONNECTION_FACTORY_PREFIX)) {

        String jndiName = key.replace(JndiProperties.CONNECTION_FACTORY_PREFIX, "");
        ConnectionFactory connectionFactory = jndiTemplate.lookup(jndiName, ConnectionFactory.class);
        beanDefinitionRegistry.registerSingleton(jndiName, connectionFactory);

        continue;

      }
      if (key.startsWith(JndiProperties.QUEUE_PREFIX)) {

        String jndiName = key.replace(JndiProperties.QUEUE_PREFIX, "");
        Queue queue = jndiTemplate.lookup(jndiName, Queue.class);
        beanDefinitionRegistry.registerSingleton(jndiName, queue);

        continue;

      }
      if (key.startsWith(JndiProperties.TOPIC_PREFIX)) {

        String jndiName = key.replace(JndiProperties.TOPIC_PREFIX, "");
        Topic topic = jndiTemplate.lookup(jndiName, Topic.class);
        beanDefinitionRegistry.registerSingleton(jndiName, topic);

        continue;

      }

    }

  }

  private JndiTemplate createJndiTemplate() {
    Map<String, String> properties = jndiProperties.getJndi();

    Properties env = new Properties();

    for (String key : properties.keySet()) {

      if (key.equals(Context.INITIAL_CONTEXT_FACTORY)) {
        env.put(key, properties.get(key));
        continue;
      }
      if (key.startsWith(JndiProperties.CONNECTION_FACTORY_PREFIX)) {
        env.put(key, properties.get(key));
        continue;
      }
      if (key.startsWith(JndiProperties.QUEUE_PREFIX)) {
        env.put(key, properties.get(key));
        continue;
      }
      if (key.startsWith(JndiProperties.TOPIC_PREFIX)) {
        env.put(key, properties.get(key));
        continue;
      }

    }
    return new JndiTemplate(env);

  }

  @ConfigurationProperties(prefix = "artemis")
  public class JndiProperties {

    public static final String CONNECTION_FACTORY_PREFIX = "connectionFactory.";
    public static final String QUEUE_PREFIX = "queue.";
    public static final String TOPIC_PREFIX = "topic.";

    private final Map<String, String> jndi = new HashMap<>();

    public Map<String, String> getJndi() {
      return jndi;
    }

  }

}
