package org.khpi.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class QualitativeParameters {
    double aSum;
    double bSum;
    double gSum;
    double hSum;
}
