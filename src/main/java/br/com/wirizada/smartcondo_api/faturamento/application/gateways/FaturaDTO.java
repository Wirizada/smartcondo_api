package br.com.wirizada.smartcondo_api.faturamento.application.gateways;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FaturaDTO(
        UUID id,
        BigDecimal valor,
        LocalDate dataVencimento,
        String status,
        String numeroApartamento
){}
