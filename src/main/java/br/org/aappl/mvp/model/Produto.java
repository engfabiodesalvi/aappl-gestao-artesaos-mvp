package br.org.aappl.mvp.model;

import br.org.aappl.mvp.service.RegraDeNegocioException;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private int estoque;

    private boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Artesao artesao;

    protected Produto() {}

    public Produto(String nome, BigDecimal preco, int estoque, Artesao artesao) {
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
        this.artesao = artesao;
    }

    /** Regra de negócio: nunca permite estoque negativo (RF03). */
    public void baixar(int quantidade) {
        if (quantidade > estoque) {
            throw new RegraDeNegocioException(
                "Estoque insuficiente para \"" + nome + "\": restam " + estoque + " un.");
        }
        this.estoque -= quantidade;
    }

    public boolean isEsgotado() { return estoque <= 0; }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public BigDecimal getPreco() { return preco; }
    public int getEstoque() { return estoque; }
    public boolean isAtivo() { return ativo; }
    public Artesao getArtesao() { return artesao; }
}
