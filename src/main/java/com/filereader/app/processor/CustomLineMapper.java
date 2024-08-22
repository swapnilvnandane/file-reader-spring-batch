package com.filereader.app.processor;

import com.filereader.app.entity.MyTest;
import jakarta.annotation.PostConstruct;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

@Component
public class CustomLineMapper implements LineMapper<MyTest> {

    @Value("${com.file.separator}")
    private String delimiter;

    private DelimitedLineTokenizer tokenizer;

    @PostConstruct
    public void init() {
        tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(delimiter);
    }

    @Override
    public MyTest mapLine(String line, int lineNumber) throws BindException {
        FieldSet fieldSet = tokenizer.tokenize(line);
        MyTest record = new MyTest();
        record.setName(fieldSet.readString(0));
        record.setCity(fieldSet.readString(1));
        return record;
    }
}
