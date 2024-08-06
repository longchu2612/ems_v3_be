package com.ems.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ems.application.entity.Notification;

public interface NotificationRepository extends JpaRepositoryBase<Notification, Integer>,
        JpaSpecificationExecutor<Notification> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Notification n SET n.isRead = :status WHERE n.user.id = :userId AND n.notificationId IN (:notificationIds)")
    void updateIsReadNotification(@Param("userId") int userId,
            @Param("notificationIds") List<Integer> notificationIds,
            @Param("status") int status);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Notification n SET n.isRead = :status WHERE n.user.id = :userId")
    void updateIsReadForAllNotifications(@Param("userId") int userId, @Param("status") int status);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Notification n WHERE n.user.id = :userId AND n.notificationId IN (:notificationIds)")
    void deleteByUserAndNotificationIds(@Param("userId") int userId,
            @Param("notificationIds") List<Integer> notificationIds);
}
