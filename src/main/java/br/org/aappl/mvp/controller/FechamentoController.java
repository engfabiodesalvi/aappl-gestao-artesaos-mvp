package br.org.aappl.mvp.controller;

import br.org.aappl.mvp.service.FechamentoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;

@Controller
public class FechamentoController {

    private final FechamentoService service;

    public FechamentoController(FechamentoService service) {
        this.service = service;
    }

    @GetMapping("/fechamento")
    public String fechamento(@RequestParam(required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
                             Model model) {
        model.addAttribute("r", service.consolidar(data != null ? data : LocalDate.now()));
        return "fechamento";
    }
}
