package ru.craftlogic.economy.common.commands;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.command.CommandException;
import ru.craftlogic.api.command.CommandBase;
import ru.craftlogic.api.command.CommandContext;
import ru.craftlogic.api.text.Text;
import ru.craftlogic.api.world.OfflinePlayer;
import ru.craftlogic.api.world.Player;
import ru.craftlogic.economy.EconomyManager;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;

public class CommandBalanceTop extends CommandBase {
    public CommandBalanceTop() {
        super("baltop", 1,
            "",
            "<size>"
        );
    }

    @Override
    protected void execute(CommandContext ctx) throws Throwable {
        EconomyManager economyManger = (EconomyManager) ctx.server().getEconomyManager();
        if (!economyManger.isEnabled()) {
            throw new CommandException("commands.economy.disabled");
        }

        int size = ctx.getIfPresent("size", a -> a.asInt(10, 50)).orElse(10);

        List<Object2FloatMap.Entry<UUID>> top = economyManger.getTop(size);

        if (top.isEmpty()) {
            throw new CommandException("commands.baltop.empty");
        }

        ctx.sendMessage(
            Text.translation("commands.baltop.header").yellow()
        );

        for (int i = 0; i < top.size(); i++) {
            Object2FloatMap.Entry<UUID> e = top.get(i);
            OfflinePlayer pl = ctx.server().getPlayerManager().getOffline(e.getKey());
            if (pl != null) {
                ctx.sendMessage(
                    Text.translation("commands.baltop.entry").yellow()
                        .arg(i + 1)
                        .arg(pl.getName(), Text::gold)
                        .arg(economyManger.format(e.getFloatValue()).gold())
                );
            }
        }
    }
}
