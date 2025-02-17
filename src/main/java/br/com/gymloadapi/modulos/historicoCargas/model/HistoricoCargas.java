package br.com.gymloadapi.modulos.historicoCargas.model;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.historicoCargas.enums.EUnidadePeso;
import jakarta.persistence.*;
import lombok.*;

@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "HISTORICO_CARGAS")
public class HistoricoCargas {

    @Id
    @GeneratedValue(generator = "SEQ_HISTORICO_CARGAS", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "SEQ_HISTORICO_CARGAS", sequenceName = "SEQ_HISTORICO_CARGAS", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private Double carga;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EUnidadePeso unidadePeso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_EXERCICIO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_EXERCICIO"), nullable = false)
    private Exercicio exercicio;

    public String getCargaComUnidadePeso() {
        return this.carga + " (" + unidadePeso.name() + ")";
    }
}
