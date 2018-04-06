package br.com.cinemafx.models;

import java.sql.Timestamp;

public class Sessao {

    private int codSessao;
    private Sala sala;
    private Filme filme;
    private Exibicao exibicao;
    private Timestamp dataHoraExib;
    private Timestamp dataHoraFim;

    public Sessao(int codSessao, Sala sala, Filme filme, Exibicao exibicao, Timestamp dataHoraExib) {
        this.codSessao = codSessao;
        this.sala = sala;
        this.filme = filme;
        this.exibicao = exibicao;
        this.dataHoraExib = dataHoraExib;
    }

    @Override
    public String toString() {
        return "Sessao{" +
                "codSessao=" + codSessao +
                ", sala=" + sala +
                ", filme=" + filme +
                ", exibicao=" + exibicao +
                ", dataHoraExib=" + dataHoraExib +
                ", dataHoraFim=" + dataHoraFim +
                '}';
    }

    public int getCodSessao() {
        return codSessao;
    }

    public void setCodSessao(int codSessao) {
        this.codSessao = codSessao;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Filme getFilme() {
        return filme;
    }

    public void setFilme(Filme filme) {
        this.filme = filme;
    }

    public Exibicao getExibicao() {
        return exibicao;
    }

    public void setExibicao(Exibicao exibicao) {
        this.exibicao = exibicao;
    }

    public Timestamp getDataHoraExib() {
        return dataHoraExib;
    }

    public void setDataHoraExib(Timestamp dataHoraExib) {
        this.dataHoraExib = dataHoraExib;
        this.setDataHoraFim(Timestamp.valueOf(this.getDataHoraExib().toLocalDateTime().plusMinutes(filme.getMinFilme().longValue())));
    }

    public Timestamp getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(Timestamp dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }
}
