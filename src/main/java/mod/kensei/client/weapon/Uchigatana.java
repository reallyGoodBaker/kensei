package mod.kensei.client.weapon;

import net.minecraft.world.item.Item;
import top.rgb39.kensei_client.annotation.CreativeCategory;
import top.rgb39.kensei_client.annotation.CustomItem;
import top.rgb39.kensei_client.item.GeckoItem;

@CreativeCategory
@CustomItem(name = "uchigatana")
public class Uchigatana extends GeckoItem {

    public Uchigatana() {
        super(new Item.Properties().fireResistant().stacksTo(1));
    }

}
