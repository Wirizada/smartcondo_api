package br.com.wirizada.smartcondo_api.condominio.infrastructure;

import br.com.wirizada.smartcondo_api.condominio.domain.Apartamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApartamentoRepository extends JpaRepository<Apartamento, UUID> {
}
