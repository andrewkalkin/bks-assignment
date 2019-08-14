package ru.kalkin.bksassignment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Assets {
    private BigDecimal value;
    private List<Allocation> allocations;
}
