package br.com.gymloadapi.modulos.tipovariacao.model;

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
@Entity(name = "TIPO_VARIACAO_HISTORICO")
public class TipoVariacaoHistorico extends HistoricoBase {

    @Id
    @GeneratedValue(generator = "SEQ_TIPO_VARIACAO_HISTORICO", strategy = SEQUENCE)
    @SequenceGenerator(name = "SEQ_TIPO_VARIACAO_HISTORICO", sequenceName = "SEQ_TIPO_VARIACAO_HISTORICO", allocationSize = 1)
    private Integer id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FK_TIPO_VARIACAO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_TIPO_VARIACAO"), nullable = false)
    private TipoVariacao tipoVariacao;
}
