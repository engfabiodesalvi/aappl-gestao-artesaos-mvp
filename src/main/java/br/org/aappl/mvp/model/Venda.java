package br.org.aappl.mvp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaPagamento forma;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(nullable = false)
    private String operador;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL)
    private List<ItemVenda> itens = new ArrayList<>();

    protected Venda() {}

    public Venda(FormaPagamento forma, String operador) {
        this.forma = forma;
        this.operador = operador;
    }

    public void adicionar(ItemVenda item) {
        item.setVenda(this);
        itens.add(item);
        total = total.add(item.subtotal());
    }

    public Long getId() { return id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public FormaPagamento getForma() { return forma; }
    public BigDecimal getTotal() { return total; }
    public String getOperador() { return operador; }
    public List<ItemVenda> getItens() { return itens; }
}
