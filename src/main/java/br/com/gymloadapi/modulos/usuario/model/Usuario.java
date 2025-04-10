package br.com.gymloadapi.modulos.usuario.model;

import br.com.gymloadapi.modulos.usuario.enums.EUserRole;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static br.com.gymloadapi.modulos.usuario.enums.EUserRole.ADMIN;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.SEQUENCE;

@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "USUARIO")
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(generator = "SEQ_USUARIO", strategy = SEQUENCE)
    @SequenceGenerator(name = "SEQ_USUARIO", sequenceName = "SEQ_USUARIO", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private UUID uuid;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String senha;

    @Column(name = "ROLE")
    @Enumerated(STRING)
    @ElementCollection(fetch = EAGER)
    @CollectionTable(name = "USUARIO_ROLE", joinColumns = @JoinColumn(name = "USUARIO_ID"))
    private List<EUserRole> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.mapRole()))
            .toList();
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public String[] getRolesArray() {
        return this.roles.stream()
            .map(EUserRole::mapRole)
            .toArray(String[]::new);
    }

    public boolean isAdmin() {
        return this.roles.contains(ADMIN);
    }
}
