package me.ensah.trainLink.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainDTO {
    private Long id;
    private String name;
    private Long providerId;
    private String providerName;
    private Long trainLayoutId;
    private String trainLayoutName;
    private int totalSeats;
}
