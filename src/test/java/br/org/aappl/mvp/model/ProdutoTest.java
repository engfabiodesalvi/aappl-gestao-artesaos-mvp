package br.org.aappl.mvp.model;

import br.org.aappl.mvp.service.RegraDeNegocioException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProdutoTest {

    private Produto produto(int estoque) {
        return new Produto("Geleia", new BigDecimal("18.00"), estoque,
                           new Artesao("Dona Clara", "(16) 99123-4567"));
    }

    @Test
    void baixaReduzEstoque() {
        Produto p = produto(6);
        p.baixar(2);
        assertEquals(4, p.getEstoque());
        assertFalse(p.isEsgotado());
    }

    @Test
    void baixaDoTotalMarcaComoEsgotado() {
        Produto p = produto(2);
        p.baixar(2);
        assertEquals(0, p.getEstoque());
        assertTrue(p.isEsgotado());
    }

    @Test
    void nuncaPermiteEstoqueNegativo() {
        Produto p = produto(1);
        assertThrows(RegraDeNegocioException.class, () -> p.baixar(2));
        assertEquals(1, p.getEstoque());
    }
}
