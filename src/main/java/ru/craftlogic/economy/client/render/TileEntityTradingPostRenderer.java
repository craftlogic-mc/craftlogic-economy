package ru.craftlogic.economy.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;
import ru.craftlogic.economy.common.tile.TileEntityTradingPost;

import java.util.Random;

public class TileEntityTradingPostRenderer extends TileEntitySpecialRenderer<TileEntityTradingPost> {
    private final RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
    private final Random random = new Random();

    @Override
    public void render(TileEntityTradingPost post, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ITextComponent tooltip = post.getDisplayName();
        if (tooltip != null && this.rendererDispatcher.cameraHitResult != null && post.getPos().equals(this.rendererDispatcher.cameraHitResult.getBlockPos())) {
            setLightmapDisabled(true);
            drawNameplate(post, tooltip.getFormattedText(), x, y, z, 12);
            setLightmapDisabled(false);
        }

        ItemStack item = new ItemStack(Items.EMERALD);
        int seed = item.isEmpty() ? 187 : Item.getIdFromItem(item.getItem()) + item.getMetadata();
        this.random.setSeed(seed);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5F, y + 0.75F, z + 0.5F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        IBakedModel model = this.itemRenderer.getItemModelWithOverrides(item, post.getWorld(), null);
        GlStateManager.pushMatrix();
        IBakedModel transformedModel = ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.GROUND, false);
        this.itemRenderer.renderItem(item, transformedModel);
        GlStateManager.popMatrix();

        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
