package br.com.wirizada.smartcondo_api.condominio.application;

import br.com.wirizada.smartcondo_api.condominio.domain.Condominio;
import br.com.wirizada.smartcondo_api.condominio.infrastructure.CondominioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastrarCondominioUseCase {

    private final CondominioRepository condominioRepository;

    public CadastrarCondominioUseCase(CondominioRepository condominioRepository) {
        this.condominioRepository = condominioRepository;
    }

    @Transactional
    public Condominio registrarCondominio(String nome, String cnpj) {
        Condominio condominio = new Condominio();

        condominio.setNome(nome);
        condominio.setCnpj(cnpj);

        return condominioRepository.save(condominio);
    }
}
