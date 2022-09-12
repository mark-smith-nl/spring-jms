package nl.smith.springmdb.configuration;

import lombok.extern.slf4j.Slf4j;
import nl.smith.springmdb.domain.OrderTransaction;
import nl.smith.springmdb.domain.Person;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyListener {

    @JmsListener(destination = "DummyQueue", containerFactory = "myFactory")
    public void receiveOrderTransactionMessage(OrderTransaction transaction) {
        log.info("Received {}", transaction);
    }

    @JmsListener(destination = "DummyQueue", containerFactory = "myFactory")
    public void receivePersonMessage(Person person) {
        log.info("Received {}", person);
    }
}
