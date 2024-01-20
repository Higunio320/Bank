package com.bank.entities.user;

import com.bank.entities.user.password.Password;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.bank.entities.user.constants.UserConstants.MAX_LOGIN_ATTEMPTS;
import static com.bank.entities.user.constants.UserConstants.THIRTY_SECONDS_TO_MILLIS;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "bank_user")
@Slf4j
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank
    private String login;

    @NotBlank
    @Email
    private String email;

    @ToString.Exclude
    @OneToMany
    private List<Password> passwords;

    private int incorrectLoginAttempts;

    private Instant unblockTime;

    @Override
    public final Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public final String getPassword() {
        return "";
    }

    @Override
    public final String getUsername() {
        return this.login;
    }

    @Override
    public final boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public final boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public final boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public final boolean isEnabled() {
        return true;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public final void increaseIncorrectLoginAttempts() {
        incorrectLoginAttempts++;

        if(incorrectLoginAttempts >= MAX_LOGIN_ATTEMPTS) {
            unblockTime = Instant.now().plusMillis(
                    (long) Math.pow(2.0,
                            (incorrectLoginAttempts - MAX_LOGIN_ATTEMPTS)) * THIRTY_SECONDS_TO_MILLIS);
        }
    }

    public final void resetIncorrectLoginAttempts() {
        incorrectLoginAttempts = 0;
        unblockTime = Instant.now().minusSeconds(5L);
    }
}
