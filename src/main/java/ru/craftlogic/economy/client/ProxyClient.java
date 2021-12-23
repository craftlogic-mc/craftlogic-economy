package ru.craftlogic.economy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ru.craftlogic.api.CraftSounds;
import ru.craftlogic.api.CraftTileEntities;
import ru.craftlogic.api.network.AdvancedMessage;
import ru.craftlogic.api.text.Text;
import ru.craftlogic.economy.client.render.TileEntityTradingPostRenderer;
import ru.craftlogic.economy.common.ProxyCommon;
import ru.craftlogic.economy.common.tile.TileEntityTradingPost;
import ru.craftlogic.economy.network.message.MessageBalance;
import ru.craftlogic.util.ReflectiveUsage;

@ReflectiveUsage
public class ProxyClient extends ProxyCommon {
    private final Minecraft client = FMLClientHandler.instance().getClient();
    private Balance balance;

    @Override
    public void preInit() {
        super.preInit();
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void postInit() {
        super.postInit();
        CraftTileEntities.registerTileEntityRenderer(TileEntityTradingPost.class, TileEntityTradingPostRenderer::new);
    }

    @Override
    protected AdvancedMessage handleBalance(MessageBalance message, MessageContext context) {
        syncTask(context, () -> {
            Balance oldBalance = this.balance;
            this.balance = new Balance(message);
            if (oldBalance != null) {
                int d = oldBalance.compareTo(this.balance);
                EntityPlayer player = getPlayer(context);
                if (d != 0) {
                    SoundEvent sound = d < 0 ? CraftSounds.BALANCE_ADD : CraftSounds.BALANCE_SUBTRACT;
                    player.playSound(sound, 1F, 0.7F + player.world.rand.nextFloat() * 0.3F);
                }
            }
        });
        return null;
    }

    @SubscribeEvent
    public void onTextRender(RenderGameOverlayEvent.Text event) {
        if (this.balance != null && this.client.currentScreen == null) {
            this.balance.renderTextOverlay(this.client, event);
        }
    }

    private class Balance implements Comparable<Balance> {
        private final float balance;
        private final String currency, format;
        private final ITextComponent text;

        private Balance(MessageBalance message) {
            this.balance = message.getBalance();
            this.currency = message.getCurrency();
            this.format = message.getFormat();
            this.text = Text.translation("tooltip.balance")
                .arg(String.format(this.format, this.balance))
                .arg(this.currency)
                .build();
        }

        public void renderTextOverlay(Minecraft client, RenderGameOverlayEvent.Text event) {
            ScaledResolution resolution = event.getResolution();
            FontRenderer fontRenderer = client.fontRenderer;
            int x = 5;
            int y = resolution.getScaledHeight() - fontRenderer.FONT_HEIGHT - 5;
            fontRenderer.drawString(this.text.getFormattedText(), x, y, 0xFFFFFF, true);
        }

        @Override
        public int compareTo(Balance o) {
            return Float.compare(this.balance, o.balance);
        }
    }
}
