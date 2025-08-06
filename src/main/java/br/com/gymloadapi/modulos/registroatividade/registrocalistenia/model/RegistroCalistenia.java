package br.com.gymloadapi.modulos.registroatividade.registrocalistenia.model;

import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;
import br.com.gymloadapi.modulos.comum.enums.EUnidadePeso;
import br.com.gymloadapi.modulos.registroatividade.model.RegistroAtividade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;

import static br.com.gymloadapi.modulos.comum.utils.MapUtils.mapNull;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.SEQUENCE;
import static java.lang.String.format;

@Table
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "REGISTRO_CALISTENIA")
public class RegistroCalistenia extends RegistroAtividade {

    @Id
    @GeneratedValue(generator = "SEQ_REGISTRO_CALISTENIA", strategy = SEQUENCE)
    @SequenceGenerator(name = "SEQ_REGISTRO_CALISTENIA", sequenceName = "SEQ_REGISTRO_CALISTENIA", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private Integer qtdSeries;

    @Column(nullable = false)
    private Integer qtdRepeticoes;

    @Column
    private Double pesoAdicional;

    @Column
    private ETipoEquipamento tipoEquipamentoPesoAdicional;

    @Column
    @Enumerated(STRING)
    private EUnidadePeso unidadePeso;

    public String getPesoComUnidadePeso() {
        return mapNull(this.pesoAdicional, () -> format("%s (%s)", this.pesoAdicional, this.unidadePeso.name()));
    }

    public String getQtdRepeticoesDestaque() {
        return format("%s reps", this.qtdRepeticoes);
    }
}
