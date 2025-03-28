package br.com.gymloadapi.modulos.exercicio.model;

import br.com.gymloadapi.modulos.exercicio.enums.ETipoExercicio;
import br.com.gymloadapi.modulos.exercicio.enums.ETipoPegada;
import br.com.gymloadapi.modulos.grupomuscular.model.GrupoMuscular;
import lombok.*;

import jakarta.persistence.*;

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

    public String getNomeComTipoExercicio() {
        return this.nome + " (" + this.tipoExercicio.name() + ")";
    }
}
