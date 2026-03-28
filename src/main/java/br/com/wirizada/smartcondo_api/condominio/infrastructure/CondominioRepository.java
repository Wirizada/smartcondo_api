package br.com.wirizada.smartcondo_api.condominio.infrastructure;

import br.com.wirizada.smartcondo_api.condominio.domain.Condominio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CondominioRepository extends JpaRepository< Condominio, UUID> {
}
