package mod.kensei.client.weapon;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import top.rgb39.kensei_client.annotation.CreativeCategory;
import top.rgb39.kensei_client.annotation.CustomItem;
import top.rgb39.kensei_client.item.GeckoItem;

@CreativeCategory
@CustomItem(name = "uchigatana")
public class Uchigatana extends GeckoItem {

    public Uchigatana() {
        super(new FabricItemSettings().fireproof().maxCount(1));
    }

}
