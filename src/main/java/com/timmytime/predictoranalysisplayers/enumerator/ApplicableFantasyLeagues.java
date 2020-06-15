package com.timmytime.predictoranalysisplayers.enumerator;

//note the commented out leagues dont seem to have any current data.  it could be that players got transfered into the league who have data from elsewhere
//so for now, just leave them out.
public enum ApplicableFantasyLeagues {
    ENGLAND_1("england"),
    ENGLAND_2("england"),
    SPAIN_1("spain"),
    //SPAIN_2("spain"),
    ITALY_1("italy"),
    //ITALY_2("italy"),
    GERMAN_1("german"),
    //GERMAN_2("german"),
    FRANCE_1("france"),
    //FRANCE_2("france"),
    //TURKEY_1("turkey"),
    PORTUGAL_1("portugal");

    public String getCountry() {
        return country;
    }

    private String country;

    ApplicableFantasyLeagues(String country){
        this.country = country;
    }
}
