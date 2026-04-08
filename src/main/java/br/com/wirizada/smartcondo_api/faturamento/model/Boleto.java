package br.com.wirizada.smartcondo_api.faturamento.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.TenantId;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "boletos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "fatura_id", nullable = false)
    private UUID faturaId;

    @Column(name = "linha_digitavel", length = 60)
    private String linhaDigitavel;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "condominio_id", nullable = false)
    @TenantId
    private UUID condominioId;
}

