package br.com.gymloadapi.modulos.treino.model;

import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TREINO_COMPARTILHAMENTO")
public class TreinoCompartilhamento {

    @Id
    @GeneratedValue(generator = "SEQ_TREINO_COMPARTILHAMENTO", strategy = SEQUENCE)
    @SequenceGenerator(name = "SEQ_TREINO_COMPARTILHAMENTO",
        sequenceName = "SEQ_TREINO_COMPARTILHAMENTO", allocationSize = 1)
    private Integer id;

    @Column(nullable = false, unique = true, length = 36)
    private String token;

    @Column(nullable = false, unique = true, length = 8)
    private String codigo;

    @Column(nullable = false)
    private String nomeTreino;

    @Column(nullable = false)
    private String exerciciosIds;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private LocalDateTime dataExpiracao;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_TREINO_COMPARTILHAMENTO_USUARIO"), nullable = false)
    private Usuario usuario;
}
