package ru.practicum.main_service.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RequestStats {
    private Long eventId;
    private Long confirmedRequests;
}
