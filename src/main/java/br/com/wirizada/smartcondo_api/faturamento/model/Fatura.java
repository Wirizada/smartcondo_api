package br.com.wirizada.smartcondo_api.faturamento.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.TenantId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "faturas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Fatura {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusFatura status;

    @Column(name = "unidade_id", nullable = false)
    private UUID unidadeId;

    @Column(name = "competencia_id", nullable = false)
    private UUID competenciaId;

    @Column(name = "condominio_id", nullable = false)
    @TenantId
    private UUID condominioId;
}

