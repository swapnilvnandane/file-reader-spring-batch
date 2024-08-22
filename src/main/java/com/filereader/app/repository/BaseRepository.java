package com.filereader.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * BaseRepository interface is used to perform CRUD operations on the entity.
 * @param <T> a T object.
 */
@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T, String> {
}
