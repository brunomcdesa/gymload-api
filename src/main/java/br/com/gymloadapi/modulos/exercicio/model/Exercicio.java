package br.com.gymloadapi.modulos.exercicio.model;

import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;
import br.com.gymloadapi.modulos.comum.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.comum.enums.ETipoPegada;
import br.com.gymloadapi.modulos.grupomuscular.model.GrupoMuscular;
import br.com.gymloadapi.modulos.treino.model.Treino;
import lombok.*;

import jakarta.persistence.*;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "EXERCICIO")
public class Exercicio {

    @Id
    @GeneratedValue(generator = "SEQ_EXERCICIO", strategy = SEQUENCE)
    @SequenceGenerator(name = "SEQ_EXERCICIO", sequenceName = "SEQ_EXERCICIO", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String descricao;

    @Column
    @Enumerated(STRING)
    private ETipoEquipamento tipoEquipamento;

    @Column
    @Enumerated(STRING)
    private ETipoExercicio tipoExercicio;

    @Column
    @Enumerated(STRING)
    private ETipoPegada tipoPegada;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FK_GRUPO_MUSCULAR", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_GRUPO_MUSCULAR"))
    private GrupoMuscular grupoMuscular;

    @ManyToMany
    @SuppressWarnings("Indentation")
    @JoinTable(name = "TREINO_EXERCICIO", joinColumns = {
        @JoinColumn(name = "FK_EXERCICIO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_EXERCICIO"))}, inverseJoinColumns = {
        @JoinColumn(name = "FK_TREINO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_TREINO"))})
    private List<Treino> treinos;

    public String getNomeComTipoEquipamento() {
        return this.nome + (this.tipoEquipamento != null ? " (" + this.tipoEquipamento.name() + ")" : "");
    }
}
