package ru.kalkin.bksassignment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Allocation {
    private String sector;
    private BigDecimal assetValue;
    private BigDecimal proportion;
}
