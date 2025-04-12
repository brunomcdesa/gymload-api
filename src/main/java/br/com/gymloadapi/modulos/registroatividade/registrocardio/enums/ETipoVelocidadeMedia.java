package br.com.gymloadapi.modulos.registroatividade.registrocardio.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.lang.String.format;

@Getter
@AllArgsConstructor
public enum ETipoVelocidadeMedia {

    KM_H("km/h") {
        @Override
        public String formatarDistancia(double distancia) {
            return format("%.2f km", distancia);
        }

        @Override
        public String formatarDuracao(double duracao) {
            return format("%.2f h", duracao);
        }
    },
    M_S("m/s") {
        @Override
        public String formatarDistancia(double distancia) {
            return String.format("%.2f m", distancia);
        }

        @Override
        public String formatarDuracao(double duracao) {
            return String.format("%.2f s", duracao);
        }
    };

    private final String descricao;

    public abstract String formatarDistancia(double distancia);

    public abstract String formatarDuracao(double duracao);
}
