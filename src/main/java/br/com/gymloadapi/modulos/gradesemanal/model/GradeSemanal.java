package br.com.gymloadapi.modulos.gradesemanal.model;

import br.com.gymloadapi.modulos.comum.enums.EDiaSemana;
import br.com.gymloadapi.modulos.treino.model.Treino;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "GRADE_SEMANAL")
@Table(uniqueConstraints = @UniqueConstraint(
    name = "UQ_GRADE_SEMANAL_DIA", columnNames = {"FK_USUARIO", "DIA_SEMANA"}))
public class GradeSemanal {

    @Id
    @GeneratedValue(generator = "SEQ_GRADE_SEMANAL", strategy = SEQUENCE)
    @SequenceGenerator(name = "SEQ_GRADE_SEMANAL", sequenceName = "SEQ_GRADE_SEMANAL", allocationSize = 1)
    private Integer id;

    @Enumerated(STRING)
    @Column(nullable = false)
    private EDiaSemana diaSemana;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FK_TREINO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_GRADE_SEMANAL_TREINO"), nullable = false)
    private Treino treino;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_GRADE_SEMANAL_USUARIO"), nullable = false)
    private Usuario usuario;

    public void alterarTreino(Treino treino) {
        this.setTreino(treino);
    }
}
