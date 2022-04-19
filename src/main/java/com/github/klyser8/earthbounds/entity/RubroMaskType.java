package com.github.klyser8.earthbounds.entity;

public enum RubroMaskType {

    DEFAULT(0),
    GILDED(1),
    CRYSTALLINE(2),
    CHARRED(3),
    VERDANT(4),
    CRIMSON(5);

    private final int id;

    RubroMaskType(int id) {
        this.id = id;
    }

    public static RubroMaskType getFromId(int id) {
        return switch (id) {
            case 1 -> GILDED;
            case 2 -> CRYSTALLINE;
            case 3 -> CHARRED;
            case 4 -> VERDANT;
            case 5 -> CRIMSON;
            default -> DEFAULT;
        };
    }

    public int getId() {
        return id;
    }
}
