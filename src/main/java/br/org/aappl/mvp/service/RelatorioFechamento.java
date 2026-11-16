package br.org.aappl.mvp.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RelatorioFechamento(LocalDate data,
                                  BigDecimal bruto,
                                  BigDecimal taxa,
                                  BigDecimal liquido,
                                  boolean conferido,
                                  List<Repasse> repasses) {

    public record Repasse(String nome, String chavePix, int itens, BigDecimal valor) {}
}
