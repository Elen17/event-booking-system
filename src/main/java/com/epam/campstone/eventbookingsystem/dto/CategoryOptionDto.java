package com.epam.campstone.eventbookingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryOptionDto {
    private final Long id;
    private final String value;
    private final String displayName;
}
