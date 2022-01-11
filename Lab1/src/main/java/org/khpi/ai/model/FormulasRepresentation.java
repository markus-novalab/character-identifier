package org.khpi.ai.model;

import lombok.experimental.UtilityClass;
import org.khpi.ai.service.CharacterDeterminant;

import java.util.List;

@UtilityClass
public class FormulasRepresentation {
    public static final String WITH_SQUARE_ROOT =
                    "                      /-------------\n" +
                    "                _    /n             2\n" +
                    "   L(Si, Xj) =   \\  / Σ (Sik - Xjk)\n" +
                    "                  \\/ k=1";

    public static final String WITH_MODULE =
                    "               n\n" +
                    "   L(Si, Xj) = Σ |(Sik - Xjk)|\n" +
                    "               k=1";

    public static final String WITH_COEFFICIENT =
                    "                      /-------------\n" +
                    "                _    /n             2\n" +
                    "   L(Si, Xj) =   \\  / Σ Ŋk(Sik - Xjk)\n" +
                    "                  \\/ k=1";

    public static final String WITH_MODULE_AND_FRACTION =
                    "               n   |Sik - Xjk|\n" +
                    "   L(Si, Xj) = Σ   ———————————\n" +
                    "               k=1 |Sik + Xjk|";

    public static final String WITH_ARCCOS =
                    "                      n\n" +
                    "   L(Si, Xj) = arccos(Σ Sik×Xjk / |Si|×|Xj|)\n" +
                    "                      k=1";


    public static void printResult(Standard standard, CharacterDeterminant.Formula formula) {
        System.out.println("-------------------------------------------");
        System.out.println("Used formula: ");
        System.out.println(getRepresentationForFormula(formula));
        System.out.printf("%n----> You character is %s <----%n", standard);
        System.out.println("-------------------------------------------");
    }

    public static void printMultipleResult(List<Standard> standardList) {
        System.out.println("-------------------------------------------");
        System.out.println("Used formulas: Russell and Rao, Jokard and Needman, Dyce, Sokal and Snif");
        System.out.printf("%n----> Results by formulas %s <----%n", standardList);
        System.out.println("-------------------------------------------");
    }

    public static String getRepresentationForFormula(CharacterDeterminant.Formula formula) {
        switch (formula) {
            case WITH_SQUARE_ROOT:
                return WITH_SQUARE_ROOT;
            case WITH_MODULE:
                return WITH_MODULE;
            case WITH_COEFFICIENT:
                return WITH_COEFFICIENT;
            case WITH_MODULE_AND_FRACTION:
                return WITH_MODULE_AND_FRACTION;
            case WITH_ARCCOS:
                return WITH_ARCCOS;
        }

        throw new IllegalArgumentException(String.format("No representation for formula: %s", formula));
    }
}
