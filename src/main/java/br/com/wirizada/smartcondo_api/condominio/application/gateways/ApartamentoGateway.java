package br.com.wirizada.smartcondo_api.condominio.application.gateways;

import br.com.wirizada.smartcondo_api.condominio.domain.Apartamento;

import java.util.List;

public interface ApartamentoGateway {
    List<Apartamento> buscarTodosApartamentos();
}
