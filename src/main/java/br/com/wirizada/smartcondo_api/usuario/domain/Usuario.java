package br.com.wirizada.smartcondo_api.usuario.domain;

import br.com.wirizada.smartcondo_api.condominio.domain.Condominio;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.TenantId;

import java.util.UUID;

@Entity
@Table(name= "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String senha;

    @Enumerated(EnumType.STRING)
    private TipoUsuario role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condominio_id", nullable = false)
    @TenantId
    private Condominio condominio;
}
