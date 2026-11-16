package br.org.aappl.mvp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TelasWebTest {

    @Autowired MockMvc mvc;

    @Test
    void loginEhPublico() throws Exception {
        mvc.perform(get("/login")).andExpect(status().isOk());
    }

    @Test
    void caixaExigeAutenticacao() throws Exception {
        mvc.perform(get("/caixa")).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "VOLUNTARIO")
    void voluntarioAcessaCaixa() throws Exception {
        mvc.perform(get("/caixa")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "VOLUNTARIO")
    void voluntarioNaoAcessaFechamento() throws Exception {
        mvc.perform(get("/fechamento")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "VOLUNTARIO")
    void postSemTokenCsrfEhBloqueado() throws Exception {
        mvc.perform(post("/caixa/finalizar")).andExpect(status().isForbidden());
    }
}
