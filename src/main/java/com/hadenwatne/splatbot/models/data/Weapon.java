package com.hadenwatne.splatbot.models.data;

public class Weapon {
    private String name;
    private String category;
    private String subWeapon;
    private String specialWeapon;

    public Weapon(String name, String category, String subWeapon, String specialWeapon) {
        this.name = name;
        this.category = category;
        this.subWeapon = subWeapon;
        this.specialWeapon = specialWeapon;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getSubWeapon() {
        return subWeapon;
    }

    public String getSpecialWeapon() {
        return specialWeapon;
    }
}
