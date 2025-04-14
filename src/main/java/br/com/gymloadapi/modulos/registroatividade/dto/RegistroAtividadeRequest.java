package br.com.gymloadapi.modulos.registroatividade.dto;

import br.com.gymloadapi.modulos.comum.enums.EUnidadePeso;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Aerobico;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Musculacao;
import br.com.gymloadapi.modulos.comum.utils.ValidacaoUtils;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record RegistroAtividadeRequest(
    @NotNull
    Integer exercicioId,

    @Null(groups = Aerobico.class)
    @NotNull(groups = Musculacao.class)
    Double peso,

    @Null(groups = Aerobico.class)
    @NotNull(groups = Musculacao.class)
    EUnidadePeso unidadePeso,

    @Null(groups = Aerobico.class)
    @NotNull(groups = Musculacao.class)
    Integer qtdRepeticoes,

    @Null(groups = Aerobico.class)
    @NotNull(groups = Musculacao.class)
    Integer qtdSeries,

    @Null(groups = Musculacao.class)
    @NotNull(groups = Aerobico.class)
    Double distancia,

    @Null(groups = Musculacao.class)
    @NotNull(groups = Aerobico.class)
    Double duracao
) {

    public void aplicarGroupValidators(Class<?> groupValidator) {
        ValidacaoUtils.aplicarValidacoes(this, groupValidator);
    }
}
