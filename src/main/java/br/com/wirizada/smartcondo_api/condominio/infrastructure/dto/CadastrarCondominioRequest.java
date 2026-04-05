package br.com.wirizada.smartcondo_api.condominio.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CNPJ;


public record CadastrarCondominioRequest(
        @NotBlank(message = "O nome do condominio é obrigatório")
        String nome,

        @NotBlank(message = "O CNPJ do condominio é obrigatório")
        @CNPJ(message = "O CNPJ do condominio é inválido")
        String cnpj
){
}
