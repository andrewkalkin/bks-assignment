package ru.kalkin.bksassignment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class StockQuote {
    private String sector;
    private BigDecimal latestPrice;
    private BigDecimal value;
}
