package com.sistema.eventos.model;

import java.time.LocalDateTime;

public class Participacao {
    private int usuarioId;
    private int eventoId;
    private LocalDateTime dataConfirmacao;

    public Participacao(int usuarioId, int eventoId, LocalDateTime dataConfirmacao) {
        this.usuarioId = usuarioId;
        this.eventoId = eventoId;
        this.dataConfirmacao = dataConfirmacao;
    }

    // Getters
    public int getUsuarioId() { return usuarioId; }
    public int getEventoId() { return eventoId; }
    public LocalDateTime getDataConfirmacao() { return dataConfirmacao; }
}
