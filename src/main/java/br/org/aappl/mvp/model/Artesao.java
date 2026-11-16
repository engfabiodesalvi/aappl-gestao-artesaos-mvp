package br.org.aappl.mvp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "artesaos")
public class Artesao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "chave_pix", nullable = false)
    private String chavePix;

    protected Artesao() {}

    public Artesao(String nome, String chavePix) {
        this.nome = nome;
        this.chavePix = chavePix;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getChavePix() { return chavePix; }
}
