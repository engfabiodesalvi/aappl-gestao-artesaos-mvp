package br.org.aappl.mvp.controller;

import br.org.aappl.mvp.repository.ProdutoRepository;
import br.org.aappl.mvp.service.CaixaForm;
import br.org.aappl.mvp.service.RegraDeNegocioException;
import br.org.aappl.mvp.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

@Controller
public class CaixaController {

    private final ProdutoRepository produtos;
    private final VendaService vendas;

    public CaixaController(ProdutoRepository produtos, VendaService vendas) {
        this.produtos = produtos;
        this.vendas = vendas;
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/caixa";
    }

    @GetMapping("/caixa")
    public String caixa(Model model) {
        model.addAttribute("produtos", produtos.listarAtivos());
        return "caixa";
    }

    @PostMapping("/caixa/finalizar")
    public String finalizar(@Valid @ModelAttribute CaixaForm form, BindingResult br,
                            Principal usuario, RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("erro", "Selecione a forma de pagamento.");
            return "redirect:/caixa";
        }
        try {
            vendas.registrar(form.getQuantidades(), form.getForma(), usuario.getName());
            ra.addFlashAttribute("sucesso", "Venda registrada. Estoque atualizado.");
        } catch (RegraDeNegocioException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/caixa";
    }
}
