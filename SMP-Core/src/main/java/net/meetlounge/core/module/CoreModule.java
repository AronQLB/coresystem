package net.meetlounge.core.module;


public interface CoreModule {

    String name();

    void enable();

    void disable();

    default void reload() {
        disable();
        enable();
    }
}