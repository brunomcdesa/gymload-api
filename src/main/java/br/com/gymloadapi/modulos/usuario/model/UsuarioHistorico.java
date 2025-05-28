package br.com.gymloadapi.modulos.usuario.model;

import br.com.gymloadapi.modulos.comum.base.HistoricoBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Table
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "USUARIO_HISTORICO")
public class UsuarioHistorico extends HistoricoBase {

    @Id
    @GeneratedValue(generator = "SEQ_USUARIO_HISTORICO", strategy = SEQUENCE)
    @SequenceGenerator(name = "SEQ_USUARIO_HISTORICO", sequenceName = "SEQ_USUARIO_HISTORICO", allocationSize = 1)
    private Integer id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FK_USUARIO", referencedColumnName = "ID",
        foreignKey = @ForeignKey(name = "FK_USUARIO"), nullable = false)
    private Usuario usuario;
}
