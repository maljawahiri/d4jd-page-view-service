package guru.springframework.services;

import guru.springframework.domain.PageView;
import guru.springframework.model.events.PageViewEvent;
import guru.springframework.repositories.PageViewsRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import javax.xml.bind.JAXB;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PageViewEventMessageHandler implements MessageHandler {
    private PageViewsRepository repository;

    public PageViewEventMessageHandler(PageViewsRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {

        System.out.println("Got Message!");

        String xmlString = (String) message.getPayload();

        System.out.println(xmlString);

        InputStream is = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));

        PageViewEvent pageViewEvent =  JAXB.unmarshal(is, PageViewEvent.class);

        PageView pageView = new PageView();
        pageView.setPageUrl(pageViewEvent.getPageUrl());
        pageView.setPageViewDate(pageViewEvent.getPageViewDate());
        pageView.setCorrelationId(pageViewEvent.getCorrelationId());

        repository.save(pageView);
    }
}
