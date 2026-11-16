package br.org.aappl.mvp.repository;

import br.org.aappl.mvp.model.Artesao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtesaoRepository extends JpaRepository<Artesao, Long> {
}
