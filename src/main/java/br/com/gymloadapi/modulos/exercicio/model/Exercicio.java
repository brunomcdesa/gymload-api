package br.com.gymloadapi.modulos.exercicio.model;

import br.com.gymloadapi.modulos.exercicio.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.exercicio.enums.ETipoPegada;
import br.com.gymloadapi.modulos.grupomuscular.model.GrupoMuscular;
import br.com.gymloadapi.modulos.treino.model.Treino;
import lombok.*;

import jakarta.persistence.*;
import java.util.List;

@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "EXERCICIO")
public class Exercicio {

    @Id
    @GeneratedValue(generator = "SEQ_EXERCICIO", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "SEQ_EXERCICIO", sequenceName = "SEQ_EXERCICIO", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String descricao;

    @Column
    @Enumerated(EnumType.STRING)
    private ETipoExercicio tipoExercicio;

    @Column
    @Enumerated(EnumType.STRING)
    private ETipoPegada tipoPegada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_GRUPO_MUSCULAR", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_GRUPO_MUSCULAR"), nullable = false)
    private GrupoMuscular grupoMuscular;

    @ManyToMany
    @SuppressWarnings("Indentation")
    @JoinTable(name = "TREINO_EXERCICIO", joinColumns = {
        @JoinColumn(name = "FK_EXERCICIO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_EXERCICIO"))}, inverseJoinColumns = {
        @JoinColumn(name = "FK_TREINO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_TREINO"))})
    private List<Treino> treinos;

    public String getNomeComTipoExercicio() {
        return this.nome + " (" + this.tipoExercicio.name() + ")";
    }
}
