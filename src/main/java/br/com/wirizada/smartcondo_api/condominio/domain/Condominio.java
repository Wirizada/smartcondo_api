package br.com.wirizada.smartcondo_api.condominio.domain;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name= "condominios")
@Table(name= "condominios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Condominio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column( nullable = false)
    private boolean ativo = true;

}
