package com.ems.application.service.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ems.application.dto.notification.NotificationDeleteRequest;
import com.ems.application.dto.notification.NotificationResponse;
import com.ems.application.dto.notification.NotificationSearchRequest;
import com.ems.application.dto.notification.NotificationUpdateRequest;
import com.ems.application.entity.Notification;
import com.ems.application.enums.NotificationStatus;
import com.ems.application.mapping.notification.NotificationMapper;
import com.ems.application.repository.NotificationRepository;
import com.ems.application.repository.specification.NotificationSpecification;
import com.ems.application.service.BaseService;

@Service
public class NotificationService extends BaseService {

    private final NotificationRepository notificationRepository;

    // Construct a new NotificationService with the provided NotificationRepository
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Retrieves a page of notifications for a user based on the provided search
    // criteria
    public ResponseEntity<Page<NotificationResponse>> getAllNotificationForUser(
            NotificationSearchRequest request) {

        Page<Notification> notificationPage = getNotificationList(request.getPageIndex() - 1,
                request.getPageSize(), request.isDescending(), request.getSortBy());

        return ResponseEntity.ok()
                .body(notificationPage.map(NotificationMapper.INSTANCE::toDto));
    }

    // Marks notifications as read based on the provided update request
    public ResponseEntity<Page<NotificationResponse>> readNotification(
            NotificationUpdateRequest request) {
        if (CollectionUtils.isEmpty(request.getNotificationIds())) {
            notificationRepository.updateIsReadForAllNotifications(getUser().getId(),
                    NotificationStatus.READ.getValue());
        } else {
            notificationRepository.updateIsReadNotification(getUser().getId(),
                    request.getNotificationIds(), NotificationStatus.READ.getValue());
        }

        Page<Notification> notificationPage = getNotificationList(request.getPageIndex() - 1,
                request.getPageSize(), request.isDescending(), request.getSortBy());

        return ResponseEntity.ok()
                .body(notificationPage.map(NotificationMapper.INSTANCE::toDto));
    }

    // Deletes notifications based on the provided delete request
    public ResponseEntity<Page<NotificationResponse>> deleteNotification(
            NotificationDeleteRequest request) {
        notificationRepository.deleteByUserAndNotificationIds(getUser().getId(),
                request.getNotificationIds());

        Page<Notification> notificationPage = getNotificationList(request.getPageIndex() - 1,
                request.getPageSize(), request.isDescending(), request.getSortBy());

        return ResponseEntity.ok()
                .body(notificationPage.map(NotificationMapper.INSTANCE::toDto));
    }

    // Retrieves a page of notifications for the current user based on the provided
    // pagination parameters
    private Page<Notification> getNotificationList(int pageNumber, int recordNumber,
            boolean isDescending, String sortBy) {
        Pageable pageable = createPageRequest(pageNumber, recordNumber,
                Sort.by(
                        isDescending ? Sort.Direction.DESC : Sort.Direction.ASC,
                        sortBy));

        return notificationRepository.findAll(NotificationSpecification.getByUserId(getUser().getId()),
                pageable);
    }
}
