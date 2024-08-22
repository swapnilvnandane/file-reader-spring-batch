package com.filereader.app.processor;

import com.filereader.app.entity.MyTest;
import jakarta.annotation.PostConstruct;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

/**
 * CustomLineMapper class is used to map the line from the input file to the MyTest object.
 */
@Component
public class CustomLineMapper implements LineMapper<MyTest> {

    @Value("${com.file.separator}")
    private String delimiter;

    /** The tokenizer is used to tokenize the line. **/
    private DelimitedLineTokenizer tokenizer;

    /**
     * The init method is used to initialize the tokenizer object and assign delimiter value.
     */
    @PostConstruct
    public void init() {
        tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(delimiter);
    }

    /**
     * The mapLine method is used to map the line from the input file to the MyTest object.
     * @param line a {@link String} object.
     * @param lineNumber an {@link int} value.
     * @return a {@link MyTest} object.
     * @throws BindException a {@link BindException} object.
     */
    @Override
    public MyTest mapLine(String line, int lineNumber) throws BindException {
        FieldSet fieldSet = tokenizer.tokenize(line);
        MyTest record = new MyTest();
        record.setName(fieldSet.readString(0));
        record.setCity(fieldSet.readString(1));
        return record;
    }
}
