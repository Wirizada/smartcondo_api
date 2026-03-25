package br.com.wirizada.smartcondo_api.faturamento.infrastructure;

import br.com.wirizada.smartcondo_api.faturamento.domain.Fatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FaturaRepository extends JpaRepository<Fatura, UUID> {
}
