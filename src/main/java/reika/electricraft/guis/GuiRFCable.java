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
import reika.dragonapi.libraries.mathsci.ReikaEngLibrary;
import reika.dragonapi.libraries.mathsci.ReikaMathLibrary;
import reika.dragonapi.libraries.rendering.ReikaGuiAPI;
import reika.electricraft.ElectriCraft;
import reika.electricraft.blockentities.modinterface.BlockEntityRFCable;
import reika.electricraft.registry.ElectriPackets;
import reika.rotarycraft.gui.container.machine.BlankContainer;

/**
 * RF cable throughput-limit screen: one number field caps the RF/t the whole cable network will
 * move (0 = unlimited). The value is pushed to the server through the DragonAPI packet channel
 * (ElectriPackets.RFCABLE); the network then clamps every insert/extract to it, as in 1.7.10.
 */
public class GuiRFCable extends AbstractContainerScreen<BlankContainer<BlockEntityRFCable>> {

    private final BlockEntityRFCable cable;
    private EditBox input;

    public GuiRFCable(BlankContainer<BlockEntityRFCable> container, Inventory inv, Component title) {
        super(container, inv, title, 176, 100);
        cable = container.tile;
    }

    @Override
    protected void init() {
        super.init();
        input = new EditBox(font, leftPos + 48, topPos + 42, 80, 16, Component.literal("Limit"));
        input.setMaxLength(10);
        input.setValue(String.valueOf(cable.getRFLimit()));
        addRenderableWidget(input);

        addRenderableWidget(Button.builder(Component.literal("Apply"), b -> this.apply())
                .bounds(leftPos + 58, topPos + 66, 60, 20).build());
    }

    private void apply() {
        int limit;
        try {
            limit = Integer.parseInt(input.getValue().trim());
        } catch (NumberFormatException e) {
            return;
        }
        if (limit < 0)
            return;
        ReikaPacketHelper.sendPacketToServer(ElectriCraft.packetChannel, ElectriPackets.RFCABLE.ordinal(), cable, limit);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF3F3F3F);
        graphics.fill(leftPos + 2, topPos + 2, leftPos + imageWidth - 2, topPos + imageHeight - 2, 0xFFC6C6C6);
        if (input != null)
            input.extractRenderState(graphics, mouseX, mouseY, partial);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        ReikaGuiAPI api = ReikaGuiAPI.instance;
        api.drawCenteredStringNoShadow(graphics, font, "RF Transfer Limit", imageWidth / 2, 8, 0x202020);
        int lim = cable.getRFLimit();
        String cur = lim == 0 ? "Current: Unlimited"
                : String.format("Current: %.2f %sRF/t", ReikaMathLibrary.getThousandBase(lim), ReikaEngLibrary.getSIPrefix(lim));
        api.drawCenteredStringNoShadow(graphics, font, cur, imageWidth / 2, 22, 0x202020);
    }
}
