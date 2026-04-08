package br.com.wirizada.smartcondo_api.faturamento.infra;

import br.com.wirizada.smartcondo_api.faturamento.model.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BoletoRepository extends JpaRepository<Boleto, UUID> {
}

