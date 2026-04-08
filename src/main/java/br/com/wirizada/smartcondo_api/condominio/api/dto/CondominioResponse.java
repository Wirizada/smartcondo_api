package br.com.wirizada.smartcondo_api.condominio.api.dto;

import java.util.UUID;

public record CondominioResponse(
        UUID id,
        String nome,
        String cnpj
) {}

