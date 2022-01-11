package org.khpi.ai;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.khpi.ai.model.Character;
import org.khpi.ai.model.FileCharacterInfo;
import org.khpi.ai.model.FormulasRepresentation;
import org.khpi.ai.model.Standard;
import org.khpi.ai.service.CSVHandler;
import org.khpi.ai.service.CharacterDeterminant;
import org.khpi.ai.service.StandardsLoader;

import java.util.Arrays;
import java.util.List;
import java.util.function.ToDoubleFunction;

public class Application {
    private static final String FILE_OPTION = "f";

    public static void main(String[] args) throws ParseException {
        StandardsLoader loader = new StandardsLoader();
        List<Standard> standards = loader.loadStandards();
        List<Standard> metadata = loader.loadMetadata();
        System.out.printf("Standards loaded: %s%n", standards);
        System.out.printf("Metadata loaded: %s%n", metadata);

        CharacterDeterminant determinant = new CharacterDeterminant();
        CSVHandler csvHandler = new CSVHandler();

        Options options = new Options();
        options.addOption(FILE_OPTION, true, "file name");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption(FILE_OPTION)) {
            FileCharacterInfo info = new FileCharacterInfo(cmd.getOptionValue(FILE_OPTION), null);
            Character inputCharacter = csvHandler.readCharacter(info);

            Arrays.stream(CharacterDeterminant.Formula.values()).forEach(formula -> {
                Standard standard = determinant.determineCharacter(
                        inputCharacter, loader.getStandardList(), loader.getMetaData(), formula);
                FormulasRepresentation.printResult(standard, formula);
            });

            List<ToDoubleFunction<Character>> functionList = List.of(
                    standardCharacter -> determinant.computeByRussellAndRao(inputCharacter, standardCharacter),
                    standardCharacter -> determinant.computeByJokardAndNeedman(inputCharacter, standardCharacter),
                    standardCharacter -> determinant.computeByDyce(inputCharacter, standardCharacter),
                    standardCharacter -> determinant.computeBySokalAndSnif(inputCharacter, standardCharacter)
            );

            List<Standard> standardList = determinant.determineCharacterWithParameters(inputCharacter, loader.getStandardList(), functionList);

            FormulasRepresentation.printMultipleResult(standardList);
        } else {
            throw new IllegalArgumentException("You need specify -f option with file name");
        }
    }
}
