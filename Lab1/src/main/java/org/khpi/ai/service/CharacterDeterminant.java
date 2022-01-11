package org.khpi.ai.service;

import org.khpi.ai.model.Character;
import org.khpi.ai.model.QualitativeParameters;
import org.khpi.ai.model.Standard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.ToDoubleFunction;

public class CharacterDeterminant {

    public Standard determineCharacter(Character inputCharacter, List<Character> standardList, List<Character> metadata,
                                       Formula formula) {
        switch (formula) {
            case WITH_SQUARE_ROOT:
                return determineCharacter(inputCharacter, standardList,
                        standardCharacter -> computeBySquareRootFormula(inputCharacter, standardCharacter));
            case WITH_MODULE:
                return determineCharacter(inputCharacter, standardList,
                        standardCharacter -> computeByModuleFormula(inputCharacter, standardCharacter));
            case WITH_COEFFICIENT:
                return determineCharacterWithMetadata(inputCharacter, standardList, metadata,
                        (standardCharacter, metaCharacter) -> computeByFormulaWithCoefficient(inputCharacter, standardCharacter, metaCharacter));
            case WITH_MODULE_AND_FRACTION:
                return determineCharacter(inputCharacter, standardList,
                        standardCharacter -> computeByFormulaWithModuleAndFraction(inputCharacter, standardCharacter));
            case WITH_ARCCOS:
                return determineCharacter(inputCharacter, standardList,
                        standardCharacter -> computeByFormulaWithArcCos(inputCharacter, standardCharacter));
        }

        throw new IllegalArgumentException(String.format("Formula %s does not supported", formula));
    }


    private Standard determineCharacter(Character inputCharacter, List<Character> standardList, ToDoubleFunction<Character> function) {
        Map<Double, Standard> standardDoubleMap = new HashMap<>();

        standardList.forEach(standardCharacter -> {

            if (!isCharactersDimensionsEqual(inputCharacter, standardCharacter)) {
                throw new IllegalArgumentException("Input character have illegal dimension size.");
            }

            standardDoubleMap.put(function.applyAsDouble(standardCharacter), standardCharacter.getStandard());
        });

        return findMin(standardDoubleMap);
    }

    private Standard determineCharacterWithMetadata(Character inputCharacter, List<Character> standardList,
                                                List<Character> metadata, BiFunction<Character, Character, Double> function) {
        Map<Double, Standard> standardDoubleMap = new HashMap<>();

        standardList.forEach(standardCharacter -> {
            Character metaCharacter = metadata.get(metadata.indexOf(standardCharacter));

            if (!isCharactersDimensionsEqual(inputCharacter, standardCharacter, metaCharacter)) {
                throw new IllegalArgumentException("Input character have illegal dimension size.");
            }

            standardDoubleMap.put(function.apply(standardCharacter, metaCharacter), standardCharacter.getStandard());
        });

        return findMin(standardDoubleMap);
    }

    public List<Standard> determineCharacterWithParameters(Character inputCharacter, List<Character> standardList, List<ToDoubleFunction<Character>> functions) {
        List<Standard> results = new ArrayList<>();
        Map<Double, Standard> standardDoubleMap = new HashMap<>();

        functions.forEach(function -> {
            standardList.forEach(standardCharacter -> {

                if (!isCharactersDimensionsEqual(inputCharacter, standardCharacter)) {
                    throw new IllegalArgumentException("Input character have illegal dimension size.");
                }

                standardDoubleMap.put(function.applyAsDouble(standardCharacter), standardCharacter.getStandard());
            });
            results.add(findMax(standardDoubleMap));
            standardDoubleMap.clear();
        });

        return results;
    }

    private Standard findMin(Map<Double, Standard> standardDoubleMap) {
        Optional<Standard> min = standardDoubleMap.keySet().stream()
                .min(Double::compareTo)
                .map(standardDoubleMap::get);

        if (min.isEmpty()) {
            throw new IllegalStateException("Cannot find min value");
        }

        return min.get();
    }

    private Standard findMax(Map<Double, Standard> standardDoubleMap) {
        Optional<Standard> max = standardDoubleMap.keySet().stream()
                .max(Double::compareTo)
                .map(standardDoubleMap::get);

        if (max.isEmpty()) {
            throw new IllegalStateException("Cannot find min value");
        }

        return max.get();
    }


    /**
     *                  /-------------
     *            _    /n             2
     * L(Si, Xj) = \  / Σ (Sik - Xjk)
     *              \/ k=1
     * @param input character that need to determine.
     * @param standard standard character.
     * @return result of formula.
     */
    private double computeBySquareRootFormula(Character input, Character standard) {
        final int n = input.getData().size();
        List<List<Integer>> inputData = input.getData();
        List<List<Integer>> standardData = standard.getData();
        double rawSum = 0;

        for (int i = 0; i < n; i++) {
            List<Integer> inputRaw = inputData.get(i);
            List<Integer> standardRaw = standardData.get(i);

            for (int j = 0; j < inputRaw.size(); j++) {
                rawSum += Math.pow(inputRaw.get(j) - (double) standardRaw.get(j), 2);
            }
        }

        return Math.sqrt(rawSum);
    }

    /**
     *             n
     * L(Si, Xj) = Σ |(Sik - Xjk)|
     *             k=1
     *
     * @param input character that need to determine.
     * @param standard standard character.
     * @return result of formula.
     */
    private double computeByModuleFormula(Character input, Character standard) {
        final int n = input.getData().size();
        List<List<Integer>> inputData = input.getData();
        List<List<Integer>> standardData = standard.getData();
        double rawSum = 0;

        for (int i = 0; i < n; i++) {
            List<Integer> inputRaw = inputData.get(i);
            List<Integer> standardRaw = standardData.get(i);

            for (int j = 0; j < inputRaw.size(); j++) {
                rawSum += Math.abs(inputRaw.get(j) - (double) standardRaw.get(j));
            }
        }

        return rawSum;
    }

    /**
     *                      /-------------
     *               _     /n             2
     *   L(Si, Xj) =   \  / Σ Ŋk(Sik - Xjk)
     *                  \/ k=1
     *
     * @param input character that need to determine.
     * @param standard standard character.
     * @param metaData coefficients of each point of data.
     * @return result of formula.
     */
    private double computeByFormulaWithCoefficient(Character input, Character standard, Character metaData) {
        final int n = input.getData().size();
        List<List<Integer>> inputData = input.getData();
        List<List<Integer>> standardData = standard.getData();
        List<List<Integer>> meta = metaData.getData();
        double rawSum = 0;

        for (int i = 0; i < n; i++) {
            List<Integer> inputRaw = inputData.get(i);
            List<Integer> standardRaw = standardData.get(i);
            List<Integer> metaRaw = meta.get(i);

            for (int j = 0; j < inputRaw.size(); j++) {
                rawSum += metaRaw.get(j) * Math.pow(inputRaw.get(j) - (double) standardRaw.get(j), 2);
            }
        }

        return Math.sqrt(rawSum);
    }

    /**
     *               n   |Sik - Xjk|
     *   L(Si, Xj) = Σ   ———————————
     *               k=1 |Sik + Xjk|
     *
     * @param input character that need to determine.
     * @param standard standard character.
     * @return result of formula.
     */
    private double computeByFormulaWithModuleAndFraction(Character input, Character standard) {
        final int n = input.getData().size();
        List<List<Integer>> inputData = input.getData();
        List<List<Integer>> standardData = standard.getData();
        double rawSum = 0;

        for (int i = 0; i < n; i++) {
            List<Integer> inputRaw = inputData.get(i);
            List<Integer> standardRaw = standardData.get(i);

            for (int j = 0; j < inputRaw.size(); j++) {
                double numerator = Math.abs(inputRaw.get(j) - (double) standardRaw.get(j)) + 1; // +1 is used in cases when multiplication provides 0
                double denominator = Math.abs(inputRaw.get(j) + (double) standardRaw.get(j)) + 1; // +1 is used in cases when multiplication provides 0
                rawSum += numerator / denominator;
            }
        }

        return rawSum;
    }

    /**
     *                      n
     *   L(Si, Xj) = arccos(Σ Sik×Xjk / |Si|×|Xj|)
     *                      k=1
     *
     * @param input character that need to determine.
     * @param standard standard character.
     * @return result of formula.
     */
    private double computeByFormulaWithArcCos(Character input, Character standard) {
        final int n = input.getData().size();
        List<List<Integer>> inputData = input.getData();
        List<List<Integer>> standardData = standard.getData();
        double rawSum = 0;

        for (int i = 0; i < n; i++) {
            List<Integer> inputRaw = inputData.get(i);
            List<Integer> standardRaw = standardData.get(i);

            for (int j = 0; j < inputRaw.size(); j++) {
                double numerator = (inputRaw.get(j) * standardRaw.get(j)) + 1.0; // +1 is used in cases when multiplication provides 0
                double denominator = ((Math.abs(inputRaw.get(j)) + 1) * (Math.abs(standardRaw.get(j)) + 1)); // +1 is used in cases when multiplication provides 0
                rawSum += numerator / denominator;
            }
        }

        return Math.toDegrees(Math.acos(Math.cos(rawSum)));
    }

    public double computeByRussellAndRao(Character input, Character standard) {
        QualitativeParameters qualitativeParameters = computeByFormulaWithParameters(input, standard);

        double denominator =
                qualitativeParameters.getASum() +
                        qualitativeParameters.getBSum() +
                        qualitativeParameters.getGSum() +
                        qualitativeParameters.getHSum();

        denominator = denominator == 0
                ? 1
                : denominator;

        return qualitativeParameters.getASum() / denominator;
    }

    public double computeByJokardAndNeedman(Character input, Character standard) {
        QualitativeParameters qualitativeParameters = computeByFormulaWithParameters(input, standard);

        double n =
                qualitativeParameters.getASum() +
                        qualitativeParameters.getBSum() +
                        qualitativeParameters.getGSum() +
                        qualitativeParameters.getHSum();

        double denominator = n - qualitativeParameters.getBSum();

        denominator = denominator == 0
                ? 1
                : denominator;

        return qualitativeParameters.getASum() / denominator;
    }

    public double computeByDyce(Character input, Character standard) {
        QualitativeParameters qualitativeParameters = computeByFormulaWithParameters(input, standard);

        double denominator = (2 * qualitativeParameters.getASum()) + qualitativeParameters.getGSum() +
                qualitativeParameters.getHSum();

        denominator = denominator == 0
                ? 1
                : denominator;

        return qualitativeParameters.getASum() / denominator;
    }

    public double computeBySokalAndSnif(Character input, Character standard) {
        QualitativeParameters qualitativeParameters = computeByFormulaWithParameters(input, standard);

        double denominator = qualitativeParameters.getASum() + 2 * (qualitativeParameters.getGSum() +
                qualitativeParameters.getHSum());

        denominator = denominator == 0
                ? 1
                : denominator;

        return qualitativeParameters.getASum() / denominator;
    }

    private QualitativeParameters computeByFormulaWithParameters(Character input, Character standard) {
        final int n = input.getData().size();

        List<List<Integer>> inputData = input.getData();
        List<List<Integer>> standardData = standard.getData();

        double aSum = 0;
        double bSum = 0;
        double gSum = 0;
        double hSum = 0;

        for (int i = 0; i < n; i++) {
            List<Integer> inputRaw = inputData.get(i);
            List<Integer> standardRaw = standardData.get(i);

            for (int j = 0; j < inputRaw.size(); j++) {
                aSum += (inputRaw.get(j) * standardRaw.get(j)) + 1;
                bSum += ((1 - inputRaw.get(j)) * (1 - standardRaw.get(j))) + 1;
                gSum += (inputRaw.get(j) * (1 - standardRaw.get(j))) + 1;
                hSum += ((1 - inputRaw.get(j)) * standardRaw.get(j));
            }
        }

        return QualitativeParameters.builder()
                .aSum(aSum)
                .bSum(bSum)
                .gSum(gSum)
                .hSum(hSum)
                .build();
    }

    private boolean isCharactersDimensionsEqual(Character one, Character two) {
        List<List<Integer>> oneData = one.getData();
        List<List<Integer>> twoData = two.getData();

        if (oneData.size() != twoData.size()) {
            return false;
        }

        for (int i = 0; i < oneData.size(); i++) {
            if (oneData.get(i).size() != twoData.get(i).size()) {
                return false;
            }
        }

        return true;
    }

    private boolean isCharactersDimensionsEqual(Character one, Character two, Character three) {
        List<List<Integer>> firstData = one.getData();
        List<List<Integer>> secondData = two.getData();
        List<List<Integer>> thirdData = three.getData();

        if (firstData.size() != secondData.size() || secondData.size() != thirdData.size()) {
            return false;
        }

        for (int i = 0; i < firstData.size(); i++) {
            if (firstData.get(i).size() != secondData.get(i).size()
                    || thirdData.get(i).size() != secondData.get(i).size()) {
                return false;
            }
        }

        return true;
    }

    public enum Formula {
        WITH_SQUARE_ROOT, WITH_MODULE, WITH_COEFFICIENT, WITH_MODULE_AND_FRACTION, WITH_ARCCOS
    }
}
