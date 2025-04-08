package br.com.gymloadapi.modulos.treino.model;

import br.com.gymloadapi.modulos.comum.enums.ESituacao;
import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

import static br.com.gymloadapi.modulos.comum.enums.ESituacao.ATIVO;
import static br.com.gymloadapi.modulos.comum.enums.ESituacao.INATIVO;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TREINO")
public class Treino {

    @Id
    @GeneratedValue(generator = "SEQ_TREINO", strategy = SEQUENCE)
    @SequenceGenerator(name = "SEQ_TREINO", sequenceName = "SEQ_TREINO", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalDate dataCadastro;

    @Enumerated(STRING)
    @Column(nullable = false)
    private ESituacao situacao;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID", columnDefinition = "UUID",
        foreignKey = @ForeignKey(name = "FK_USUARIO"), nullable = false)
    private Usuario usuario;

    @ManyToMany
    @OrderColumn(name = "ORDEM_EXERCICIO")
    @SuppressWarnings("Indentation")
    @JoinTable(name = "TREINO_EXERCICIO", joinColumns = {
        @JoinColumn(name = "FK_TREINO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_TREINO"))}, inverseJoinColumns = {
        @JoinColumn(name = "FK_EXERCICIO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_EXERCICIO"))})
    private List<Exercicio> exercicios;

    public List<Integer> getExerciciosIds() {
        return exercicios.stream()
            .map(Exercicio::getId)
            .toList();
    }

    public void alterarSituacao() {
        this.setSituacao(this.situacao == ATIVO ? INATIVO : ATIVO);
    }
}
