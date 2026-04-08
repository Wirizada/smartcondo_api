package br.com.wirizada.smartcondo_api.condominio.infra;

import br.com.wirizada.smartcondo_api.condominio.model.Morador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MoradorRepository extends JpaRepository<Morador, UUID> {
}

