package br.org.aappl.mvp.service;

import br.org.aappl.mvp.model.Artesao;
import br.org.aappl.mvp.model.ItemVenda;
import br.org.aappl.mvp.model.Produto;
import br.org.aappl.mvp.repository.VendaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FechamentoServiceTest {

    @Mock VendaRepository vendas;
    @InjectMocks FechamentoService service;

    private final Artesao clara = new Artesao("Dona Clara", "(16) 99123-4567");
    private final Artesao antonio = new Artesao("Seu Antônio", "antonio.arte@email.com");

    private Produto produto(String nome, String preco, Artesao dono) {
        return new Produto(nome, new BigDecimal(preco), 99, dono);
    }

    @Test
    void calculaRepasseDeNoventaPorCentoParaCadaArtesao() {
        // Clara: 15 x 18,00 = 270,00 | Antônio: 8 x 45,00 = 360,00
        when(vendas.itensDoPeriodo(any(), any())).thenReturn(List.of(
            new ItemVenda(produto("Geleia", "18.00", clara), 15),
            new ItemVenda(produto("Cesto", "45.00", antonio), 8)));
        when(vendas.somaTotais(any(), any())).thenReturn(new BigDecimal("630.00"));

        RelatorioFechamento r = service.consolidar(LocalDate.of(2026, 11, 13));

        assertEquals(new BigDecimal("630.00"), r.bruto());
        assertEquals(new BigDecimal("567.00"), r.liquido());
        assertEquals(new BigDecimal("63.00"), r.taxa());
        assertEquals(2, r.repasses().size());
        assertEquals("Dona Clara", r.repasses().get(0).nome());
        assertEquals(new BigDecimal("243.00"), r.repasses().get(0).valor());
        assertEquals(15, r.repasses().get(0).itens());
        assertEquals("Seu Antônio", r.repasses().get(1).nome());
        assertEquals(new BigDecimal("324.00"), r.repasses().get(1).valor());
        assertTrue(r.conferido());
    }

    @Test
    void agregaItensDoMesmoArtesaoEmUmUnicoRepasse() {
        // 3 geleias (54,00) + 2 doces (40,00) da mesma produtora = 94,00 -> repasse 84,60
        when(vendas.itensDoPeriodo(any(), any())).thenReturn(List.of(
            new ItemVenda(produto("Geleia", "18.00", clara), 3),
            new ItemVenda(produto("Doce", "20.00", clara), 2)));
        when(vendas.somaTotais(any(), any())).thenReturn(new BigDecimal("94.00"));

        RelatorioFechamento r = service.consolidar(LocalDate.of(2026, 11, 13));

        assertEquals(1, r.repasses().size());
        assertEquals(5, r.repasses().get(0).itens());
        assertEquals(new BigDecimal("84.60"), r.repasses().get(0).valor());
    }

    @Test
    void valoresDaDemonstracaoDoCaixaBatemComATela() {
        // 3 geleias (54,00) + 2 cestos (90,00) = 144,00
        when(vendas.itensDoPeriodo(any(), any())).thenReturn(List.of(
            new ItemVenda(produto("Geleia", "18.00", clara), 3),
            new ItemVenda(produto("Cesto", "45.00", antonio), 2)));
        when(vendas.somaTotais(any(), any())).thenReturn(new BigDecimal("144.00"));

        RelatorioFechamento r = service.consolidar(LocalDate.of(2026, 11, 13));

        assertEquals(new BigDecimal("144.00"), r.bruto());
        assertEquals(new BigDecimal("14.40"), r.taxa());
        assertEquals(new BigDecimal("129.60"), r.liquido());
        assertEquals(new BigDecimal("48.60"), r.repasses().get(0).valor());
        assertEquals(new BigDecimal("81.00"), r.repasses().get(1).valor());
    }

    @Test
    void arredondaCentavosEFechaBrutoComRepasseMaisTaxa() {
        // 10,05 x 0,90 = 9,045 -> 9,05 (HALF_UP); taxa = 10,05 - 9,05 = 1,00
        when(vendas.itensDoPeriodo(any(), any())).thenReturn(List.of(
            new ItemVenda(produto("Sabonete", "10.05", clara), 1)));
        when(vendas.somaTotais(any(), any())).thenReturn(new BigDecimal("10.05"));

        RelatorioFechamento r = service.consolidar(LocalDate.of(2026, 11, 13));

        assertEquals(new BigDecimal("9.05"), r.liquido());
        assertEquals(new BigDecimal("1.00"), r.taxa());
        assertEquals(0, r.bruto().compareTo(r.liquido().add(r.taxa())));
    }

    @Test
    void sinalizaDivergenciaQuandoTotalDoCaixaNaoBate() {
        when(vendas.itensDoPeriodo(any(), any())).thenReturn(List.of(
            new ItemVenda(produto("Geleia", "18.00", clara), 15)));
        when(vendas.somaTotais(any(), any())).thenReturn(new BigDecimal("250.00"));

        assertFalse(service.consolidar(LocalDate.of(2026, 11, 13)).conferido());
    }

    @Test
    void relatorioVazioQuandoNaoHaVendas() {
        when(vendas.itensDoPeriodo(any(), any())).thenReturn(List.of());
        when(vendas.somaTotais(any(), any())).thenReturn(BigDecimal.ZERO);

        RelatorioFechamento r = service.consolidar(LocalDate.of(2026, 11, 13));

        assertTrue(r.repasses().isEmpty());
        assertEquals(0, r.bruto().compareTo(BigDecimal.ZERO));
        assertEquals(0, r.liquido().compareTo(BigDecimal.ZERO));
        assertTrue(r.conferido());
    }

    @Test
    void consultaSomenteOsLimitesDoDiaSolicitado() {
        LocalDate dia = LocalDate.of(2026, 11, 13);
        when(vendas.itensDoPeriodo(any(), any())).thenReturn(List.of());
        when(vendas.somaTotais(any(), any())).thenReturn(BigDecimal.ZERO);

        service.consolidar(dia);

        verify(vendas).itensDoPeriodo(dia.atStartOfDay(), dia.plusDays(1).atStartOfDay());
        verify(vendas).somaTotais(dia.atStartOfDay(), dia.plusDays(1).atStartOfDay());
    }
}
