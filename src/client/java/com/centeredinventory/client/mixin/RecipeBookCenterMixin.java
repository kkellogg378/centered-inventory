package com.centeredinventory.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.centeredinventory.CenteredInventoryConfig;

import java.lang.reflect.Field;

import java.util.List;

@Mixin(RecipeBookComponent.class)
public class RecipeBookCenterMixin {
    private static final int RECIPE_BOOK_WIDTH = 177;

    @Shadow
    private boolean widthTooNarrow;

    @Shadow
    private int xOffset;

    @Shadow
    public boolean isVisible() {
        throw new UnsupportedOperationException();
    }

    // Center the inventory screen when the recipe book is visible
    @Inject(method = "updateScreenPosition(II)I", at = @At("RETURN"), cancellable = true)
    private void centerInventoryScreen(int width, int imageWidth, CallbackInfoReturnable<Integer> cir) {
        if (!CenteredInventoryConfig.get().enabled) return;
        
        if (this.isVisible() && !this.widthTooNarrow) {
            int centeredLeft = (width - RECIPE_BOOK_WIDTH + 1) / 2;
            cir.setReturnValue(centeredLeft);
        }
    }

    // Reposition the recipe book when inventory is opened
    @Inject(method = "init", at = @At("TAIL"))
    private void repositionRecipeBook(int width, int height, Minecraft minecraft, boolean widthTooNarrow, CallbackInfo ci) {
        if (!CenteredInventoryConfig.get().enabled) return;
        
        if (!this.isVisible() || widthTooNarrow) return;
        this.xOffset = this.xOffset + (RECIPE_BOOK_WIDTH + 1) / 2 - 12;
        applyWidgetOffset((RecipeBookComponent)(Object)this, (RECIPE_BOOK_WIDTH + 1) / 2 - 12);
    }
    
    // Reposition the recipe book when toggling recipe book visibility
    @Inject(method = "setVisible(Z)V", at = @At("TAIL"))
    private void onSetVisible(boolean visible, CallbackInfo ci) {
        if (!CenteredInventoryConfig.get().enabled) return;
        
        if (!visible || this.widthTooNarrow) return;
        this.xOffset = 86 + (RECIPE_BOOK_WIDTH + 1) / 2 - 12;
        applyWidgetOffset((RecipeBookComponent)(Object)this, (RECIPE_BOOK_WIDTH + 1) / 2 - 12);

        // System.out.println("[CenteredInventory] RecipeBookComponent methods:");
        // for (java.lang.reflect.Method m : net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen.class.getDeclaredMethods()) {
        //     if (m.getName().equals("hasClickedOutside")) {
        //         System.out.println("[CenteredInventory] AbstractRecipeBookScreen.hasClickedOutside return: " + m.getReturnType().getName());
        //         for (Class<?> p : m.getParameterTypes()) {
        //             System.out.println("[CenteredInventory] AbstractRecipeBookScreen.hasClickedOutside param: " + p.getName());
        //         }
        //     }
        // }
    }

    private void applyWidgetOffset(RecipeBookComponent self, int delta) {
        if (!CenteredInventoryConfig.get().enabled) return;
        
        try {
            Field tabButtonsField = RecipeBookComponent.class.getDeclaredField("tabButtons");
            tabButtonsField.setAccessible(true);
            List<net.minecraft.client.gui.components.AbstractWidget> tabs =
                (List<net.minecraft.client.gui.components.AbstractWidget>) tabButtonsField.get(self);
            for (net.minecraft.client.gui.components.AbstractWidget tab : tabs) {
                tab.setX(tab.getX() - delta);
            }

            Field filterButtonField = RecipeBookComponent.class.getDeclaredField("filterButton");
            filterButtonField.setAccessible(true);
            net.minecraft.client.gui.components.AbstractWidget filterBtn =
                (net.minecraft.client.gui.components.AbstractWidget) filterButtonField.get(self);
            filterBtn.setX(filterBtn.getX() - delta);

            Field searchBoxField = RecipeBookComponent.class.getDeclaredField("searchBox");
            searchBoxField.setAccessible(true);
            net.minecraft.client.gui.components.AbstractWidget searchBox =
                (net.minecraft.client.gui.components.AbstractWidget) searchBoxField.get(self);
            searchBox.setX(searchBox.getX() - delta);

            Field recipeBookPageField = RecipeBookComponent.class.getDeclaredField("recipeBookPage");
            recipeBookPageField.setAccessible(true);
            Object recipeBookPage = recipeBookPageField.get(self);
            Class<?> pageClass = recipeBookPage.getClass();

            Field buttonsField = pageClass.getDeclaredField("buttons");
            buttonsField.setAccessible(true);
            List<net.minecraft.client.gui.components.AbstractWidget> buttons =
                (List<net.minecraft.client.gui.components.AbstractWidget>) buttonsField.get(recipeBookPage);
            for (net.minecraft.client.gui.components.AbstractWidget button : buttons) {
                button.setX(button.getX() - delta);
            }

            Field forwardButtonField = pageClass.getDeclaredField("forwardButton");
            forwardButtonField.setAccessible(true);
            net.minecraft.client.gui.components.AbstractWidget forwardButton =
                (net.minecraft.client.gui.components.AbstractWidget) forwardButtonField.get(recipeBookPage);
            forwardButton.setX(forwardButton.getX() - delta);

            Field backButtonField = pageClass.getDeclaredField("backButton");
            backButtonField.setAccessible(true);
            net.minecraft.client.gui.components.AbstractWidget backButton =
                (net.minecraft.client.gui.components.AbstractWidget) backButtonField.get(recipeBookPage);
            backButton.setX(backButton.getX() - delta);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fix the recipe book closing when pressing ESC
    @Inject(method = "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z", at = @At("HEAD"), cancellable = true)
    private void fixKeyPressed(net.minecraft.client.input.KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (!CenteredInventoryConfig.get().enabled) return;
        
        if (event.key() == 256) { // GLFW_KEY_ESCAPE
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    // Prevent the recipe book from closing when clicking recipes

    private boolean isHandlingMouseClick = false;

    @Inject(method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z", at = @At("HEAD"))
    private void beforeMouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (!CenteredInventoryConfig.get().enabled) return;
        
        isHandlingMouseClick = true;
    }

    @Inject(method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z", at = @At("RETURN"))
    private void afterMouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (!CenteredInventoryConfig.get().enabled) return;
        
        isHandlingMouseClick = false;
    }

    @Inject(method = "setVisible(Z)V", at = @At("HEAD"), cancellable = true)
    private void guardSetVisible(boolean visible, CallbackInfo ci) {
        if (!CenteredInventoryConfig.get().enabled) return;
        
        if (!visible && isHandlingMouseClick) {
            ci.cancel();
        }
    }
}