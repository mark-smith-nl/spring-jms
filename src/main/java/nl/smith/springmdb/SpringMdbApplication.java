package nl.smith.springmdb;

import lombok.extern.slf4j.Slf4j;
import nl.smith.springmdb.configuration.XMLMessageConverter;
import nl.smith.springmdb.domain.OrderTransaction;
import nl.smith.springmdb.domain.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.jms.*;
import java.math.BigDecimal;

@SpringBootApplication
@EnableJms
@EnableScheduling
@Slf4j
public class SpringMdbApplication {

    @Autowired
    private JmsTemplate jmsTemplate;

    public static void main(String[] args) throws JMSException {

        ApplicationContext context = SpringApplication.run(SpringMdbApplication.class, args);
    //    JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);

     //   jmsTemplate.convertAndSend("DummyQueue", new OrderTransaction(new Person("Mark", "Smith"), new Person("Tom", "Smith"), BigDecimal.TEN));
     //   jmsTemplate.convertAndSend("DummyQueue", new Person("Mark", "Rutte"));
    }

    @Bean
    public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();

        // lambda function
        factory.setErrorHandler(t -> log.warn("An error has occurred in the transaction", t));

        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Scheduled(fixedDelay = 2000)
    public void sendMessage() {
      //  if ((int)(Math.random()*2) == 0) {
            jmsTemplate.convertAndSend("DummyQueue", new OrderTransaction(new Person("Mark", "Smith"), new Person("Tom", "Smith"), BigDecimal.TEN));
      //  }
      //  else {
            log.info("Skip");
    //        jmsTemplate.convertAndSend("DummyQueue", new Person("Mark", "Rutte"));
      //  }
    }


}
