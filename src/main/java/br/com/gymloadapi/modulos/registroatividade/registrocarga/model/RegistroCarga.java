package br.com.gymloadapi.modulos.registroatividade.registrocarga.model;

import br.com.gymloadapi.modulos.comum.enums.EUnidadePeso;
import br.com.gymloadapi.modulos.registroatividade.model.RegistroAtividade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Table
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "REGISTRO_CARGA")
public class RegistroCarga extends RegistroAtividade {

    @Id
    @GeneratedValue(generator = "SEQ_REGISTRO_CARGA", strategy = SEQUENCE)
    @SequenceGenerator(name = "SEQ_REGISTRO_CARGA", sequenceName = "SEQ_REGISTRO_CARGA", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private Double peso;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EUnidadePeso unidadePeso;

    @Column(nullable = false)
    private Integer qtdRepeticoes;

    @Column(nullable = false)
    private Integer qtdSeries;

    public String getCargaComUnidadePeso() {
        return this.peso + " (" + unidadePeso.name() + ")";
    }
}
