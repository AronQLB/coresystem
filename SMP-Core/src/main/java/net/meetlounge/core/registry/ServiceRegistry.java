package net.meetlounge.core.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ServiceRegistry {

    private final Map<Class<?>, Object> services = new HashMap<>();

    public <T> void register(Class<T> serviceClass, T service) {
        services.put(serviceClass, service);
    }

    public <T> T get(Class<T> serviceClass) {
        Object service = services.get(serviceClass);

        if(service == null)  {
            throw new IllegalStateException("Server nicht registriert: " + serviceClass.getSimpleName());
        }
        return serviceClass.cast(service);
    }

    public <T> Optional<T> find(Class<T> serviceClass) {
        Object service = services.get(serviceClass);

        if(service == null) {
            return Optional.empty();
        }
        return Optional.of(serviceClass.cast(service));
    }

    public boolean has(Class<?> serviceClass) {
        return services.containsKey(serviceClass);
    }

    public void clear() {
        services.clear();
    }
}
