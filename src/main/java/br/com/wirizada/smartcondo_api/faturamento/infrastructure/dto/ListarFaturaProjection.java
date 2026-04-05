package br.com.wirizada.smartcondo_api.faturamento.infrastructure.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface ListarFaturaProjection {
    UUID getId();
    BigDecimal getValor();
    LocalDate getDataVencimento();
    String getStatus();
    String getApartamentoNumero();
}
