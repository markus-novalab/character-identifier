package org.khpi.ai.model;

import java.util.List;

public class Character {
    private final List<List<Integer>> data;
    private final Standard standard;

    public Character(List<List<Integer>> data, Standard standard) {
        this.data = data;
        this.standard = standard;
    }

    public List<List<Integer>> getData() {
        return data;
    }

    public Standard getStandard() {
        return standard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Character character = (Character) o;

        return getStandard() == character.getStandard();
    }

    @Override
    public int hashCode() {
        return getStandard() != null ? getStandard().hashCode() : 0;
    }
}
