package net.meetlounge.core.bloodmoon;

import net.meetlounge.core.Core;
import net.meetlounge.core.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public final class BloodMoonListener implements Listener {

    private final Core plugin;
    private final Random random = new Random();

    public BloodMoonListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Monster monster)) {
            return;
        }

        if (!plugin.bloodMoon().rollBoss(monster.getWorld())) {
            return;
        }

        makeBoss(monster);
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Monster monster) || entity.getKiller() == null) {
            return;
        }

        if (isBoss(monster)) {
            event.getDrops().add(new ItemStack(Material.DIAMOND, random.nextInt(3) + 1));
            event.getDrops().add(new ItemStack(Material.GOLDEN_APPLE, 1));
            entity.getKiller().sendMessage(plugin.messages().raw(Core.prefix + "&4BloodMoon-Boss &8| &cDu hast einen Boss zerlegt und extra Loot erhalten."));
            return;
        }

        if (plugin.bloodMoon().rollRareDrop(monster.getWorld())) {
            event.getDrops().add(randomRareDrop());
            entity.getKiller().sendMessage(plugin.messages().raw(Core.prefix + "&4BloodMoon &8| &cSeltener Lootdrop!"));
        }
    }

    private void makeBoss(Monster monster) {
        monster.getPersistentDataContainer().set(key("bloodmoon_boss"), PersistentDataType.BOOLEAN, true);
        addEffect(monster, 1, "SPEED");
        addEffect(monster, 0, "STRENGTH", "INCREASE_DAMAGE");
        addEffect(monster, 0, "RESISTANCE", "DAMAGE_RESISTANCE");

        Attribute maxHealth = attribute("MAX_HEALTH", "GENERIC_MAX_HEALTH");
        if (maxHealth != null) {
            var attribute = monster.getAttribute(maxHealth);
            if (attribute != null) {
                double health = attribute.getBaseValue() * plugin.configs().config().get().getDouble("bloodmoon.boss-health-multiplier", 2.5);
                attribute.setBaseValue(health);
                monster.setHealth(health);
            }
        }

        EntityEquipment equipment = monster.getEquipment();
        if (equipment != null) {
            ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
            Enchantment sharpness = enchantment("SHARPNESS", "DAMAGE_ALL");
            if (sharpness != null) {
                sword.addUnsafeEnchantment(sharpness, 4);
            }
            equipment.setItemInMainHand(sword);
            equipment.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
            equipment.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
            equipment.setItemInMainHandDropChance(0.02F);
            equipment.setHelmetDropChance(0.04F);
            equipment.setChestplateDropChance(0.04F);
        }

        monster.setCustomName(TextUtil.color("&4&lBloodMoon Boss &8| &c" + monster.getType().name()));
        monster.setCustomNameVisible(true);
    }

    private boolean isBoss(Monster monster) {
        Boolean boss = monster.getPersistentDataContainer().get(key("bloodmoon_boss"), PersistentDataType.BOOLEAN);
        return boss != null && boss;
    }

    private ItemStack randomRareDrop() {
        List<ItemStack> drops = List.of(
                new ItemStack(Material.DIAMOND, random.nextInt(2) + 1),
                new ItemStack(Material.EMERALD, random.nextInt(4) + 2),
                new ItemStack(Material.EXPERIENCE_BOTTLE, random.nextInt(8) + 4),
                new ItemStack(Material.GOLDEN_APPLE, 1),
                new ItemStack(Material.ANCIENT_DEBRIS, 1)
        );

        return drops.get(random.nextInt(drops.size()));
    }

    private NamespacedKey key(String value) {
        return new NamespacedKey(plugin, value);
    }

    private void addEffect(Monster monster, int amplifier, String... names) {
        PotionEffectType type = potionEffectType(names);

        if (type != null) {
            monster.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, amplifier, true, false));
        }
    }

    private Attribute attribute(String... names) {
        for (String name : names) {
            try {
                return (Attribute) Attribute.class.getField(name).get(null);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }

        return null;
    }

    private Enchantment enchantment(String... names) {
        for (String name : names) {
            try {
                return (Enchantment) Enchantment.class.getField(name).get(null);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }

        return null;
    }

    private PotionEffectType potionEffectType(String... names) {
        for (String name : names) {
            try {
                return (PotionEffectType) PotionEffectType.class.getField(name).get(null);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }

        return null;
    }
}
