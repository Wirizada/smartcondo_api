package br.com.wirizada.smartcondo_api.faturamento.application.gateways;

import br.com.wirizada.smartcondo_api.faturamento.domain.Fatura;

import java.util.List;
import java.util.UUID;

public interface FaturaGateway {
    void salvarTodasFaturas(List<Fatura> faturas);

    List<FaturaDTO> buscarFaturasPorCondominio(UUID tenantId);
}
