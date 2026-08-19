package com.example.Bookshop.service.impl;

import com.example.Bookshop.service.CrudService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public abstract class AbstractCrudServiceImpl<T, ID> implements CrudService<T, ID> {

    protected abstract JpaRepository<T, ID> getRepository();

    protected abstract void setId(T entity, ID id);

    protected void copyForUpdate(T currentEntity, T newEntity) {
    }

    @Override
    public List<T> getAll() {
        return getRepository().findAll();
    }

    @Override
    public Optional<T> getById(ID id) {
        return getRepository().findById(id);
    }

    @Override
    public T create(T entity) {
        setId(entity, null);
        return getRepository().save(entity);
    }

    @Override
    public Optional<T> update(ID id, T entity) {
        return getRepository().findById(id)
                .map(currentEntity -> {
                    copyForUpdate(currentEntity, entity);
                    setId(entity, id);
                    return getRepository().save(entity);
                });
    }

    @Override
    public boolean delete(ID id) {
        if (!getRepository().existsById(id)) {
            return false;
        }

        getRepository().deleteById(id);
        return true;
    }
}
