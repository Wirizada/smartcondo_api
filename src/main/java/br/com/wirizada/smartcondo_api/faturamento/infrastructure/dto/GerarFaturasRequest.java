package br.com.wirizada.smartcondo_api.faturamento.infrastructure.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GerarFaturasRequest(
        BigDecimal valorBase,
        LocalDate dataVencimento
) {
}
