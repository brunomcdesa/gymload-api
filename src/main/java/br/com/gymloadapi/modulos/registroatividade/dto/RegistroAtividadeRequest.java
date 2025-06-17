package br.com.gymloadapi.modulos.registroatividade.dto;

import br.com.gymloadapi.modulos.comum.enums.ETipoPegada;
import br.com.gymloadapi.modulos.comum.enums.EUnidadePeso;
import br.com.gymloadapi.modulos.comum.exception.ValidacaoException;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Aerobico;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Calistenia;
import br.com.gymloadapi.modulos.comum.groupvalidations.IGroupValidators.Musculacao;
import br.com.gymloadapi.modulos.comum.utils.ValidacaoUtils;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record RegistroAtividadeRequest(
    @NotNull
    Integer exercicioId,

    String observacao,

    @Null(groups = Aerobico.class)
    @NotNull(groups = Musculacao.class)
    Double peso,

    @Null(groups = Aerobico.class)
    @NotNull(groups = Musculacao.class)
    EUnidadePeso unidadePeso,

    @Null(groups = Aerobico.class)
    @NotNull(groups = {Musculacao.class, Calistenia.class})
    Integer qtdRepeticoes,

    @Null(groups = Aerobico.class)
    @NotNull(groups = {Musculacao.class, Calistenia.class})
    Integer qtdSeries,

    @Null(groups = {Musculacao.class, Calistenia.class})
    @NotNull(groups = Aerobico.class)
    Double distancia,

    @Null(groups = {Musculacao.class, Calistenia.class})
    @NotNull(groups = Aerobico.class)
    Double duracao,

    @Null(groups = {Aerobico.class, Calistenia.class})
    @NotNull(groups = Musculacao.class)
    ETipoPegada tipoPegada
) {

    public void aplicarGroupValidators(Class<?> groupValidator) {
        ValidacaoUtils.aplicarValidacoes(this, groupValidator);
    }

    public void validarPesoEUnidadePeso() {
        if (this.peso() != null ^ this.unidadePeso() != null) {
            throw new ValidacaoException("O peso e a unidade de peso devem ser informados juntos.");
        }
    }
}
