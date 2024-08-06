package com.ems.application.dto.notification;

import java.util.List;

import com.ems.application.dto.base.PaginationRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationUpdateRequest extends PaginationRequest {

    private List<Integer> notificationIds;
}
