package com.danhaywood.camel.isispubsubjdo.bus;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import com.google.common.io.Resources;

public class ExamplePayloads {
    
    private ExamplePayloads() {}

    public static String _001 = fileContents("001-Action-ToDoItem#completed.json");
    public static String _002 = fileContents("002-ChangedObject.json");
    
    public static String fileContents(String fileName) {
        final URL url = Resources.getResource(ExamplePayloads.class, fileName);
        try {
            return Resources.toString(url, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
