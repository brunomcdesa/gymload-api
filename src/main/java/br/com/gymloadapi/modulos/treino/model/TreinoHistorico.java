package br.com.gymloadapi.modulos.treino.model;

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
@Entity(name = "TREINO_HISTORICO")
public class TreinoHistorico extends HistoricoBase {

    @Id
    @GeneratedValue(generator = "SEQ_TREINO_HISTORICO", strategy = SEQUENCE)
    @SequenceGenerator(name = "SEQ_TREINO_HISTORICO", sequenceName = "SEQ_TREINO_HISTORICO", allocationSize = 1)
    private Integer id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FK_TREINO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_TREINO"), nullable = false)
    private Treino treino;
}
