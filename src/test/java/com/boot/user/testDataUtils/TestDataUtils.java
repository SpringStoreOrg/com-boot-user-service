package com.boot.user.testDataUtils;

import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@NoArgsConstructor
public class TestDataUtils {


    public static String readFileAsString(String file){
        try{
            return new String(Files.readAllBytes(Paths.get(file)));
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
