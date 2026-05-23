package net.meetlounge.civclaims.storage;

import net.meetlounge.civclaims.model.City;

import java.util.Collection;
import java.util.List;

public interface CityStorage {
    List<City> loadCities();

    void saveCities(Collection<City> cities);
}
