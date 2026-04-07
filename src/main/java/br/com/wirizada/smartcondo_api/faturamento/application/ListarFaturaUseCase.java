package br.com.wirizada.smartcondo_api.faturamento.application;

import br.com.wirizada.smartcondo_api.core.config.TenantContext;
import br.com.wirizada.smartcondo_api.faturamento.infrastructure.FaturaRepository;
import br.com.wirizada.smartcondo_api.faturamento.infrastructure.dto.FaturaProjection;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListarFaturaUseCase {

    private final FaturaRepository faturaRepository;

    public ListarFaturaUseCase(FaturaRepository faturaRepository) {
        this.faturaRepository = faturaRepository;
    }

    public List<FaturaProjection> executar() {
        UUID tenantId = UUID.fromString(TenantContext.getCurrentTenant());
        return faturaRepository.listarFaturasPorCondominio(tenantId);
    }
}
