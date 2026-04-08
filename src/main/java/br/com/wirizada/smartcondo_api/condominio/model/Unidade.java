package br.com.wirizada.smartcondo_api.condominio.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.TenantId;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "unidades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Unidade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(name = "bloco_id", nullable = false)
    private UUID blocoId;

    @Column(name = "morador_id", nullable = false)
    private UUID moradorId;

    @Column(name = "fracao_ideal", nullable = false, precision = 6, scale = 4)
    private BigDecimal fracaoIdeal;

    @Column(nullable = false)
    private boolean ativa = true;

    @Column(name = "condominio_id", nullable = false)
    @TenantId
    private UUID condominioId;
}

