package com.danhaywood.camel.isispubsubjdo.bus;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import com.google.common.io.Resources;

public class ExamplePayloads {
    
    private ExamplePayloads() {}

    public static String _001 = fileContents("001-Action-ToDoItem#completed.json");
    public static String _002 = fileContents("002-ObjectUpdated.json");
    public static String _003 = fileContents("003-Action-ToDoItem#notCompleted.json");
    
    public static String fileContents(String fileName) {
        final URL url = Resources.getResource(ExamplePayloads.class, fileName);
        try {
            return Resources.toString(url, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
