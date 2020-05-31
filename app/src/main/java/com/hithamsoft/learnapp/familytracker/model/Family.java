package com.hithamsoft.learnapp.familytracker.model;

public class Family {
    private String personName;
    private int personIcon;

    public Family() {
    }

    public Family(String personName, int personIcon) {
        this.personName = personName;
        this.personIcon = personIcon;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public int getPersonIcon() {
        return personIcon;
    }

    public void setPersonIcon(int personIcon) {
        this.personIcon = personIcon;
    }
}
