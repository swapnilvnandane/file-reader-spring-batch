package com.filereader.app.processor;

import com.filereader.app.entity.MyTest;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

@Service
public class ValidatingItemProcessor implements ItemProcessor<MyTest, MyTest> {

    @Override
    public MyTest process(MyTest item) throws Exception {
        if(item.getName().length() > 10) {
            throw new IllegalArgumentException("Name is too long");
        }
        return item;
    }
}
