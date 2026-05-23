package net.meetlounge.civclaims.model;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class City {

    private final String name;
    private final UUID mayorId;
    private final Map<UUID, CityRole> members = new HashMap<>();
    private final Set<ClaimChunk> claims = new HashSet<>();
    private double balance;
    private double dailyTax;
    private boolean pvpAllowed;

    public City(String name, UUID mayorId) {
        this.name = name;
        this.mayorId = mayorId;
        this.members.put(mayorId, CityRole.MAYOR);
    }

    public String name() {
        return name;
    }

    public UUID mayorId() {
        return mayorId;
    }

    public Map<UUID, CityRole> members() {
        return members;
    }

    public Set<ClaimChunk> claims() {
        return claims;
    }

    public double balance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = Math.max(0.0, balance);
    }

    public double dailyTax() {
        return dailyTax;
    }

    public void setDailyTax(double dailyTax) {
        this.dailyTax = Math.max(0.0, dailyTax);
    }

    public boolean pvpAllowed() {
        return pvpAllowed;
    }

    public void setPvpAllowed(boolean pvpAllowed) {
        this.pvpAllowed = pvpAllowed;
    }

    public Optional<CityRole> roleOf(UUID playerId) {
        return Optional.ofNullable(members.get(playerId));
    }

    public boolean isMember(UUID playerId) {
        return members.containsKey(playerId);
    }

    public void addMember(UUID playerId) {
        members.putIfAbsent(playerId, CityRole.CITIZEN);
    }

    public void setRole(UUID playerId, CityRole role) {
        if (members.containsKey(playerId)) {
            members.put(playerId, role);
        }
    }
}
