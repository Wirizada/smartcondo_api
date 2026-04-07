package br.com.wirizada.smartcondo_api.faturamento.application;

import br.com.wirizada.smartcondo_api.core.config.TenantContext;
import br.com.wirizada.smartcondo_api.faturamento.application.gateways.FaturaDTO;
import br.com.wirizada.smartcondo_api.faturamento.application.gateways.FaturaGateway;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListarFaturaUseCase {

    private final FaturaGateway faturaGateway;

    public ListarFaturaUseCase(FaturaGateway faturaGateway) {
        this.faturaGateway = faturaGateway;
    }

    public List<FaturaDTO> executar() {
        UUID tenantId = UUID.fromString(TenantContext.getCurrentTenant());
        return faturaGateway.buscarFaturasPorCondominio(tenantId);
    }
}
