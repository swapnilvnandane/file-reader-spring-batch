package com.filereader.app.processor;

import com.filereader.app.entity.MyTest;
import com.filereader.app.repository.MyTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class DataTransformer implements ItemWriter<MyTest> {

    private final MyTestRepository myTestRepository;

    @Transactional
    @Override
    public void write(Chunk<? extends MyTest> chunk) throws Exception {
        myTestRepository.saveAll(chunk.getItems());
    }
}
