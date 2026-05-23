package net.meetlounge.core.auction;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public final class ItemSerializer {

    private ItemSerializer() {}

    public static String serialize(ItemStack item) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        try (BukkitObjectOutputStream outputStream = new BukkitObjectOutputStream(byteStream)) {
            outputStream.writeObject(item);
        }

        return Base64.getEncoder().encodeToString(byteStream.toByteArray());
    }

    public static ItemStack deserialize(String data) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.getDecoder().decode(data);

        try (BukkitObjectInputStream inputStream = new BukkitObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (ItemStack) inputStream.readObject();
        }
    }
}
