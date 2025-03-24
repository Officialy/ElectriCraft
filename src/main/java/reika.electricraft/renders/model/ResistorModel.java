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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static reika.electricraft.ElectriCraft.MODID;

public class ResistorModel extends ResistorBaseModel {

    public static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(MODID, "textures/resistor.png");

    private final ModelPart band3b;
    private final ModelPart band2a;
    private final ModelPart band3a;
    private final ModelPart band1a;
    private final ModelPart band1b;
    private final ModelPart band2b;
    private final ModelPart root;
    private ArrayList<ResistorBand> bands;

    public ResistorModel(ModelPart modelPart) {
        super(modelPart);
        this.root = modelPart;

        this.band3b = modelPart.getChild("band3b");
        this.band2a = modelPart.getChild("band2a");
        this.band3a = modelPart.getChild("band3a");
        this.band1a = modelPart.getChild("band1a");
        this.band1b = modelPart.getChild("band1b");
        this.band2b = modelPart.getChild("band2b");
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
//
        root.addOrReplaceChild("band3b",
                CubeListBuilder.create()
                        .mirror(true)
                        .texOffs(22, 63)
                        .addBox(0, 0, 0, 9, 6, 2),
                PartPose.offsetAndRotation(-4.5F, 13.5F, 0, 0, 0, 0));

        root.addOrReplaceChild("band2a",
                CubeListBuilder.create()
                        .mirror(true)
                        .texOffs(0, 63)
                        .addBox(0, 0, 0, 7, 6, 2),
                PartPose.offsetAndRotation(-3.5F, 11.5F, -3, 0, 0, 0));

        root.addOrReplaceChild("band3a",
                CubeListBuilder.create()
                        .mirror(true)
                        .texOffs(0, 63)
                        .addBox(0, 0, 0, 7, 6, 2),
                PartPose.offsetAndRotation(-3.5F, 11.5F, 0, 0, 0, 0));

        root.addOrReplaceChild("band1a",
                CubeListBuilder.create()
                        .mirror(true)
                        .texOffs(0, 63)
                        .addBox(0, 0, 0, 7, 6, 2),
                PartPose.offsetAndRotation(-3.5F, 11.5F, -6, 0, 0, 0));

        root.addOrReplaceChild("band1b",
                CubeListBuilder.create()
                        .mirror(true)
                        .texOffs(22, 63)
                        .addBox(0, 0, 0, 9, 6, 2),
                PartPose.offsetAndRotation(-4.5F, 13.5F, -6, 0, 0, 0));

        root.addOrReplaceChild("band2b",
                CubeListBuilder.create()
                        .mirror(true)
                        .texOffs(22, 63)
                        .addBox(0, 0, 0, 9, 6, 2),
                PartPose.offsetAndRotation(-4.5F, 13.5F, -3, 0, 0, 0));


        return LayerDefinition.create(definition, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        root.render(stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    protected List<ResistorBand> getBands() {
        bands = new ArrayList<>();
        bands.add(new ResistorBand(1, band1a, band1b));
        bands.add(new ResistorBand(2, band2a, band2b));
        bands.add(new ResistorBand(3, band3a, band3b));
        return Collections.unmodifiableList(bands);
    }

    @Override
    public ResourceLocation getTexture() {
        return TEXTURE_LOCATION;
    }
}
