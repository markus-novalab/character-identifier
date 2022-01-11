package org.khpi.ai.service;

import org.khpi.ai.model.Character;
import org.khpi.ai.model.FileCharacterInfo;
import org.khpi.ai.model.Standard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StandardsLoader {
    private final List<Character> standardList = new ArrayList<>();
    private final List<Character> metaData = new ArrayList<>();
    private final CSVHandler csvHandler = new CSVHandler();
    private static final String STANDARD_PATH = "./src/main/resources/standards/";
    private static final String META_PATH = "./src/main/resources/meta/";

    public List<Standard> loadStandards() {
        standardList.addAll(loadData(STANDARD_PATH));
        return standardList.stream()
                .map(Character::getStandard)
                .collect(Collectors.toList());
    }

    public List<Standard> loadMetadata() {
        metaData.addAll(loadData(META_PATH));
        return metaData.stream()
                .map(Character::getStandard)
                .collect(Collectors.toList());
    }

    private List<Character> loadData(String path) {
        List<Character> data = new ArrayList<>();

        for(Standard standard : Standard.values()) {
            String standardFilePath = String.format("%s%s.csv", path, standard);
            FileCharacterInfo info = new FileCharacterInfo(standardFilePath, standard);
            Character standardCharacter = csvHandler.readCharacter(info);

            if (standardCharacter != null) {
                data.add(standardCharacter);
            }
        }

        return data;
    }

    public List<Character> getStandardList() {
        return new ArrayList<>(standardList);
    }

    public List<Character> getMetaData() {
        return new ArrayList<>(metaData);
    }
}
