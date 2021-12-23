package ru.craftlogic.economy.common;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import ru.craftlogic.api.event.server.ServerAddManagersEvent;
import ru.craftlogic.api.network.AdvancedMessage;
import ru.craftlogic.api.network.AdvancedMessageHandler;
import ru.craftlogic.api.server.Server;
import ru.craftlogic.api.world.Player;
import ru.craftlogic.economy.EconomyManager;
import ru.craftlogic.economy.common.block.BlockTradingPost;
import ru.craftlogic.economy.common.tile.TileEntityTradingPost;
import ru.craftlogic.economy.network.message.MessageBalance;
import ru.craftlogic.util.ReflectiveUsage;

import java.util.Random;

import static ru.craftlogic.economy.CraftEconomy.NETWORK;

@ReflectiveUsage
public class ProxyCommon extends AdvancedMessageHandler {
    public static final Block TRADING_POST = new BlockTradingPost();

    public void preInit() {
    }

    public void init() {
        NETWORK.registerMessage(this::handleBalance, MessageBalance.class, Side.CLIENT);
        IForgeRegistry<Block> registry = GameRegistry.findRegistry(Block.class);
        registry.register(TRADING_POST);
        GameRegistry.registerTileEntity(TileEntityTradingPost.class, new ResourceLocation("craftlogic-economy", "trading_post"));
    }

    public void postInit() {

    }

    protected AdvancedMessage handleBalance(MessageBalance message, MessageContext context) {
        return null;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onServerAddManagers(ServerAddManagersEvent event) {
        event.addManager(EconomyManager.class, EconomyManager::new);
    }

    @SubscribeEvent
    public void onZombieKilled(LivingDeathEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        if (!living.world.isRemote && event.getSource() instanceof EntityDamageSource) {
            EconomyManager economyManager = (EconomyManager) Server.from(living.world.getMinecraftServer()).getEconomyManager();
            if (economyManager.isEnabled()) {
                ResourceLocation id = EntityList.getKey(living);
                float money = economyManager.drops.getOrDefault(id, 0f);
                if (money > 0) {
                    EntityDamageSource source = (EntityDamageSource) event.getSource();
                    if (source.damageType.equals("player") /*&& rand.nextInt(5) == 0*/) {
                        EntityPlayerMP killer = (EntityPlayerMP) source.getTrueSource();
                        if (killer != null) {
                            Player player = Player.from(killer);
                            if (player != null) {
                                economyManager.give(player, money);
                            }
                        }
                    }
                }
            }
        }
    }
}
