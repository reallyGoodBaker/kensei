package top.rgb39.kensei_client.plugin;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.rgb39.ecs.arch.App;
import top.rgb39.ecs.loader.Scanner;
import top.rgb39.ecs.plugin.Plugin;
import top.rgb39.ecs.util.Lists;
import top.rgb39.kensei_client.annotation.CreativeCategory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ItemGroupLoader implements Plugin {

    public static final Map<String, CreativeModeTab> itemGroups = new HashMap<>();

    public void registerAllItemGroups() {
        List<Class<?>> items = Scanner.filter(cls -> Objects.nonNull(cls.getAnnotation(CreativeCategory.class)));
        Map<String, Set<Class<?>>> metadata = mapAllCategories(items);

        metadata.forEach((id, set) -> {
            registerItemGroup(id, set.stream().toList());
        });
    }

    Map<String, Set<Class<?>>> mapAllCategories(List<Class<?>> items) {
        Map<String, Set<Class<?>>> metadata = new ConcurrentHashMap<>();
        items.forEach(cls -> {
            CreativeCategory category = cls.getAnnotation(CreativeCategory.class);
            Set<Class<?>> set;

            if ((set = metadata.get(category.id())) == null) {
                set = new HashSet<>();
                metadata.put(category.id(), set);
            }

            set.add(cls);
        });

        return metadata;
    }

    void registerItemGroup(String id, List<Class<?>> items) {
        Class<?> itemWithIcon = Lists.find(items, cls -> {
            CreativeCategory category = cls.getAnnotation(CreativeCategory.class);
            return category.useAsIcon();
        });

        CreativeModeTab.Builder builder = FabricItemGroup.builder();

        if (itemWithIcon != null) {
            Item item = ItemLoader.itemsRecord.get(itemWithIcon);

            if (item != null) {
                CreativeCategory category = itemWithIcon.getAnnotation(CreativeCategory.class);
                builder = builder.icon(() -> new ItemStack(item))
                        .title(Component.translatable(category.nameComponent()));
            }
        }

        builder = builder
                .displayItems((params, out) -> {
                    out.acceptAll(
                            items.stream()
                                    .map(cls -> ItemLoader.itemsRecord.get(cls))
                                    .filter(Objects::nonNull)
                                    .map(ItemStack::new)
                                    .toList()
                    );
                });

        CreativeModeTab tab = builder.build();
        itemGroups.put(id, tab);

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation("category", id), tab);
    }

    @Override
    public void build(App app) {
        registerAllItemGroups();
    }
}
