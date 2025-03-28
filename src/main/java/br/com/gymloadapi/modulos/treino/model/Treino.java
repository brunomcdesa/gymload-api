package br.com.gymloadapi.modulos.treino.model;

import br.com.gymloadapi.modulos.exercicio.model.Exercicio;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TREINO")
public class Treino {

    @Id
    @GeneratedValue(generator = "SEQ_TREINO", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "SEQ_TREINO", sequenceName = "SEQ_TREINO", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalDate dataCadastro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID", columnDefinition = "UUID",
        foreignKey = @ForeignKey(name = "FK_USUARIO"), nullable = false)
    private Usuario usuario;

    @ManyToMany
    @SuppressWarnings("Indentation")
    @JoinTable(name = "TREINO_EXERCICIO", joinColumns = {
        @JoinColumn(name = "FK_TREINO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_TREINO"))}, inverseJoinColumns = {
        @JoinColumn(name = "FK_EXERCICIO", referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_EXERCICIO"))})
    private List<Exercicio> exercicios;
}
