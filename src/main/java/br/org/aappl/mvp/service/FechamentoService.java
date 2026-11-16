package br.org.aappl.mvp.service;

import br.org.aappl.mvp.model.Artesao;
import br.org.aappl.mvp.model.ItemVenda;
import br.org.aappl.mvp.repository.VendaRepository;
import br.org.aappl.mvp.service.RelatorioFechamento.Repasse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FechamentoService {

    /** A associação retém 10%; o artesão recebe 90% do que vendeu. */
    static final BigDecimal FATOR_REPASSE = new BigDecimal("0.90");

    private final VendaRepository vendas;

    public FechamentoService(VendaRepository vendas) {
        this.vendas = vendas;
    }

    @Transactional(readOnly = true)
    public RelatorioFechamento consolidar(LocalDate dia) {
        LocalDateTime ini = dia.atStartOfDay();
        LocalDateTime fim = dia.plusDays(1).atStartOfDay();

        Map<Artesao, Integer> itensPorArtesao = new HashMap<>();
        Map<Artesao, BigDecimal> brutoPorArtesao = new HashMap<>();

        for (ItemVenda item : vendas.itensDoPeriodo(ini, fim)) {
            Artesao a = item.getProduto().getArtesao();
            itensPorArtesao.merge(a, item.getQuantidade(), Integer::sum);
            brutoPorArtesao.merge(a, item.subtotal(), BigDecimal::add);
        }

        List<Repasse> repasses = new ArrayList<>();
        BigDecimal bruto = BigDecimal.ZERO;
        BigDecimal liquido = BigDecimal.ZERO;

        for (Map.Entry<Artesao, BigDecimal> e : brutoPorArtesao.entrySet()) {
            BigDecimal valor = e.getValue()
                .multiply(FATOR_REPASSE)
                .setScale(2, RoundingMode.HALF_UP);
            repasses.add(new Repasse(e.getKey().getNome(), e.getKey().getChavePix(),
                                     itensPorArtesao.get(e.getKey()), valor));
            bruto = bruto.add(e.getValue());
            liquido = liquido.add(valor);
        }
        repasses.sort(Comparator.comparing(Repasse::nome));

        // taxa = bruto - líquido: a soma dos repasses + taxa fecha centavo a centavo
        BigDecimal taxa = bruto.subtract(liquido);

        // conferência: soma dos itens deve bater com a soma dos totais gravados nas vendas
        boolean conferido = bruto.compareTo(vendas.somaTotais(ini, fim)) == 0;

        return new RelatorioFechamento(dia, bruto, taxa, liquido, conferido, repasses);
    }
}
