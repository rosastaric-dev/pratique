package com.sistema.eventos.model;

public enum CategoriaEvento {
    ESPORTIVO("Esportivo"),
    SHOW("Show"),
    CULTURAL("Cultural"),
    OUTROS("Outros");

    private final String descricao;

    CategoriaEvento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static CategoriaEvento fromInt(int value) {
        return values()[value];
    }

    public static CategoriaEvento fromName(String categoria) {
        try{
            return valueOf(categoria.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
            return CategoriaEvento.OUTROS;
    }
}
