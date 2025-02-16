package com.example.demo.dto;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class InvestmentDetails {
    private String name;
    private String minInvestment;
    private String minPeriod;
    private String assuredReturn;
    private String bonus;

}
