package br.com.wirizada.smartcondo_api.faturamento.infrastructure.gateways;

import br.com.wirizada.smartcondo_api.faturamento.application.gateways.FaturaGateway;
import br.com.wirizada.smartcondo_api.faturamento.application.gateways.FaturaDTO;
import br.com.wirizada.smartcondo_api.faturamento.domain.Fatura;
import br.com.wirizada.smartcondo_api.faturamento.infrastructure.FaturaRepository;
import br.com.wirizada.smartcondo_api.faturamento.infrastructure.dto.FaturaProjection;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class FaturaRepositoryGateway implements FaturaGateway {

    private final FaturaRepository faturaRepository;

    public FaturaRepositoryGateway(FaturaRepository faturaRepository) {
        this.faturaRepository = faturaRepository;
    }

    @Override
    public void salvarTodasFaturas(List<Fatura> faturas) {
        faturaRepository.saveAll(faturas);
    }

    @Override
    public List<FaturaDTO> buscarFaturasPorCondominio(UUID tenantId) {

        List<FaturaProjection> projection = faturaRepository.listarFaturasPorCondominio(tenantId);

        return projection.stream()
                .map(p -> new FaturaDTO(
                        p.getId(),
                        p.getValor(),
                        p.getDataVencimento(),
                        p.getStatus(),
                        p.getApartamentoNumero()
                ))
                .toList();
    }


}
