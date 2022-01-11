package org.khpi.ai.service;

import org.khpi.ai.model.Character;
import org.khpi.ai.model.FileCharacterInfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CSVHandler {
    private static final String COMMA_DELIMITER = ",";

    public Character readCharacter(FileCharacterInfo fileInfo) {
        List<List<String>> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileInfo.getFileName()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            return null;
        }

        if (records.isEmpty()) {
            return null;
        }

        List<List<Integer>> characterData = records.stream()
                .map(stringList -> stringList
                        .stream()
                        .map(Integer::parseInt)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        return new Character(characterData, fileInfo.getStandard());
    }
}
