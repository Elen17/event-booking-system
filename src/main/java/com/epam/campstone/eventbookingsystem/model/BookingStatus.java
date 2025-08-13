package com.epam.campstone.eventbookingsystem.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "booking_status")
@Getter
@Setter
@NoArgsConstructor
public class BookingStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false,
            unique = true, length = 50)
    private String name;

    @Column(name = "display_name", nullable = false,
            unique = true)
    private String displayName;


}
