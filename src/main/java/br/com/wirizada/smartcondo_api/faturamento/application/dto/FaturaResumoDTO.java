package br.com.wirizada.smartcondo_api.faturamento.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FaturaResumoDTO(
        UUID id,
        BigDecimal valor,
        LocalDate dataVencimento,
        String status,
        String numeroUnidade
) {}

