package com.abstractphil.enchantments.menu;
import com.redmancometh.reditems.RedItems;
import com.redmancometh.reditems.abstraction.TemporaryEffect;
import com.redmancometh.reditems.config.RedItemsConfig;
import com.redmancometh.reditems.mediator.EnchantManager;
import com.redmancometh.reditems.storage.AttachmentData;
import com.redmancometh.reditems.storage.SimpleContainer;
import com.redmancometh.reditems.storage.TemporaryEffectData;
import com.redmancometh.redmenus.absraction.Menu;
import com.redmancometh.redmenus.menus.MenuButton;
import com.redmancometh.warcore.util.ItemUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;

public class PassiveMenu extends Menu {
    private SimpleContainer itemData;

    public PassiveMenu(SimpleContainer itemData) {
        super("Passive Enchant Token Shop",
                RedItems.getInstance().cfg().getBuffTemplate(),
                RedItems.getInstance().cfg().getBuffsSize());
        this.itemData = itemData;
        this.setButtons();
    }

    public void setButtons() {
        EnchantManager em = RedItems.getInstance().getEnchantManager();
        this.itemData.getBuffData().forEach((buff) -> {
            MenuButton button = new MenuButton((p) -> {
                return this.getBuffIcon(buff);
            });
            this.setButton(this.getNextBlankIndex(), button);
        });
        this.itemData.getAttachData().forEach((attachment) -> {
            MenuButton button = new MenuButton((p) -> {
                return this.getAttachIcon(attachment);
            });
            button.setClickAction((click, player) -> {
                player.getInventory().addItem(new ItemStack[]{attachment.getAttachment()});
            });
            this.setButton(this.getNextBlankIndex(), button);
        });
        this.itemData.getTransferData();
    }

    public ItemStack getAttachIcon(AttachmentData data) {
        RedItemsConfig cfg = RedItems.getInstance().cfg();
        return ItemUtil.addLore(data.getAttachment(), ChatColor.GREEN + "Click to remove");
    }

    public ItemStack getBuffIcon(TemporaryEffectData data) {
        RedItemsConfig cfg = RedItems.getInstance().cfg();
        TemporaryEffect effect = (TemporaryEffect)data.getEffect();
        return effect.getBuffIcon(data.getLevel(), data.getDuration());
    }
}
