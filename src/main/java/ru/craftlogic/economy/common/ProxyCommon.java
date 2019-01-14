package ru.craftlogic.economy.common;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import ru.craftlogic.api.event.server.ServerAddManagersEvent;
import ru.craftlogic.api.network.AdvancedMessage;
import ru.craftlogic.api.network.AdvancedMessageHandler;
import ru.craftlogic.api.world.Player;
import ru.craftlogic.economy.EconomyManager;
import ru.craftlogic.economy.network.message.MessageBalance;
import ru.craftlogic.util.ReflectiveUsage;

import java.util.Random;

import static ru.craftlogic.economy.CraftEconomy.NETWORK;

@ReflectiveUsage
public class ProxyCommon extends AdvancedMessageHandler {
    public void preInit() {

    }

    public void init() {
        NETWORK.registerMessage(this::handleBalance, MessageBalance.class, Side.CLIENT);
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
        if (!living.world.isRemote && living instanceof EntityZombie && event.getSource() instanceof EntityDamageSource) {
            EntityDamageSource source = (EntityDamageSource) event.getSource();
            Random rand = new Random();
            if (source.damageType.equals("player") && rand.nextInt(5) == 0) {
                EntityPlayerMP killer = (EntityPlayerMP) source.getTrueSource();
                if (killer != null) {
                    Player player = Player.from(killer);
                    if (player != null) {
                        EconomyManager economyManager = (EconomyManager) player.getServer().getEconomyManager();
                        if (economyManager.isEnabled()) {
                            float money = economyManager.roundUpToFormat(rand.nextFloat() * 0.9F + 0.1F);
                            economyManager.give(player, money);
                        }
                    }
                }
            }
        }
    }
}
