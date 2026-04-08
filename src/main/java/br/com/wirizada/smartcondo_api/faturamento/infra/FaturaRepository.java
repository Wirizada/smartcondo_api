package br.com.wirizada.smartcondo_api.faturamento.infra;

import br.com.wirizada.smartcondo_api.faturamento.model.Fatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FaturaRepository extends JpaRepository<Fatura, UUID> {
}

