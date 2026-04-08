package br.com.wirizada.smartcondo_api.condominio.api.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CNPJ;

public record CadastrarCondominioRequest(
        @NotBlank(message = "O nome do condomínio é obrigatório")
        String nome,

        @NotBlank(message = "O CNPJ do condomínio é obrigatório")
        @CNPJ(message = "O CNPJ do condomínio é inválido")
        String cnpj
) {}

