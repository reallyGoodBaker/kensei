package top.rgb39.kensei_client.plugin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import top.rgb39.ecs.arch.App;
import top.rgb39.ecs.loader.Scanner;
import top.rgb39.ecs.plugin.Plugin;
import top.rgb39.ecs.util.Logger;
import top.rgb39.kensei_client.annotation.CustomItem;

import java.util.HashMap;
import java.util.Map;

public class ItemLoader implements Plugin {

    static Map<Class<?>, Item> itemsRecord = new HashMap<>();

    public void loadItems() {
        Scanner.classes.forEach((k, cls) -> {
            try {
                CustomItem itemInfo = cls.getAnnotation(CustomItem.class);

                if (itemInfo == null) {
                    return;
                }

                Item item = (Item) cls
                        .getDeclaredConstructor()
                        .newInstance();

                itemsRecord.put(cls, item);

                ResourceLocation itemName = new ResourceLocation(itemInfo.namespace(), itemInfo.name());
                Items.registerItem(itemName, item);
                Logger.info("debug", "Loaded item: " + itemInfo.name());
            } catch (Exception ignored) {}
        });
    }

    @Override
    public void build(App app) {
        loadItems();
    }
}
