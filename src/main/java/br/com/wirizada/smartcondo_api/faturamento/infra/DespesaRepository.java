package br.com.wirizada.smartcondo_api.faturamento.infra;

import br.com.wirizada.smartcondo_api.faturamento.model.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DespesaRepository extends JpaRepository<Despesa, UUID> {
}

