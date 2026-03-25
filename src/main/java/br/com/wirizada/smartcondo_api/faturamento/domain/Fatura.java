package br.com.wirizada.smartcondo_api.faturamento.domain;

import br.com.wirizada.smartcondo_api.condominio.domain.Apartamento;
import br.com.wirizada.smartcondo_api.condominio.domain.Condominio;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.TenantId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name= "faturas")
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

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusFatura status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartamento_id", nullable = false)
    private Apartamento apartamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condominio_id", nullable = false)
    @TenantId
    private Condominio condominio;
}
