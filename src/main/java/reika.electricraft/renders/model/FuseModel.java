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

import static reika.electricraft.ElectriCraft.MODID;

public class FuseModel extends Model
{
    private final ModelPart shape1a;
    private final ModelPart shape3;
    private final ModelPart shape3a;
    private final ModelPart shape4e;
    private final ModelPart shape4f;
    private final ModelPart shape1;
    private final ModelPart shape1c;
    private final ModelPart shape2;
    private final ModelPart shape4;
    private final ModelPart shape4a;
    private final ModelPart shape4b;
    private final ModelPart shape5b;
    private final ModelPart shape5;
    private final ModelPart root;
    
    public FuseModel(ModelPart modelPart) {
        super(RenderType::entityCutout);
        this.root = modelPart;
        
        this.shape1a = modelPart.getChild("shape1a");
        this.shape3 = modelPart.getChild("shape3");
        this.shape3a = modelPart.getChild("shape3a");
        this.shape4e = modelPart.getChild("shape4e");
        this.shape4f = modelPart.getChild("shape4f");
        this.shape1 = modelPart.getChild("shape1");
        this.shape1c = modelPart.getChild("shape1c");
        this.shape2 = modelPart.getChild("shape2");
        this.shape4 = modelPart.getChild("shape4");
        this.shape4a = modelPart.getChild("shape4a");
        this.shape4b = modelPart.getChild("shape4b");
        this.shape5b = modelPart.getChild("shape5b");
        this.shape5 = modelPart.getChild("shape5");
    }

    // Grab the parts in the constructor if you need them
    
    public static LayerDefinition createLayer() {
        MeshDefinition definition = new MeshDefinition();
        PartDefinition root = definition.getRoot();
        
        root.addOrReplaceChild("shape1a",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(0, 0)
                .addBox(0, 0, 0, 12, 5, 16),
            PartPose.offsetAndRotation(-6, 19, -8, 0, 0, 0));
        
        root.addOrReplaceChild("shape3",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(42, 42)
                .addBox(3, -1, 0, 2, 1, 15),
            PartPose.offsetAndRotation(-2.5F, 9.5F, -7.5F, 0, 0, 2.356194F));
        
        root.addOrReplaceChild("shape3a",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(0, 42)
                .addBox(3, 0, 0, 2, 1, 15),
            PartPose.offsetAndRotation(2.5F, 9.5F, -7.5F, 0, 0, 0.7853982F));
        
        root.addOrReplaceChild("shape4e",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(52, 31)
                .addBox(0, 0, 0, 10, 7, 1),
            PartPose.offsetAndRotation(-5, 12, 7, 0, 0, 0));
        
        root.addOrReplaceChild("shape4f",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(52, 21)
                .addBox(0, 0, 0, 10, 7, 1),
            PartPose.offsetAndRotation(-5, 12, -8, 0, 0, 0));
        
        root.addOrReplaceChild("shape1",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(90, 45)
                .addBox(0, 0, 0, 1, 6, 16),
            PartPose.offsetAndRotation(-6, 13, -8, 0, 0, 0));
        
        root.addOrReplaceChild("shape1c",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(90, 21)
                .addBox(0, 0, 0, 1, 6, 16),
            PartPose.offsetAndRotation(5, 13, -8, 0, 0, 0));
        
        root.addOrReplaceChild("shape2",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(58, 0)
                .addBox(0, 0, 0, 9, 1, 15),
            PartPose.offsetAndRotation(-4.5F, 11.5F, -7.5F, 0, 0, 0));
        
        root.addOrReplaceChild("shape4",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(33, 60)
                .addBox(0, 0, 0, 2, 1, 14),
            PartPose.offsetAndRotation(-1, 15, -7, 0, 0, 0));
        
        root.addOrReplaceChild("shape4a",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(0, 60)
                .addBox(0, 0, 0, 1, 1, 14),
            PartPose.offsetAndRotation(2, 15, -7, 0, 0, 0));
        
        root.addOrReplaceChild("shape4b",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(0, 60)
                .addBox(0, 0, 0, 1, 1, 14),
            PartPose.offsetAndRotation(-3, 15, -7, 0, 0, 0));
        
        root.addOrReplaceChild("shape5b",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(0, 77)
                .addBox(0, 0, 0, 13, 1, 17),
            PartPose.offsetAndRotation(-6.5F, 22, -8.5F, 0, 0, 0));
        
        root.addOrReplaceChild("shape5",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(0, 77)
                .addBox(0, 0, 0, 13, 1, 17),
            PartPose.offsetAndRotation(-6.5F, 20, -8.5F, 0, 0, 0));
        
        return LayerDefinition.create(definition, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    
        root.render(stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
