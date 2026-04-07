package br.com.wirizada.smartcondo_api.condominio.application.gateways;

import br.com.wirizada.smartcondo_api.condominio.domain.Condominio;

public interface CondominioGateway {
    Condominio salvarCondominio(Condominio condominio);
}
