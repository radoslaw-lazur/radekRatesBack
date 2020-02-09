package com.radekrates.service.datafixerio.calculation;

import com.radekrates.api.datafixerio.client.DataFixerClient;
import com.radekrates.domain.dto.datafixerio.DataFixerDto;
import com.radekrates.domain.dto.datafixerio.RatesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class CurrrencyCalculator {
    @Autowired
    private DataFixerClient dataFixerClient;
    private final MathContext mathContext = new MathContext(4, RoundingMode.CEILING);

    public CurrencyBase createLiveCurrencyBase(String currency) {
        DataFixerDto dataFixerDto = getDataFixerData();
        RatesDto ratesDto = getDataFixerData().getRatesDto();
        switch (currency) {
            case "EUR":
                return new CurrencyBase(
                        dataFixerDto.getCurrencyBased(),
                        dataFixerDto.getDate(),
                        ratesDto.getEur(),
                        ratesDto.getPln().round(mathContext),
                        ratesDto.getGbp().round(mathContext),
                        ratesDto.getChf().round(mathContext),
                        ratesDto.getUsd().round(mathContext)
                );
            case "PLN":
                BigDecimal eurToPln = BigDecimal.ONE.divide(ratesDto.getPln(), 4, RoundingMode.CEILING);
                return new CurrencyBase(
                        "PLN",
                        dataFixerDto.getDate(),
                        eurToPln,
                        BigDecimal.ONE,
                        eurToPln.multiply(ratesDto.getGbp()).round(mathContext),
                        eurToPln.multiply(ratesDto.getChf()).round(mathContext),
                        eurToPln.multiply(ratesDto.getUsd()).round(mathContext)
                );
            case "GBP":
                BigDecimal eurToGbp = BigDecimal.ONE.divide(ratesDto.getGbp(), 4, RoundingMode.CEILING);
                return new CurrencyBase(
                        "GBP",
                        dataFixerDto.getDate(),
                        eurToGbp,
                        eurToGbp.multiply(ratesDto.getPln()).round(mathContext),
                        BigDecimal.ONE,
                        eurToGbp.multiply(ratesDto.getChf()).round(mathContext),
                        eurToGbp.multiply(ratesDto.getUsd()).round(mathContext)
                );
            case "CHF":
                BigDecimal eurToChf = BigDecimal.ONE.divide(ratesDto.getChf(), 4, RoundingMode.CEILING);
                return new CurrencyBase(
                        "CHF",
                        dataFixerDto.getDate(),
                        eurToChf,
                        eurToChf.multiply(ratesDto.getPln()).round(mathContext),
                        eurToChf.multiply(ratesDto.getGbp()).round(mathContext),
                        BigDecimal.ONE,
                        eurToChf.multiply(ratesDto.getUsd()).round(mathContext)
                );
            case "USD":
                BigDecimal eurToUsd = BigDecimal.ONE.divide(ratesDto.getUsd(), 4, RoundingMode.CEILING);
                return new CurrencyBase(
                        "USD",
                        dataFixerDto.getDate(),
                        eurToUsd,
                        eurToUsd.multiply(ratesDto.getPln()).round(mathContext),
                        eurToUsd.multiply(ratesDto.getGbp()).round(new MathContext(4, RoundingMode.CEILING)),
                        eurToUsd.multiply(ratesDto.getChf()).round(mathContext),
                        BigDecimal.ONE
                );
            default:
                return null;
        }
    }

    private DataFixerDto getDataFixerData() {
        return dataFixerClient.getDataFixerData();
    }
}
