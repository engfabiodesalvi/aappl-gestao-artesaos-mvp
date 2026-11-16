package br.org.aappl.mvp.config;

import br.org.aappl.mvp.model.*;
import br.org.aappl.mvp.repository.ArtesaoRepository;
import br.org.aappl.mvp.repository.ProdutoRepository;
import br.org.aappl.mvp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;

/** Dados de demonstração. Só roda com o perfil "dev", nunca em produção. */
@Configuration
@Profile("dev")
public class DataSeeder {

    @Bean
    CommandLineRunner seed(UsuarioRepository usuarios, ArtesaoRepository artesaos,
                           ProdutoRepository produtos, PasswordEncoder encoder,
                           @Value("${app.seed.senha:Troque#123}") String senha) {
        return args -> {
            if (usuarios.count() == 0) {
                usuarios.save(new Usuario("coordenadora@aappl.org.br", encoder.encode(senha), Perfil.COORDENADORA));
                usuarios.save(new Usuario("voluntario.balcao@aappl.org.br", encoder.encode(senha), Perfil.VOLUNTARIO));
            }

            if (produtos.count() == 0) {
                Artesao clara = artesaos.save(new Artesao("Dona Clara", "(16) 99123-4567"));
                Artesao antonio = artesaos.save(new Artesao("Seu Antônio", "antonio.arte@email.com"));
                Artesao maria = artesaos.save(new Artesao("Dona Maria", "98765432-chave-aleatoria"));

                produtos.save(new Produto("Geleia de Jabuticaba 250 g", new BigDecimal("18.00"), 6, clara));
                produtos.save(new Produto("Cesto de Palha Trançada", new BigDecimal("45.00"), 2, antonio));
                produtos.save(new Produto("Doce de Leite Caseiro 400 g", new BigDecimal("22.00"), 0, maria));
            }
        };
    }
}
