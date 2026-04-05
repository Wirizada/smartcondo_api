package br.com.wirizada.smartcondo_api.condominio.infrastructure.dto;

import java.util.UUID;

public record CondominioResponse(
        UUID id,
        String nome,
        String cnpj
){
}
