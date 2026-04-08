package br.com.wirizada.smartcondo_api.faturamento.application;

import br.com.wirizada.smartcondo_api.condominio.application.CondominioFacade;
import br.com.wirizada.smartcondo_api.condominio.application.dto.UnidadeParaFaturamentoDTO;
import br.com.wirizada.smartcondo_api.faturamento.infra.FaturaRepository;
import br.com.wirizada.smartcondo_api.faturamento.model.Fatura;
import br.com.wirizada.smartcondo_api.faturamento.model.StatusFatura;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GerarFaturaService {

    private final CondominioFacade condominioFacade;
    private final FaturaRepository faturaRepository;

    public GerarFaturaService(CondominioFacade condominioFacade, FaturaRepository faturaRepository) {
        this.condominioFacade = condominioFacade;
        this.faturaRepository = faturaRepository;
    }

    @Transactional
    public void executar(BigDecimal valorBase, LocalDate dataVencimento, UUID competenciaId) {

        List<UnidadeParaFaturamentoDTO> unidades = condominioFacade.listarUnidadesParaFaturamento();
        List<Fatura> novasFaturas = new ArrayList<>();

        for (UnidadeParaFaturamentoDTO unidade : unidades) {
            Fatura fatura = new Fatura();
            fatura.setUnidadeId(unidade.idUnidade());
            fatura.setCompetenciaId(competenciaId);
            fatura.setValor(valorBase);
            fatura.setDataVencimento(dataVencimento);
            fatura.setStatus(StatusFatura.RASCUNHO);

            novasFaturas.add(fatura);
        }

        faturaRepository.saveAll(novasFaturas);
    }
}

