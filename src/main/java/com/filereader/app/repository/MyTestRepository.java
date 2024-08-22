package com.filereader.app.repository;

import com.filereader.app.entity.MyTest;
import org.springframework.stereotype.Repository;

/**
 * MyTestRepository interface is used to perform CRUD operations on the MyTest entity.
 */
@Repository
public interface MyTestRepository extends BaseRepository<MyTest> {
}
