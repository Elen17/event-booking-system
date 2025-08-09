package com.epam.campstone.eventbookingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryOptionDto {

    private final String value;
    private final String displayName;
}
