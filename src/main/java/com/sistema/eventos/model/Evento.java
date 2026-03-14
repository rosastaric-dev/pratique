package com.sistema.eventos.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Evento {
    private int id;
    private String nome;
    private String endereco;
    private CategoriaEvento categoria;
    private LocalDateTime horario;
    private String descricao;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Evento(int id, String nome, String endereco, CategoriaEvento categoria,
                  LocalDateTime horario, String descricao) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.categoria = categoria;
        this.horario = horario;
        this.descricao = descricao;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public CategoriaEvento getCategoria() { return categoria; }
    public void setCategoria(CategoriaEvento categoria) { this.categoria = categoria; }

    public LocalDateTime getHorario() { return horario; }
    public void setHorario(LocalDateTime horario) { this.horario = horario; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    // Formatar para salvar em arquivo
    public String toFileString() {
        return String.format("%d|%s|%s|%d|%s|%s",
                id, nome, endereco, categoria.ordinal(),
                horario.format(FORMATTER), descricao);
    }

    // Criar evento a partir de string do arquivo
    public static Evento fromFileString(String linha) {
        String[] partes = linha.split(";");
        return new Evento(
                Integer.parseInt(partes[0]),
                partes[1],
                partes[2],
                CategoriaEvento.fromName(partes[3]),
                LocalDateTime.parse(partes[4], FORMATTER),
                partes[5]
        );
    }

    // Verificar se evento está acontecendo agora
    public boolean estaAcontecendoAgora() {
        LocalDateTime agora = LocalDateTime.now();
        return !horario.isAfter(agora) && horario.plusHours(2).isAfter(agora);
    }

    // Verificar se evento já passou
    public boolean jaAconteceu() {
        return horario.plusHours(2).isBefore(LocalDateTime.now());
    }

    @Override
    public String toString() {
        String status;
        if (estaAcontecendoAgora()) {
            status = "ACONTECENDO AGORA";
        } else if (jaAconteceu()) {
            status = "Passado";
        } else {
            status = "Futuro";
        }

        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("ID: %d | %s | %s | %s | %s",
                id, nome, categoria.getDescricao(),
                horario.format(displayFormatter), status);
    }
}
