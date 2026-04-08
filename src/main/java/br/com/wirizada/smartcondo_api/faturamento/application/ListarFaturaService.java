package br.com.wirizada.smartcondo_api.faturamento.application;

import br.com.wirizada.smartcondo_api.condominio.application.CondominioFacade;
import br.com.wirizada.smartcondo_api.faturamento.application.dto.FaturaResumoDTO;
import br.com.wirizada.smartcondo_api.faturamento.infra.FaturaRepository;
import br.com.wirizada.smartcondo_api.faturamento.model.Fatura;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
public class ListarFaturaService {

    private final FaturaRepository faturaRepository;
    private final CondominioFacade condominioFacade;

    public ListarFaturaService(FaturaRepository faturaRepository, CondominioFacade condominioFacade) {
        this.faturaRepository = faturaRepository;
        this.condominioFacade = condominioFacade;
    }

    public List<FaturaResumoDTO> executar() {
        List<Fatura> faturas = faturaRepository.findAll();

        List<UUID> unidadeIds = faturas.stream()
                .map(Fatura::getUnidadeId)
                .distinct()
                .toList();

        Map<UUID, String> numerosUnidades = condominioFacade.buscarNumerosUnidades(unidadeIds);

        return faturas.stream()
                .map(f -> new FaturaResumoDTO(
                        f.getId(),
                        f.getValor(),
                        f.getDataVencimento(),
                        f.getStatus().name(),
                        numerosUnidades.getOrDefault(f.getUnidadeId(), "—")
                ))
                .toList();
    }
}

