package br.com.gymloadapi.modulos.tipovariacao.model;

import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TIPO_VARIACAO")
public class TipoVariacao {

    @Id
    @GeneratedValue(generator = "SEQ_TIPO_VARIACAO", strategy = SEQUENCE)
    @SequenceGenerator(name = "SEQ_TIPO_VARIACAO", sequenceName = "SEQ_TIPO_VARIACAO", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalDateTime dataCadastro;

    @Column(nullable = false)
    private Integer usuarioCadastroId;

    @Column(nullable = false)
    private String usuarioCadastroNome;
}
