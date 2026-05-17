package com.centeredinventory.client;

import com.centeredinventory.CenteredInventoryConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClothConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.translatable("config.centered_inventory.title"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.centered_inventory.category.general"));

        general.addEntry(entryBuilder
            .startBooleanToggle(
                Component.translatable("config.centered_inventory.enabled"),
                CenteredInventoryConfig.get().enabled
            )
            .setDefaultValue(true)
            .setTooltip(Component.translatable("config.centered_inventory.enabled.tooltip"))
            .setSaveConsumer(val -> CenteredInventoryConfig.get().enabled = val)
            .build()
        );

        builder.setSavingRunnable(CenteredInventoryConfig::save);
        return builder.build();
    }
}