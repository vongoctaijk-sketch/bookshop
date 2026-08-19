package com.example.Bookshop.service;

import java.util.List;
import java.util.Optional;

public interface CrudService<T, ID> {

    List<T> getAll();

    Optional<T> getById(ID id);

    T create(T entity);

    Optional<T> update(ID id, T entity);

    boolean delete(ID id);
}
