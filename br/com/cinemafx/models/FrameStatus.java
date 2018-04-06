package br.com.cinemafx.models;

public class FrameStatus {

    Status status = Status.Visualizando;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {Carregando, Visualizando, Alterando, Adicionando}
}