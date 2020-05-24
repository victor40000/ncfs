package com.itmo.ncfs.integrations.notification.dto;

import lombok.Data;

@Data
public class NotificationRequest {

    private String templateName;

    private String to;

    private NotificationContext context;
}
