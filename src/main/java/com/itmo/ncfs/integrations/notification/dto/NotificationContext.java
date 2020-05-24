package com.itmo.ncfs.integrations.notification.dto;

import com.itmo.ncfs.enums.FeedbackStatus;
import lombok.Data;

@Data
public class NotificationContext {

    private String customerName;

    private String declineMessage;

    private String productName;

    private FeedbackStatus status;
}
