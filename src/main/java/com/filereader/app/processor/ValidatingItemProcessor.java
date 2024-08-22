package com.filereader.app.processor;

import com.filereader.app.entity.MyTest;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

/**
 * ValidatingItemProcessor class is used to validate the item.
 */
@Service
public class ValidatingItemProcessor implements ItemProcessor<MyTest, MyTest> {

    /**
     * The process method is used to validate the item.
     * @param item a {@link MyTest} object.
     * @return a {@link MyTest} object.
     * @throws Exception an {@link Exception} object.
     */
    @Override
    public MyTest process(MyTest item) throws Exception {
        if(item.getName().length() > 10) {
            throw new IllegalArgumentException("Name is too long");
        }
        return item;
    }
}
