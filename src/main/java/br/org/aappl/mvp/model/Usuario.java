package br.org.aappl.mvp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil;

    private boolean ativo = true;

    protected Usuario() {}

    public Usuario(String username, String senhaHash, Perfil perfil) {
        this.username = username;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getSenhaHash() { return senhaHash; }
    public Perfil getPerfil() { return perfil; }
    public boolean isAtivo() { return ativo; }
}
