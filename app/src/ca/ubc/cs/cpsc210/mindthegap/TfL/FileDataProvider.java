package ca.ubc.cs.cpsc210.mindthegap.TfL;

/*
 * Copyright 2015-2016 Department of Computer Science UBC
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Data provider where data source is a file in Java (non-Android) environment
 */
public class FileDataProvider extends AbstractFileDataProvider {
    private String fileName;

    public FileDataProvider(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String dataSourceToString() throws IOException {
        InputStream is = new FileInputStream(fileName);
        return readSource(is);
    }
}
