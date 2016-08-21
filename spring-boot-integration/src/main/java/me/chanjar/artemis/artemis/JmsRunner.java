package me.chanjar.artemis.artemis;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by qianjia on 16/8/21.
 */
@Component
public class JmsRunner implements InitializingBean {

  private final AtomicInteger count = new AtomicInteger(100);

  @Autowired
  @Qualifier("jms.connectionFactory")
  private ConnectionFactory connectionFactory;

  @Autowired
  @Qualifier("jms.exampleQueue1")
  private Queue exampleQueue1;

  @Autowired
  @Qualifier("jms.exampleQueue2")
  private Queue exampleQueue2;

  @Autowired
  @Qualifier("jms.exampleTopic1")
  private Topic exampleTopic1;

  @Autowired
  @Qualifier("jms.exampleTopic2")
  private Topic exampleTopic2;

  @Override
  public void afterPropertiesSet() throws Exception {
    System.out.println();
    System.out.println("===================");
    System.out.println("JMS Object Injection result");
    System.out.println("===================");
    System.out.println(connectionFactory);
    System.out.println(exampleQueue1);
    System.out.println(exampleQueue2);
    System.out.println(exampleTopic1);
    System.out.println(exampleTopic2);
  }


  public void run() {
    try (Connection connection = connectionFactory.createConnection()) {
      send(connection, exampleQueue1);
      receive(connection, exampleQueue1);
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

  public void send(Connection connection, Queue requestQueue) throws JMSException {

    try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

      MessageProducer producer = session.createProducer(requestQueue);
      int i = count.get();
      while (i != 0) {
        producer.send(session.createTextMessage("test"));
        i--;
      }

    }

  }

  public void receive(Connection connection, Queue requestQueue) {

    try {

      connection.start();

      try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

        MessageConsumer consumer = session.createConsumer(requestQueue);

        consumer.setMessageListener(message -> {
          try {
            count.decrementAndGet();
            System.out.println(count + ":" + ((TextMessage) message).getText());
          } catch (JMSException e) {
            e.printStackTrace();
          }
        });

        while (count.get() != 0) {

        }
      }

    } catch (JMSException e) {
      e.printStackTrace();
    }

  }

}
