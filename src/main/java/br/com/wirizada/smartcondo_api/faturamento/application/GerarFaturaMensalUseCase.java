package br.com.wirizada.smartcondo_api.faturamento.application;

import br.com.wirizada.smartcondo_api.condominio.application.gateways.ApartamentoGateway;
import br.com.wirizada.smartcondo_api.condominio.domain.Apartamento;
import br.com.wirizada.smartcondo_api.faturamento.application.gateways.FaturaGateway;
import br.com.wirizada.smartcondo_api.faturamento.domain.Fatura;
import br.com.wirizada.smartcondo_api.faturamento.domain.StatusFatura;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class GerarFaturaMensalUseCase {

    private final ApartamentoGateway apartamentoGateway;
    private final FaturaGateway faturaGateway;

    public GerarFaturaMensalUseCase(ApartamentoGateway apartamentoGateway, FaturaGateway faturaGateway) {
        this.apartamentoGateway = apartamentoGateway;
        this.faturaGateway = faturaGateway;
    }

    @Transactional
    public void executar(BigDecimal valorBase, LocalDate dataVencimento){

        List<Apartamento> apartamentos = apartamentoGateway.buscarTodosApartamentos();
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

        faturaGateway.salvarTodasFaturas(novasFaturas);
    }

}
