package br.com.wirizada.smartcondo_api.faturamento.application;

import br.com.wirizada.smartcondo_api.core.config.TenantContext;
import br.com.wirizada.smartcondo_api.faturamento.infrastructure.FaturaRepository;
import br.com.wirizada.smartcondo_api.faturamento.infrastructure.dto.ListarFaturaProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListarFaturasUseCase {

    private final FaturaRepository faturaRepository;

    public ListarFaturasUseCase(FaturaRepository faturaRepository) {
        this.faturaRepository = faturaRepository;
    }

    public List<ListarFaturaProjection> listarFaturas() {
        UUID tenantId = UUID.fromString(TenantContext.getCurrentTenant());
        return faturaRepository.listarFaturasPorCondominio(tenantId);
    }
}
