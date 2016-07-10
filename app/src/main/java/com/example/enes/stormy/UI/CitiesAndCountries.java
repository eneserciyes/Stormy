package com.example.enes.stormy.UI;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Enes on 7/10/2016.
 */
public class CitiesAndCountries {
    private String[] countries = {"France","United States of America","Turkey"};
    private String[] france = {"Toulouse","Marseille","Paris"};
    private String[] usa = {"New York","Los Angeles","Salt Lake City"};
    private String[] turkey = {"İzmir","Kayseri","Sivas","Niğde"};

    private Map<String,String[]> cities = new HashMap<>();

    public void populateMap(){
        cities.put("france",france);
        cities.put("usa",usa);
        cities.put("turkey",turkey);
    }

    public String[] getCountries() {
        return countries;
    }

    public String[] getFrance() {
        return france;
    }

    public String[] getUsa() {
        return usa;
    }

    public String[] getTurkey() {
        return turkey;
    }

    public Map<String, String[]> getCities() {
        return cities;
    }

    public void setCities(Map<String, String[]> cities) {
        this.cities = cities;
    }
}
