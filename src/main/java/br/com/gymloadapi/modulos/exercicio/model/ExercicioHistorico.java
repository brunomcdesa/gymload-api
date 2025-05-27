package br.com.gymloadapi.modulos.exercicio.model;

import br.com.gymloadapi.modulos.comum.base.HistoricoBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Table
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "EXERCICIO_HISTORICO")
public class ExercicioHistorico extends HistoricoBase {

    @Id
    @GeneratedValue(generator = "SEQ_EXERCICIO_HISTORICO", strategy = SEQUENCE)
    @SequenceGenerator(name = "SEQ_EXERCICIO_HISTORICO", sequenceName = "SEQ_EXERCICIO_HISTORICO", allocationSize = 1)
    private Integer id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FK_EXERCICIO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_EXERCICIO"), nullable = false)
    private Exercicio exercicio;
}
