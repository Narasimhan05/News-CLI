package com.newsarticle.model;

public class Source {

    private int id;
    private String apiSourceId;
    private String name;
    private String language;
    private String country;

    public Source() {
    }

    public Source(String apiSourceId, String name, String language, String country) {
        this.apiSourceId = apiSourceId;
        this.name = name;
        this.language = language;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApiSourceId() {
        return apiSourceId;
    }

    public void setApiSourceId(String apiSourceId) {
        this.apiSourceId = apiSourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Source{id=" + id + ", name='" + name + "'}";
    }
}
