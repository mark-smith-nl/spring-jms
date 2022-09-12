package nl.smith.springmdb.configuration;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class XMLMessageConverter implements MessageConverter {

    private static final String CLASS_NAME = "Class";

    private final Map<Class<?>, Marshaller> marshallers = new HashMap<>();

    private final Map<Class<?>, Unmarshaller> unmarshallers = new HashMap<>();

    @SneakyThrows
    private Marshaller getMarshallerForClass(Class<?> clazz) {
        marshallers.putIfAbsent(clazz, JAXBContext.newInstance(clazz).createMarshaller());
        Marshaller marshaller = marshallers.get(clazz);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        return marshaller;
    }

    @SneakyThrows
    private Unmarshaller getUnmarshallerForClass(Class<?> clazz) {
        unmarshallers.putIfAbsent(clazz, JAXBContext.newInstance(clazz).createUnmarshaller());
        return unmarshallers.get(clazz);
    }

    @Override
    public Message toMessage(@NonNull Object object, Session session) throws JMSException, MessageConversionException {
        try {
            Marshaller marshaller = getMarshallerForClass(object.getClass());
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(object, stringWriter);
            TextMessage message = session.createTextMessage();
            log.info("Created message\n{}", stringWriter);
            message.setText(stringWriter.toString());
            message.setStringProperty(CLASS_NAME, object.getClass().getCanonicalName());
            return message;
        } catch (JAXBException e) {
            throw new MessageConversionException(e.getMessage());
        }
    }

    @Override
    public Object fromMessage(@NonNull Message message) throws JMSException, MessageConversionException {
        TextMessage textMessage = (TextMessage) message;
        String payload = textMessage.getText();
        String className = textMessage.getStringProperty(CLASS_NAME);
        log.info("Converting message with id {} and {}={}into java object.", message.getJMSMessageID(), CLASS_NAME, className);
        try {
            Class<?> clazz = Class.forName(className);
            Unmarshaller unmarshaller = getUnmarshallerForClass(clazz);
            return unmarshaller.unmarshal(new StringReader(payload));
        } catch (JAXBException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
