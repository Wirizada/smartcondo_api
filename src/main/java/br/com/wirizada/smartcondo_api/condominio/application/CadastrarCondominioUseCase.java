package br.com.wirizada.smartcondo_api.condominio.application;

import br.com.wirizada.smartcondo_api.condominio.application.gateways.CondominioGateway;
import br.com.wirizada.smartcondo_api.condominio.domain.Condominio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastrarCondominioUseCase {

    private final CondominioGateway condominioGateway;

    public CadastrarCondominioUseCase(CondominioGateway condominioGateway) {
        this.condominioGateway = condominioGateway;
    }

    @Transactional
    public Condominio executar(String nome, String cnpj) {
        Condominio condominio = new Condominio();

        condominio.setNome(nome);
        condominio.setCnpj(cnpj);

        return condominioGateway.salvarCondominio(condominio);
    }
}
