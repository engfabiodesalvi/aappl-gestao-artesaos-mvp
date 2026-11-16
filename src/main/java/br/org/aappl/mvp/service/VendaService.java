package br.org.aappl.mvp.service;

import br.org.aappl.mvp.model.FormaPagamento;
import br.org.aappl.mvp.model.ItemVenda;
import br.org.aappl.mvp.model.Produto;
import br.org.aappl.mvp.model.Venda;
import br.org.aappl.mvp.repository.ProdutoRepository;
import br.org.aappl.mvp.repository.VendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

@Service
public class VendaService {

    private final ProdutoRepository produtos;
    private final VendaRepository vendas;

    public VendaService(ProdutoRepository produtos, VendaRepository vendas) {
        this.produtos = produtos;
        this.vendas = vendas;
    }

    /** Grava a venda e baixa o estoque na mesma transação (atômico). */
    @Transactional
    public Venda registrar(Map<Long, Integer> quantidades, FormaPagamento forma, String operador) {
        if (forma == null) {
            throw new RegraDeNegocioException("Selecione a forma de pagamento.");
        }
        Venda venda = new Venda(forma, operador);

        quantidades.entrySet().stream()
            .filter(e -> e.getValue() != null && e.getValue() > 0)
            .sorted(Map.Entry.comparingByKey())          // ordem fixa evita deadlock
            .forEach(e -> {
                Produto p = produtos.findByIdForUpdate(e.getKey())
                    .orElseThrow(() -> new RegraDeNegocioException("Produto não encontrado."));
                p.baixar(e.getValue());
                venda.adicionar(new ItemVenda(p, e.getValue()));
            });

        if (venda.getItens().isEmpty()) {
            throw new RegraDeNegocioException("Adicione ao menos um item ao carrinho.");
        }
        return vendas.save(venda);
    }
}
