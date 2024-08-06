package com.ems.application.controller.notification;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.application.dto.notification.NotificationDeleteRequest;
import com.ems.application.dto.notification.NotificationResponse;
import com.ems.application.dto.notification.NotificationSearchRequest;
import com.ems.application.dto.notification.NotificationUpdateRequest;
import com.ems.application.service.notification.NotificationService;

import io.swagger.annotations.Api;

@Api(tags = "Notification")

@RestController
@RequestMapping(value = "/api/notification")
public class NotificationController {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationController(NotificationService notificationService, SimpMessagingTemplate messagingTemplate) {
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/send/notification")
    public void sendMessage(@Payload String message) {
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }

    @PostMapping(value = "/list")
    public ResponseEntity<Page<NotificationResponse>> getAllNotificationForUser(
            @Valid @RequestBody NotificationSearchRequest request) {
        return notificationService.getAllNotificationForUser(request);
    }

    @PostMapping(value = "/read")
    public ResponseEntity<Page<NotificationResponse>> realNotification(
            @Valid @RequestBody NotificationUpdateRequest request) {
        return notificationService.readNotification(request);
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<Page<NotificationResponse>> deleteNotification(
            @Valid @RequestBody NotificationDeleteRequest request) {
        return notificationService.deleteNotification(request);
    }
}
