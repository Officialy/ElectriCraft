package reika.electricraft.renders.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.entity.BlockEntity;
import reika.electricraft.base.BlockEntityResistorBase;
import reika.rotarycraft.base.RotaryModelBase;

import java.util.ArrayList;
import java.util.List;

import static reika.electricraft.ElectriCraft.MODID;

public abstract class ResistorBaseModel extends RotaryModelBase {

    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/resistor.png");

    private final ModelPart shape1;
    private final ModelPart shape2a;
    private final ModelPart shape2;
    private final ModelPart shape3a;
    private final ModelPart shape3;
    private final ModelPart shape3b;
    private final ModelPart shape3c;
    private final ModelPart root;

    public ResistorBaseModel(ModelPart modelPart) {
        super(RenderType::entityCutout);
        this.root = modelPart;

        this.shape1 = modelPart.getChild("shape1");
        this.shape2a = modelPart.getChild("shape2a");
        this.shape2 = modelPart.getChild("shape2");
        this.shape3a = modelPart.getChild("shape3a");
        this.shape3 = modelPart.getChild("shape3");
        this.shape3b = modelPart.getChild("shape3b");
        this.shape3c = modelPart.getChild("shape3c");
    }

    // Grab the parts in the constructor if you need them
    public static LayerDefinition createLayer() {
        MeshDefinition definition = new MeshDefinition();
        PartDefinition root = definition.getRoot();

        root.addOrReplaceChild("shape1",
                CubeListBuilder.create()
                        .mirror(true)
                        .texOffs(63, 0)
                        .addBox(0, 0, 0, 12, 5, 16),
                PartPose.offsetAndRotation(-6, 19, -8, 0, 0, 0));

        root.addOrReplaceChild("shape2a",
                CubeListBuilder.create()
                        .mirror(true)
                        .texOffs(63, 23)
                        .addBox(0, 0, 0, 6, 2, 16),
                PartPose.offsetAndRotation(-3, 12, -8, 0, 0, 0));

        root.addOrReplaceChild("shape2",
                CubeListBuilder.create()
                        .mirror(true)
                        .texOffs(0, 18)
                        .addBox(0, 0, 0, 8, 5, 16),
                PartPose.offsetAndRotation(-4, 14, -8, 0, 0, 0));

        root.addOrReplaceChild("shape3a",
                CubeListBuilder.create()
                        .mirror(true)
                        .texOffs(0, 41)
                        .addBox(0, 0, 0, 8, 1, 15),
                PartPose.offsetAndRotation(-4, 12.5F, -7.5F, 0, 0, 0));

        root.addOrReplaceChild("shape3",
                CubeListBuilder.create()
                        .mirror(true)
                        .texOffs(0, 0)
                        .addBox(0, 0, 0, 10, 1, 15),
                PartPose.offsetAndRotation(-5, 16, -7.5F, 0, 0, 0));

        root.addOrReplaceChild("shape3b",
                CubeListBuilder.create()
                        .mirror(true)
                        .texOffs(0, 0)
                        .addBox(0, 0, 0, 10, 1, 15),
                PartPose.offsetAndRotation(-5, 14.5F, -7.5F, 0, 0, 0));

        root.addOrReplaceChild("shape3c",
                CubeListBuilder.create()
                        .mirror(true)
                        .texOffs(0, 0)
                        .addBox(0, 0, 0, 10, 1, 15),
                PartPose.offsetAndRotation(-5, 17.5F, -7.5F, 0, 0, 0));

        return LayerDefinition.create(definition, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        root.render(stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    protected abstract List<ResistorBand> getBands();

    @Override
    public final void renderAll(PoseStack stack, VertexConsumer tex, int light, BlockEntity te, ArrayList<?> li, float phi, float theta) {
        shape1.render(stack, tex, 0, 0); //todo ints
        shape2a.render(stack, tex, 0, 0); //todo ints
        shape2.render(stack, tex, 0, 0); //todo ints
        shape3a.render(stack, tex, 0, 0); //todo ints
        shape3.render(stack, tex, 0, 0); //todo ints
        shape3b.render(stack, tex, 0, 0); //todo ints
        shape3c.render(stack, tex, 0, 0); //todo ints

        List<ResistorBand> bands = this.getBands();
        for (int i = 0; i < bands.size(); i++) {
            ResistorBand rb = bands.get(i);
            rb.color = (BlockEntityResistorBase.ColorBand) li.get(i);
            rb.color.renderColor.setGLColorBlend();
            rb.partA.render(stack, tex, 0,0); //todo both ints
            rb.partB.render(stack, tex, 0,0);
        }
    }

    protected static class ResistorBand {

        private final int index;

        private BlockEntityResistorBase.ColorBand color;

        private final ModelPart partA;
        private final ModelPart partB;

        protected ResistorBand(int i, ModelPart p1, ModelPart p2) {
            index = i;
            partA = p1;
            partB = p2;
        }

    }

}
