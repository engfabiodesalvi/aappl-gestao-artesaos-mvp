package br.org.aappl.mvp.service;

import br.org.aappl.mvp.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarios;

    public UsuarioDetailsService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return usuarios.findByUsername(username)
            .filter(u -> u.isAtivo())
            .map(u -> User.withUsername(u.getUsername())
                .password(u.getSenhaHash())
                .roles(u.getPerfil().name())
                .build())
            .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas"));
    }
}
