package br.org.aappl.mvp.service;

import br.org.aappl.mvp.model.Artesao;
import br.org.aappl.mvp.model.FormaPagamento;
import br.org.aappl.mvp.model.Produto;
import br.org.aappl.mvp.model.Venda;
import br.org.aappl.mvp.repository.ProdutoRepository;
import br.org.aappl.mvp.repository.VendaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock ProdutoRepository produtos;
    @Mock VendaRepository vendas;
    @InjectMocks VendaService service;

    private final Artesao clara = new Artesao("Dona Clara", "(16) 99123-4567");

    @Test
    void registraVendaCalculaTotalEBaixaEstoque() {
        Produto geleia = new Produto("Geleia", new BigDecimal("18.00"), 6, clara);
        when(produtos.findByIdForUpdate(1L)).thenReturn(Optional.of(geleia));
        when(vendas.save(any(Venda.class))).thenAnswer(i -> i.getArgument(0));

        Venda venda = service.registrar(Map.of(1L, 2), FormaPagamento.PIX, "voluntario");

        assertEquals(new BigDecimal("36.00"), venda.getTotal());
        assertEquals(4, geleia.getEstoque());
        assertEquals(FormaPagamento.PIX, venda.getForma());
    }

    @Test
    void somaItensDeProdutosDiferentes() {
        Produto geleia = new Produto("Geleia", new BigDecimal("18.00"), 6, clara);
        Produto cesto = new Produto("Cesto", new BigDecimal("45.00"), 2, clara);
        when(produtos.findByIdForUpdate(1L)).thenReturn(Optional.of(geleia));
        when(produtos.findByIdForUpdate(2L)).thenReturn(Optional.of(cesto));
        when(vendas.save(any(Venda.class))).thenAnswer(i -> i.getArgument(0));

        Venda venda = service.registrar(Map.of(1L, 1, 2L, 1), FormaPagamento.DINHEIRO, "voluntario");

        assertEquals(new BigDecimal("63.00"), venda.getTotal());
        assertEquals(2, venda.getItens().size());
    }

    @Test
    void rejeitaQuantidadeMaiorQueOEstoque() {
        Produto cesto = new Produto("Cesto", new BigDecimal("45.00"), 2, clara);
        when(produtos.findByIdForUpdate(2L)).thenReturn(Optional.of(cesto));

        assertThrows(RegraDeNegocioException.class,
            () -> service.registrar(Map.of(2L, 3), FormaPagamento.DINHEIRO, "voluntario"));

        assertEquals(2, cesto.getEstoque());
        verify(vendas, never()).save(any());
    }

    @Test
    void rejeitaVendaSemItens() {
        assertThrows(RegraDeNegocioException.class,
            () -> service.registrar(Map.of(1L, 0), FormaPagamento.PIX, "voluntario"));
        verify(vendas, never()).save(any());
    }

    @Test
    void rejeitaVendaSemFormaDePagamento() {
        assertThrows(RegraDeNegocioException.class,
            () -> service.registrar(Map.of(1L, 1), null, "voluntario"));
    }

    @Test
    void rejeitaProdutoInexistente() {
        when(produtos.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        assertThrows(RegraDeNegocioException.class,
            () -> service.registrar(Map.of(99L, 1), FormaPagamento.PIX, "voluntario"));
    }
}
