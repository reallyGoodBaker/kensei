package mod.kensei.client.weapon;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import top.rgb39.kensei_client.annotation.CreativeCategory;
import top.rgb39.kensei_client.annotation.CustomItem;
import top.rgb39.kensei_client.item.GeckoItem;

@CreativeCategory(useAsIcon = true)
@CustomItem(name = "ootachi")
public class Ootachi extends GeckoItem {

    public Ootachi() {
        super(new FabricItemSettings().fireproof().maxCount(1));
    }

}
