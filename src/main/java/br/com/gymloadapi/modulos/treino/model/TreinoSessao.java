package br.com.gymloadapi.modulos.treino.model;

import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TREINO_SESSAO")
public class TreinoSessao {

    @Id
    @GeneratedValue(generator = "SEQ_TREINO_SESSAO", strategy = SEQUENCE)
    @SequenceGenerator(name = "SEQ_TREINO_SESSAO", sequenceName = "SEQ_TREINO_SESSAO", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private LocalDate dataSessao;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FK_TREINO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_TREINO"), nullable = false)
    private Treino treino;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_USUARIO"), nullable = false)
    private Usuario usuario;
}
