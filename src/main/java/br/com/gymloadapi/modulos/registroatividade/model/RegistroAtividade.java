package br.com.gymloadapi.modulos.registroatividade.model;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;
import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public abstract class RegistroAtividade {

    @Column(nullable = false)
    protected LocalDate dataCadastro;

    @Column(length = 150)
    protected String observacao;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FK_EXERCICIO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_EXERCICIO"), nullable = false)
    protected Exercicio exercicio;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_USUARIO"), nullable = false)
    protected Usuario usuario;

    public Integer getUsuarioId() {
        return usuario.getId();
    }
}
