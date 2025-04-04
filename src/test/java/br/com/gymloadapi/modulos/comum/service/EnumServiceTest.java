package br.com.gymloadapi.modulos.comum.service;

import br.com.gymloadapi.modulos.comum.dto.SelectResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ExtendWith(MockitoExtension.class)
class EnumServiceTest {

    @InjectMocks
    private EnumService service;

    @Test
    void getTiposExerciciosSelect_deveRetornarTodosOsTiposDeExercicios_quandoSolicitado() {
        assertThat(service.getTiposExerciciosSelect())
            .extracting(SelectResponse::value, SelectResponse::label)
            .containsExactly(tuple("HALTER", "Halter"), tuple("BARRA", "Barra"),
                tuple("MAQUINA", "Maquina"), tuple("POLIA", "Polia"),
                tuple("ANILHA", "Anilha"), tuple("BOLA", "Bola"),
                tuple("KETLLEBEL", "Kettlebel"), tuple("BAG", "Bag"),
                tuple("CORPORAL", "Corporal"));
    }

    @Test
    void getTiposPegadasSelect_deveRetornarTodosOsTiposDePegadas_quandoSolicitado() {
        assertThat(service.getTiposPegadasSelect())
            .extracting(SelectResponse::value, SelectResponse::label)
            .containsExactly(tuple("PRONADA", "Pronada"), tuple("SUPINADA", "Supinada"),
                tuple("NEUTRA", "Neutra"), tuple("CORDA", "Corda"));
    }

    @Test
    void getUnidadesPesosSelect_deveRetornarTodasAsUnidadesDePesos_quandoSolicitado() {
        assertThat(service.getUnidadesPesosSelect())
            .extracting(SelectResponse::value, SelectResponse::label)
            .containsExactly(tuple("KG", "Quilogramas"), tuple("LBS", "Libras"));
    }
}
