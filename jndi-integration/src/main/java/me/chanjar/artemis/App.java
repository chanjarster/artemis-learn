package me.chanjar.artemis;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hello world!
 */
public class App {

  private final AtomicInteger count = new AtomicInteger(100);

  public static void main(String[] args) throws NamingException, JMSException {

    InitialContext initialContext = new InitialContext();
    ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");

    Queue queue = (Queue) initialContext.lookup("exampleQueue");
    App app = new App();

    try (Connection connection = connectionFactory.createConnection()) {
      app.send(connection, queue);
      app.receive(connection, queue);
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
