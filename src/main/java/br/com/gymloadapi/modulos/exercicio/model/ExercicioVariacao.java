package br.com.gymloadapi.modulos.exercicio.model;

import br.com.gymloadapi.modulos.comum.enums.ETipoEquipamento;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "EXERCICIO_VARIACAO")
public class ExercicioVariacao {

    @Id
    @GeneratedValue(generator = "SEQ_EXERCICIO_VARIACAO", strategy = SEQUENCE)
    @SequenceGenerator(name = "SEQ_EXERCICIO_VARIACAO", sequenceName = "SEQ_EXERCICIO_VARIACAO", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column
    @Enumerated(STRING)
    private ETipoEquipamento tipoEquipamento;

    @Column(nullable = false)
    private LocalDateTime dataCadastro;

    @Column(nullable = false)
    private Integer usuarioCadastroId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FK_EXERCICIO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_EXERCICIO"), nullable = false)
    private Exercicio exercicio;
}
