package com.itmo.ncfs.integrations.notification;

import com.itmo.ncfs.dto.FeedbackDto;
import com.itmo.ncfs.integrations.notification.dto.NotificationContext;
import com.itmo.ncfs.integrations.notification.dto.NotificationRequest;
import com.itmo.ncfs.integrations.shopping.dto.GetCustomerResponse;
import com.itmo.ncfs.integrations.shopping.dto.GetProductResponse;
import com.itmo.ncfs.services.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class NotificationRestClient {

    private final ConfigService configService;

    @Autowired
    public NotificationRestClient(ConfigService configService) {
        this.configService = configService;
    }

    private static final String SEND_NOTIFICATION_URL = "/api/v1/notification-engine/";
    private static final String NOTIFICATION_TEMPLATE_NAME = "feedbackStatusChangeNotification";

    public void sendNotification(FeedbackDto feedback, GetCustomerResponse customer, GetProductResponse product) {
        NotificationRequest request = buildNotificationRequset(feedback, customer, product);
        RestTemplate restTemplate = new RestTemplate();
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(configService.getGatewayAddress())
                .path(SEND_NOTIFICATION_URL)
                .build();
        try {
            log.info("sending request to notification service: \n" + request);
            restTemplate.postForEntity(uriComponents.toUriString(), request, String.class);
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
        }
    }

    private NotificationRequest buildNotificationRequset(FeedbackDto feedback, GetCustomerResponse customer,
                                                         GetProductResponse product) {
        NotificationRequest request = new NotificationRequest();
        NotificationContext context = new NotificationContext();
        context.setCustomerName(customer.getName());
        context.setDeclineMessage(feedback.getDeclineMessage());
        context.setStatus(feedback.getStatus());
        context.setProductName(product.getName());
        request.setContext(context);
        request.setTemplateName(NOTIFICATION_TEMPLATE_NAME);
        request.setTo(customer.getEmail());
        return request;
    }
}
