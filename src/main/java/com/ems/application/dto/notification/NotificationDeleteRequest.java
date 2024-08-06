package com.ems.application.dto.notification;

import java.util.List;

import com.ems.application.dto.base.PaginationRequest;
import com.ems.application.validator.FieldRequired;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDeleteRequest extends PaginationRequest {

    @FieldRequired(name = "field.notification.list")
    private List<Integer> notificationIds;
}
