package br.org.aappl.mvp.service;

import br.org.aappl.mvp.model.FormaPagamento;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public class CaixaForm {

    private Map<Long, Integer> quantidades = new HashMap<>();

    @NotNull(message = "Selecione a forma de pagamento.")
    private FormaPagamento forma;

    public Map<Long, Integer> getQuantidades() { return quantidades; }
    public void setQuantidades(Map<Long, Integer> quantidades) { this.quantidades = quantidades; }
    public FormaPagamento getForma() { return forma; }
    public void setForma(FormaPagamento forma) { this.forma = forma; }
}
