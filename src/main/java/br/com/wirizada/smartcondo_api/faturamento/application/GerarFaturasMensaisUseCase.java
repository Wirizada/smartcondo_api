package br.com.wirizada.smartcondo_api.faturamento.application;

import br.com.wirizada.smartcondo_api.condominio.domain.Apartamento;
import br.com.wirizada.smartcondo_api.condominio.infrastructure.ApartamentoRepository;
import br.com.wirizada.smartcondo_api.faturamento.domain.Fatura;
import br.com.wirizada.smartcondo_api.faturamento.domain.StatusFatura;
import br.com.wirizada.smartcondo_api.faturamento.infrastructure.FaturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class GerarFaturasMensaisUseCase {

    private final ApartamentoRepository apartamentoRepository;
    private final FaturaRepository faturaRepository;

    public GerarFaturasMensaisUseCase(ApartamentoRepository apartamentoRepository, FaturaRepository faturaRepository) {
        this.apartamentoRepository = apartamentoRepository;
        this.faturaRepository = faturaRepository;
    }

    @Transactional
    public void executar(BigDecimal valorBase, LocalDate dataVencimento){

        List<Apartamento> apartamentos = apartamentoRepository.findAll();
        List<Fatura> novasFaturas = new ArrayList<>();

        for (Apartamento apartamento : apartamentos) {
            Fatura fatura = new Fatura();
            fatura.setApartamento(apartamento);
            fatura.setCondominio(apartamento.getCondominio());
            fatura.setValor(valorBase);
            fatura.setDataVencimento(dataVencimento);
            fatura.setStatus(StatusFatura.PENDENTE);

            novasFaturas.add(fatura);
        }

        faturaRepository.saveAll(novasFaturas);
    }

}
