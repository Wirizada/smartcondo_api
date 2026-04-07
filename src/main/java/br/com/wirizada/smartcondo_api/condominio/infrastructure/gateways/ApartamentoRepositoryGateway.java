package br.com.wirizada.smartcondo_api.condominio.infrastructure.gateways;

import br.com.wirizada.smartcondo_api.condominio.application.gateways.ApartamentoGateway;
import br.com.wirizada.smartcondo_api.condominio.domain.Apartamento;
import br.com.wirizada.smartcondo_api.condominio.infrastructure.ApartamentoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApartamentoRepositoryGateway implements ApartamentoGateway {

    private final ApartamentoRepository apartamentoRepository;

    public ApartamentoRepositoryGateway(ApartamentoRepository apartamentoRepository) {
        this.apartamentoRepository = apartamentoRepository;
    }

    @Override
    public List<Apartamento> buscarTodosApartamentos() {
        return apartamentoRepository.findAll();
    }

}
