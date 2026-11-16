package br.org.aappl.mvp.service;

import br.org.aappl.mvp.model.Perfil;
import br.org.aappl.mvp.model.Usuario;
import br.org.aappl.mvp.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock UsuarioRepository usuarios;
    @InjectMocks UsuarioDetailsService service;

    @Test
    void carregaUsuarioComHashEPerfil() {
        when(usuarios.findByUsername("coord@aappl.org.br"))
            .thenReturn(Optional.of(new Usuario("coord@aappl.org.br", "hash-bcrypt", Perfil.COORDENADORA)));

        UserDetails d = service.loadUserByUsername("coord@aappl.org.br");

        assertEquals("hash-bcrypt", d.getPassword());
        assertTrue(d.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_COORDENADORA")));
    }

    @Test
    void rejeitaUsuarioInexistenteComMensagemGenerica() {
        when(usuarios.findByUsername("x")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("x"));
    }
}
