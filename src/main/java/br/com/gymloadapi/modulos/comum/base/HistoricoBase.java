package br.com.gymloadapi.modulos.comum.base;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public abstract class HistoricoBase {

    @Column(nullable = false)
    protected LocalDateTime dataCadastro;

    @Column(nullable = false)
    protected Integer usuarioCadastroId;

    @Enumerated(STRING)
    @Column(nullable = false)
    protected EAcao acao;
}
