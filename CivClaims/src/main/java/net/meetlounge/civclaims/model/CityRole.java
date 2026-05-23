package net.meetlounge.civclaims.model;

public enum CityRole {
    MAYOR,
    VICE,
    COUNCIL,
    CITIZEN,
    GUEST;

    public boolean canManageClaims() {
        return this == MAYOR || this == VICE;
    }

    public boolean canBuild() {
        return this != GUEST;
    }
}
