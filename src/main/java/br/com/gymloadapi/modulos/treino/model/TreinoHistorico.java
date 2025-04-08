package br.com.gymloadapi.modulos.treino.model;

import br.com.gymloadapi.modulos.comum.enums.EAcao;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TREINO_HISTORICO")
public class TreinoHistorico {

    @Id
    @GeneratedValue(generator = "SEQ_TREINO_HISTORICO", strategy = SEQUENCE)
    @SequenceGenerator(name = "SEQ_TREINO_HISTORICO", sequenceName = "SEQ_TREINO_HISTORICO", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private LocalDateTime dataCadastro;

    @Column(nullable = false)
    private UUID usuarioCadastroId;

    @Enumerated(STRING)
    @Column(nullable = false)
    private EAcao acao;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FK_TREINO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_TREINO"), nullable = false)
    private Treino treino;
}
