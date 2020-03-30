package com.dlsc.workbenchfx.modules.dashboard.model;

public enum ColorCategories {

    RED("RED"), ORANGE("ORANGE"), YELLOW("YELLOW"),
    GREEN("GREEN"), BLUE("BLUE") , VIOLET("VIOLET");

    private String name;

    ColorCategories(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
