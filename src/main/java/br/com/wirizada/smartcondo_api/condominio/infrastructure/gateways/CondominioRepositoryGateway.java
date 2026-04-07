package br.com.wirizada.smartcondo_api.condominio.infrastructure.gateways;

import br.com.wirizada.smartcondo_api.condominio.application.gateways.CondominioGateway;
import br.com.wirizada.smartcondo_api.condominio.domain.Condominio;
import br.com.wirizada.smartcondo_api.condominio.infrastructure.CondominioRepository;
import org.springframework.stereotype.Component;

@Component
public class CondominioRepositoryGateway implements CondominioGateway {

    private final CondominioRepository condominioRepository;

    public CondominioRepositoryGateway(CondominioRepository condominioRepository){
        this.condominioRepository = condominioRepository;
    }

    @Override
    public Condominio salvarCondominio(Condominio condominio) {
        return condominioRepository.save(condominio);
    }
}
