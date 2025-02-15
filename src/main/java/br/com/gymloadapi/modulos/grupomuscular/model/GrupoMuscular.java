package br.com.gymloadapi.modulos.grupomuscular.model;

import jakarta.persistence.*;
import lombok.*;

@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "GRUPO_MUSCULAR")
public class GrupoMuscular {

    @Id
    @GeneratedValue(generator = "SEQ_GRUPO_MUSCULAR", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "SEQ_GRUPO_MUSCULAR", sequenceName = "SEQ_GRUPO_MUSCULAR", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String codigo;

}
