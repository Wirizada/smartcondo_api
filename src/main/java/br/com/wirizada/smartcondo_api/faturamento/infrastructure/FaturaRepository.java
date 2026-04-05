package br.com.wirizada.smartcondo_api.faturamento.infrastructure;

import br.com.wirizada.smartcondo_api.faturamento.domain.Fatura;
import br.com.wirizada.smartcondo_api.faturamento.infrastructure.dto.ListarFaturaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FaturaRepository extends JpaRepository<Fatura, UUID> {

    @Query(value = """
        SELECT
            f.id AS id,
            f.valor AS valor,
            f.data_vencimento AS dataVencimento,
            f.status AS status,
            a.numero AS apartamentoNumero
        FROM tb_faturas f
        INNER JOIN tb_apartamentos a ON f.apartamento_id = a.id
        WHERE f.condominio_id = :tenantId
    """, nativeQuery = true)
    List<ListarFaturaProjection> listarFaturasPorCondominio(@Param("tenantId") UUID tenantId);
}
