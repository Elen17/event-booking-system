package com.epam.campstone.eventbookingsystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EventSearchDto {
    private String city;
    private LocalDate date;
    private String category;
}
