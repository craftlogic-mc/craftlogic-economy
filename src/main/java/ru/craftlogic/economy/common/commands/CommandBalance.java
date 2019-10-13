package ru.craftlogic.economy.common.commands;

import net.minecraft.command.CommandException;
import ru.craftlogic.api.command.CommandBase;
import ru.craftlogic.api.command.CommandContext;
import ru.craftlogic.api.text.Text;
import ru.craftlogic.api.world.OfflinePlayer;
import ru.craftlogic.api.world.Player;
import ru.craftlogic.economy.EconomyManager;

import java.util.Collections;

import static java.util.Arrays.asList;

public class CommandBalance extends CommandBase {
    public CommandBalance() {
        super("balance", 0, asList(
            new Syntax("", "commands.balance"),
            new Syntax("<target:OfflinePlayer>", "commands.balance.other")
        ));
        Collections.addAll(aliases, "bal", "money");
    }

    @Override
    protected void execute(CommandContext ctx) throws Throwable {
        EconomyManager economyManger = (EconomyManager) ctx.server().getEconomyManager();
        if (!economyManger.isEnabled()) {
            throw new CommandException("commands.economy.disabled");
        }

        OfflinePlayer target = ctx.has("target") ? ctx.get("target").asOfflinePlayer() : ctx.senderAsPlayer();
        float balance = economyManger.getBalance(target);
        Text<?, ?> a = economyManger.format(balance).gold();

        if (ctx.sender() instanceof Player && ((Player) ctx.sender()).getId().equals(target.getId())) {
            ctx.sendMessage(Text.translation("commands.balance").yellow().arg(a));
        } else if (ctx.checkPermission(true, "commands.balance.other", 2)) {
            ctx.sendMessage(
                Text.translation("commands.balance.other").yellow()
                    .arg(target.getName(), Text::gold)
                    .arg(a)
            );
        }
    }
}
