package org.milianz.inmomarketbackend.Domain.Entities.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTimesSaveDTO {
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer dayOfWeek;
}
