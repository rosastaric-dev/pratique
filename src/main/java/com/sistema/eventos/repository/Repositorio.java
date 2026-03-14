package com.sistema.eventos.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Repositorio <T>{
    private  final List<T> lista = new ArrayList<>();

    public void add(T item){
        this.lista.add(item);
    }

    public List<T> lista(){
       return Collections.unmodifiableList(this.lista);
    }

    public T remover(T item){
        int i = this.lista.indexOf(item);
        return this.lista.get(i);
    }

}
