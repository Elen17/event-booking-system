package com.epam.campstone.eventbookingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryOptionDto {
    private Long id;
    private String value;
    private String displayName;
}
