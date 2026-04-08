package br.com.wirizada.smartcondo_api.condominio.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record UnidadeParaFaturamentoDTO(
        UUID idUnidade,
        String nomeSacado,
        BigDecimal fracaoIdeal
) {}

