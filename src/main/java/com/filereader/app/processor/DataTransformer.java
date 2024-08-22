package com.filereader.app.processor;

import com.filereader.app.entity.MyTest;
import com.filereader.app.repository.MyTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * DataTransformer class is used to write the data to database table.
 */
@RequiredArgsConstructor
@Component
public class DataTransformer implements ItemWriter<MyTest> {

    /** The MyTestRepository class is used to perform CRUD operations on the MyTest entity. **/
    private final MyTestRepository myTestRepository;

    /**
     * The write method is used to write the data to database table.
     * @param chunk a {@link Chunk} object.
     * @throws Exception an {@link Exception} object.
     */
    @Transactional
    @Override
    public void write(Chunk<? extends MyTest> chunk) throws Exception {
        myTestRepository.saveAll(chunk.getItems());
    }
}
