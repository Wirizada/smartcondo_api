package br.com.wirizada.smartcondo_api.condominio.infra;

import br.com.wirizada.smartcondo_api.condominio.model.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UnidadeRepository extends JpaRepository<Unidade, UUID> {
    List<Unidade> findByAtivaTrue();
}

