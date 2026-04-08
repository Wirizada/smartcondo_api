package br.com.wirizada.smartcondo_api.condominio.infra;

import br.com.wirizada.smartcondo_api.condominio.model.Bloco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BlocoRepository extends JpaRepository<Bloco, UUID> {
}

