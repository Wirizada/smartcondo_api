package br.com.wirizada.smartcondo_api.condominio.application;

import br.com.wirizada.smartcondo_api.condominio.infra.CondominioRepository;
import br.com.wirizada.smartcondo_api.condominio.model.Condominio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CondominioService {

    private final CondominioRepository condominioRepository;

    public CondominioService(CondominioRepository condominioRepository) {
        this.condominioRepository = condominioRepository;
    }

    @Transactional
    public Condominio cadastrar(String nome, String cnpj) {
        Condominio condominio = new Condominio();
        condominio.setNome(nome);
        condominio.setCnpj(cnpj);
        return condominioRepository.save(condominio);
    }
}

