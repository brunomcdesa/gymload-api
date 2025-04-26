package br.com.gymloadapi.modulos.registroatividade.registrocardio.model;

import br.com.gymloadapi.modulos.registroatividade.model.RegistroAtividade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static jakarta.persistence.GenerationType.SEQUENCE;
import static java.lang.String.format;

@Table
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "REGISTRO_CARDIO")
public class RegistroCardio extends RegistroAtividade {

    @Id
    @GeneratedValue(generator = "SEQ_REGISTRO_CARDIO", strategy = SEQUENCE)
    @SequenceGenerator(name = "SEQ_REGISTRO_CARDIO", sequenceName = "SEQ_REGISTRO_CARDIO", allocationSize = 1)
    private Integer id;

    @Positive
    @Column(nullable = false)
    private Double distancia;

    @Column(nullable = false)
    private Double duracao;

    public String getDistanciaFormatada() {
        return format("%s km", this.distancia);
    }

    public String getVelocidadeMedia() {
        var velocidadeMedia = (double) this.distancia / this.duracao;

        var df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US));
        return format("%s km/h", df.format(velocidadeMedia));
    }
}
