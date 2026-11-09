package com.wedding.common.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface defining standard CRUD operations.
 * This interface demonstrates Abstraction — consumers depend on this
 * contract, not on concrete file/DB implementations.
 *
 * @param <T> The entity type
 * @param <ID> The ID type
 *
 * @author Common Module
 */
public interface Repository<T, ID> {

 /** Saves (creates or updates) an entity. */
 void save(T entity);

 /** Finds an entity by its unique ID. */
 Optional<T> findById(ID id);

 /** Returns all entities. */
 List<T> findAll();

 /** Deletes an entity by ID. */
 boolean deleteById(ID id);

 /** Returns the count of all entities. */
 int count();
}

