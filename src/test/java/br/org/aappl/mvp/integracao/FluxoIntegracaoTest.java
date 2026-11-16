package br.org.aappl.mvp.integracao;

import br.org.aappl.mvp.model.FormaPagamento;
import br.org.aappl.mvp.model.Produto;
import br.org.aappl.mvp.repository.ProdutoRepository;
import br.org.aappl.mvp.repository.VendaRepository;
import br.org.aappl.mvp.service.FechamentoService;
import br.org.aappl.mvp.service.RelatorioFechamento;
import br.org.aappl.mvp.service.VendaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Integração com banco H2 isolado e dados de demonstração do perfil "dev". Cada teste faz rollback. */
@SpringBootTest(properties =
    "spring.datasource.url=jdbc:h2:mem:integracao;MODE=PostgreSQL;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class FluxoIntegracaoTest {

    @Autowired MockMvc mvc;
    @Autowired ProdutoRepository produtos;
    @Autowired VendaRepository vendas;
    @Autowired VendaService vendaService;
    @Autowired FechamentoService fechamentoService;

    private Produto produto(String prefixo) {
        return produtos.listarAtivos().stream()
            .filter(p -> p.getNome().startsWith(prefixo))
            .findFirst().orElseThrow();
    }

    @Test
    void loginValidoRedirecionaParaOCaixa() throws Exception {
        mvc.perform(post("/login")
                .param("username", "voluntario.balcao@aappl.org.br")
                .param("password", "Troque#123")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/caixa"));
    }

    @Test
    void loginComSenhaErradaVoltaParaTelaDeLoginComErro() throws Exception {
        mvc.perform(post("/login")
                .param("username", "voluntario.balcao@aappl.org.br")
                .param("password", "senha-errada")
                .with(csrf()))
            .andExpect(redirectedUrl("/login?erro"));
    }

    @Test
    @WithMockUser(username = "voluntario", roles = "VOLUNTARIO")
    void caixaListaProdutosEMostraItemEsgotado() throws Exception {
        mvc.perform(get("/caixa"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Geleia de Jabuticaba")))
            .andExpect(content().string(containsString("Estoque esgotado")));
    }

    @Test
    @WithMockUser(username = "voluntario", roles = "VOLUNTARIO")
    void vendaPeloCaixaBaixaOEstoque() throws Exception {
        Produto geleia = produto("Geleia");

        mvc.perform(post("/caixa/finalizar").with(csrf())
                .param("forma", "PIX")
                .param("quantidades[" + geleia.getId() + "]", "2"))
            .andExpect(status().is3xxRedirection())
            .andExpect(flash().attribute("sucesso", "Venda registrada. Estoque atualizado."));

        assertEquals(4, produtos.findById(geleia.getId()).orElseThrow().getEstoque());
    }

    @Test
    @WithMockUser(username = "voluntario", roles = "VOLUNTARIO")
    void vendaAcimaDoEstoqueEhRecusadaESemAlterarSaldo() throws Exception {
        Produto cesto = produto("Cesto");

        mvc.perform(post("/caixa/finalizar").with(csrf())
                .param("forma", "DINHEIRO")
                .param("quantidades[" + cesto.getId() + "]", "3"))
            .andExpect(flash().attributeExists("erro"));

        assertEquals(2, produtos.findById(cesto.getId()).orElseThrow().getEstoque());
    }

    @Test
    @WithMockUser(username = "voluntario", roles = "VOLUNTARIO")
    void vendaSemFormaDePagamentoEhRecusada() throws Exception {
        Produto geleia = produto("Geleia");

        mvc.perform(post("/caixa/finalizar").with(csrf())
                .param("quantidades[" + geleia.getId() + "]", "1"))
            .andExpect(flash().attribute("erro", "Selecione a forma de pagamento."));
    }

    @Test
    void consultasDoRepositorioSomamAsVendasDoDia() {
        Produto geleia = produto("Geleia");
        vendaService.registrar(Map.of(geleia.getId(), 2), FormaPagamento.PIX, "voluntario");

        var ini = LocalDate.now().atStartOfDay();
        var fim = LocalDate.now().plusDays(1).atStartOfDay();

        assertEquals(1, vendas.itensDoPeriodo(ini, fim).size());
        assertEquals(0, new BigDecimal("36.00").compareTo(vendas.somaTotais(ini, fim)));
    }

    @Test
    void fechamentoConsolidaVendasReaisComRepasseDeNoventaPorCento() {
        Produto geleia = produto("Geleia");
        vendaService.registrar(Map.of(geleia.getId(), 2), FormaPagamento.PIX, "voluntario");

        RelatorioFechamento r = fechamentoService.consolidar(LocalDate.now());

        assertEquals(0, new BigDecimal("36.00").compareTo(r.bruto()));
        assertEquals(0, new BigDecimal("32.40").compareTo(r.liquido()));
        assertEquals(0, new BigDecimal("3.60").compareTo(r.taxa()));
        assertEquals("Dona Clara", r.repasses().get(0).nome());
        assertTrue(r.conferido());
    }

    @Test
    @WithMockUser(roles = "COORDENADORA")
    void coordenadoraAcessaTelaDeFechamento() throws Exception {
        mvc.perform(get("/fechamento"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Fechamento da feira")));
    }
}
