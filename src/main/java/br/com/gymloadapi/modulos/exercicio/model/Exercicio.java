package br.com.gymloadapi.modulos.exercicio.model;

import br.com.gymloadapi.modulos.exercicio.dto.ExercicioRequest;
import br.com.gymloadapi.modulos.grupomuscular.model.GrupoMuscular;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_GRUPO_MUSCULAR", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_GRUPO_MUSCULAR"), nullable = false)
    private GrupoMuscular grupoMuscular;

    public static Exercicio of(ExercicioRequest request, GrupoMuscular grupoMuscular) {
        return Exercicio.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .grupoMuscular(grupoMuscular)
                .build();
    }
}
