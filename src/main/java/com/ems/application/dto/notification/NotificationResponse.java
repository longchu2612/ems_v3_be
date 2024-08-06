package com.ems.application.dto.notification;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationResponse {

    private Integer notificationId;
    private String title;
    private String contentTemplate;
    private String contentValue;
    private Integer notificationType;
    private Integer isRead;
    private LocalDateTime createdAt;
}
