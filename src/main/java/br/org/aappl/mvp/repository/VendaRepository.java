package br.org.aappl.mvp.repository;

import br.org.aappl.mvp.model.ItemVenda;
import br.org.aappl.mvp.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    @Query("""
        select i from ItemVenda i
        join fetch i.produto p
        join fetch p.artesao
        where i.venda.dataHora >= :ini and i.venda.dataHora < :fim""")
    List<ItemVenda> itensDoPeriodo(@Param("ini") LocalDateTime ini,
                                   @Param("fim") LocalDateTime fim);

    @Query("select coalesce(sum(v.total), 0) from Venda v "
         + "where v.dataHora >= :ini and v.dataHora < :fim")
    BigDecimal somaTotais(@Param("ini") LocalDateTime ini,
                          @Param("fim") LocalDateTime fim);
}
