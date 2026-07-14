/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.guis;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import reika.dragonapi.libraries.io.ReikaPacketHelper;
import reika.dragonapi.libraries.rendering.ReikaGuiAPI;
import reika.electricraft.ElectriCraft;
import reika.electricraft.blockentities.BlockEntityTransformer;
import reika.electricraft.registry.ElectriPackets;
import reika.rotarycraft.gui.container.machine.BlankContainer;

/**
 * Transformer ratio screen: two number fields set the primary:secondary turns ratio (n1:n2),
 * which the transformer uses to scale voltage up (secondary > primary) or down. The typed values
 * are pushed to the server through the DragonAPI packet channel (ElectriPackets.TRANSFORMER),
 * exactly as the 1.7.10 GUI did.
 */
public class GuiTransformer extends AbstractContainerScreen<BlankContainer<BlockEntityTransformer>> {

    private final BlockEntityTransformer trans;
    private EditBox input1;
    private EditBox input2;

    public GuiTransformer(BlankContainer<BlockEntityTransformer> container, Inventory inv, Component title) {
        super(container, inv, title, 176, 100);
        trans = container.tile;
    }

    @Override
    protected void init() {
        super.init();
        input1 = new EditBox(font, leftPos + 40, topPos + 40, 32, 16, Component.literal("Primary"));
        input1.setMaxLength(4);
        input1.setValue(String.valueOf(trans.getN1()));
        addRenderableWidget(input1);

        input2 = new EditBox(font, leftPos + 104, topPos + 40, 32, 16, Component.literal("Secondary"));
        input2.setMaxLength(4);
        input2.setValue(String.valueOf(trans.getN2()));
        addRenderableWidget(input2);

        addRenderableWidget(Button.builder(Component.literal("Apply"), b -> this.apply())
                .bounds(leftPos + 58, topPos + 66, 60, 20).build());
    }

    private void apply() {
        int n1 = parse(input1.getValue());
        int n2 = parse(input2.getValue());
        if (n1 <= 0 || n2 <= 0)
            return;
        ReikaPacketHelper.sendPacketToServer(ElectriCraft.packetChannel, ElectriPackets.TRANSFORMER.ordinal(), trans, n1, n2);
    }

    private static int parse(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // extractBackground is in screen space (offset by leftPos/topPos).
    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF3F3F3F);
        graphics.fill(leftPos + 2, topPos + 2, leftPos + imageWidth - 2, topPos + imageHeight - 2, 0xFFC6C6C6);
        if (input1 != null)
            input1.extractRenderState(graphics, mouseX, mouseY, partial);
        if (input2 != null)
            input2.extractRenderState(graphics, mouseX, mouseY, partial);
    }

    // extractLabels is in GUI-local space (already translated by leftPos/topPos).
    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        ReikaGuiAPI api = ReikaGuiAPI.instance;
        api.drawCenteredStringNoShadow(graphics, font, "Transformer Ratio", imageWidth / 2, 8, 0x202020);
        api.drawCenteredStringNoShadow(graphics, font, "Current  " + trans.getN1() + " : " + trans.getN2(), imageWidth / 2, 22, 0x202020);
        graphics.text(font, ":", imageWidth / 2 - 2, 44, 0x202020, false);
    }
}
