package com.kiura.api.services;

import java.util.List;

public interface IService <T, E> {

    public void actualizar(E e);

    public T buscarPorId(Long id);

    public void crear(E e);

    public void borrarPorId(Long id);

    public List<T> listarTodos();

}
