package com.timmytime.predictoranalysisplayers.enumerator;

public enum ApplicableFantasyLeagues {
    ENGLAND_1("england"),
    SPAIN_1("spain"),
    ITALY_1("italy"),
    GERMAN_1("german"),
    FRANCE_1("france");

    public String getCountry() {
        return country;
    }

    private String country;

    ApplicableFantasyLeagues(String country){
        this.country = country;
    }
}
