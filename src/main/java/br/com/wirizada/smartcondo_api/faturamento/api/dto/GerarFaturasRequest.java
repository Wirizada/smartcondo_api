package br.com.wirizada.smartcondo_api.faturamento.api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record GerarFaturasRequest(
        @NotNull(message = "O valor base não pode ser nulo")
        @Positive(message = "O valor base deve ser positivo")
        BigDecimal valorBase,

        @NotNull(message = "A data de vencimento não pode ser nula")
        @FutureOrPresent(message = "A data de vencimento deve ser hoje ou no futuro")
        LocalDate dataVencimento,

        @NotNull(message = "A competência é obrigatória")
        UUID competenciaId
) {}

