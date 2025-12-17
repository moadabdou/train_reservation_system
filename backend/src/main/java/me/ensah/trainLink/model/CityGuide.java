package me.ensah.trainLink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "city_guides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CityGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cityName;

    @Column(columnDefinition = "TEXT") // Store longer content
    private String content; // Markdown or JSON content for tips/guides

    private String weatherApiId; // Optional reference for weather API
}
