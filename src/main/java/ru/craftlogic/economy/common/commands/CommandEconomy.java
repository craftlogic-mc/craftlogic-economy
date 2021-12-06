package ru.craftlogic.economy.common.commands;

import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import ru.craftlogic.api.command.CommandBase;
import ru.craftlogic.api.command.CommandContext;
import ru.craftlogic.economy.EconomyManager;

public class CommandEconomy extends CommandBase {

    public CommandEconomy() {
        super("economy", 3, "drops <id:EntityId> <money>");
    }

    @Override
    protected void execute(CommandContext ctx) throws Throwable {
        EconomyManager economyManger = (EconomyManager) ctx.server().getEconomyManager();
        if (!economyManger.isEnabled()) {
            throw new CommandException("commands.economy.disabled");
        }
        String id = ctx.get("id").asString();
        float money = ctx.get("money").asFloat(0, 1000);
        Class<? extends Entity> type = EntityList.getClass(new ResourceLocation(id));
        if (type == null) {
            throw new CommandException("commands.generic.entity.invalidType", id);
        }
        economyManger.drops.put(new ResourceLocation(id), money);
        ctx.sendNotification(new TextComponentTranslation("commands.economy.drops.set", id, money));
        economyManger.save(true);

    }
}
