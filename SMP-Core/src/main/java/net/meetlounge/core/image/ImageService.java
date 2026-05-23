package net.meetlounge.core.image;

import net.meetlounge.core.Core;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public final class ImageService {

    private final Core plugin;
    private final Map<String, List<UUID>> images = new HashMap<>();

    public ImageService(Core plugin) {
        this.plugin = plugin;
    }

    public void spawnImage(String id, String fileName, Location origin, BlockFace face) {
        File file = new File(plugin.getDataFolder(), "images/" + fileName);

        if (!file.exists()) {
            plugin.getLogger().warning("Bild nicht gefunden: " + fileName);
            return;
        }

        try {
            BufferedImage image = ImageIO.read(file);

            int mapsX = (int) Math.ceil(image.getWidth() / 128.0);
            int mapsY = (int) Math.ceil(image.getHeight() / 128.0);

            World world = origin.getWorld();
            List<UUID> frameIds = new ArrayList<>();

            for (int tileY = 0; tileY < mapsY; tileY++) {
                for (int tileX = 0; tileX < mapsX; tileX++) {

                    BufferedImage part = image.getSubimage(
                            tileX * 128,
                            tileY * 128,
                            Math.min(128, image.getWidth() - tileX * 128),
                            Math.min(128, image.getHeight() - tileY * 128)
                    );

                    BufferedImage mapImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
                    mapImage.getGraphics().drawImage(part, 0, 0, null);

                    MapView map = Bukkit.createMap(world);
                    map.getRenderers().clear();
                    map.addRenderer(new StaticImageRenderer(mapImage));

                    ItemStack item = new ItemStack(Material.FILLED_MAP);
                    MapMeta meta = (MapMeta) item.getItemMeta();
                    meta.setMapView(map);
                    item.setItemMeta(meta);

                    Location frameLocation = offset(origin, face, tileX, tileY);

                    GlowItemFrame frame = (GlowItemFrame) world.spawnEntity(frameLocation, EntityType.GLOW_ITEM_FRAME);
                    frame.setFacingDirection(face, true);
                    frame.setItem(item);
                    frame.setFixed(true);
                    frame.setVisible(false);
                    frame.setInvulnerable(true);
                    frame.addScoreboardTag("meetlounge_image");
                    frame.addScoreboardTag("image_" + id.toLowerCase());

                    frameIds.add(frame.getUniqueId());
                }
            }

            images.put(id.toLowerCase(), frameIds);

        } catch (IOException exception) {
            plugin.debug().error("Bild konnte nicht gespawnt werden", exception);
        }
    }

    private Location offset(Location origin, BlockFace face, int tileX, int tileY) {
        Location location = origin.clone();

        switch (face) {
            case NORTH -> location.add(-tileX, -tileY, 0);
            case SOUTH -> location.add(tileX, -tileY, 0);
            case EAST -> location.add(0, -tileY, -tileX);
            case WEST -> location.add(0, -tileY, tileX);
            default -> location.add(tileX, -tileY, 0);
        }

        return location;
    }

    public void move(String id, double x, double y, double z) {
        List<UUID> ids = images.get(id.toLowerCase());

        if (ids == null) {
            return;
        }

        for (UUID uuid : ids) {
            Entity entity = Bukkit.getEntity(uuid);

            if (entity != null) {
                entity.teleport(entity.getLocation().add(x, y, z));
            }
        }
    }

    public void delete(String id) {
        String tag = "image_" + id.toLowerCase();

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getScoreboardTags().contains(tag)) {
                    entity.remove();
                }
            }
        }

        images.remove(id.toLowerCase());
    }

    private static final class StaticImageRenderer extends MapRenderer {

        private final BufferedImage image;
        private boolean rendered;

        private StaticImageRenderer(BufferedImage image) {
            this.image = image;
        }

        @Override
        public void render(MapView mapView, MapCanvas canvas, Player player) {
            if (rendered) {
                return;
            }

            canvas.drawImage(0, 0, image);
            rendered = true;
        }
    }
}