package br.com.gymloadapi.modulos.gradesemanal.model;

import br.com.gymloadapi.modulos.comum.base.HistoricoBase;
import br.com.gymloadapi.modulos.comum.enums.EDiaSemana;
import br.com.gymloadapi.modulos.treino.model.Treino;
import br.com.gymloadapi.modulos.usuario.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Table
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "GRADE_SEMANAL_HISTORICO")
public class GradeSemanalHistorico extends HistoricoBase {

    @Id
    @GeneratedValue(generator = "SEQ_GRADE_SEMANAL_HISTORICO", strategy = SEQUENCE)
    @SequenceGenerator(name = "SEQ_GRADE_SEMANAL_HISTORICO",
        sequenceName = "SEQ_GRADE_SEMANAL_HISTORICO", allocationSize = 1)
    private Integer id;

    @Enumerated(STRING)
    @Column(nullable = false)
    private EDiaSemana diaSemana;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FK_TREINO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_GRADE_SEMANAL_HISTORICO_TREINO"), nullable = false)
    private Treino treino;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_GRADE_SEMANAL_HISTORICO_USUARIO"), nullable = false)
    private Usuario usuario;
}
