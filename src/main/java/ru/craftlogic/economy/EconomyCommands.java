package ru.craftlogic.economy;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.command.CommandException;
import ru.craftlogic.api.command.Command;
import ru.craftlogic.api.command.CommandContext;
import ru.craftlogic.api.command.CommandRegistrar;
import ru.craftlogic.api.text.Text;
import ru.craftlogic.api.world.OfflinePlayer;
import ru.craftlogic.api.world.Player;

import java.util.List;
import java.util.UUID;

public class EconomyCommands implements CommandRegistrar {
    @Command(name = "pay", syntax = "<receiver:Player> <amount>", serverOnly = true, opLevel = 0)
    public static void commandPay(CommandContext ctx) throws CommandException {
        EconomyManager economyManger = (EconomyManager) ctx.server().getEconomyManager();
        if (!economyManger.isEnabled()) {
            throw new CommandException("commands.economy.disabled");
        }

        Player sender = ctx.senderAsPlayer();
        Player receiver = ctx.get("receiver").asPlayer();

        if (sender == receiver || sender.getId().equals(receiver.getId())) {
            throw new CommandException("commands.pay.self");
        }

        float amount = economyManger.roundUpToFormat(ctx.get("amount").asFloat(0.1F, 5000));
        float senderBalance = economyManger.getBalance(sender);

        if (senderBalance >= amount) {
            amount = economyManger.take(sender, amount);
            economyManger.give(receiver, amount);
            Text<?, ?> a = economyManger.format(amount);
            sender.sendMessage(
                Text.translation("commands.pay.sent")
                    .arg(a.gold())
                    .arg(receiver.getName(), Text::gold)
            );
            receiver.sendMessage(
                Text.translation("commands.pay.received")
                    .arg(a.gold())
                    .arg(sender.getName(), Text::gold)
            );
        } else {
            Text<?, ?> a = economyManger.format(amount - senderBalance);
            throw new CommandException("commands.economy.not_enough", a);
        }
    }

    @Command(
        name = "balance",
        aliases = {"bal", "money"},
        syntax = {
            "",
            "<target:OfflinePlayer>"
        },
        serverOnly = true,
        opLevel = 0
    )
    public static void commandBal(CommandContext ctx) throws CommandException {
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

    @Command(name = "baltop", syntax = {
        "",
        "<size>"
    }, serverOnly = true, opLevel = 1)
    public static void commandBalTop(CommandContext ctx) throws CommandException {
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

    @Command(name = "economy", syntax = {
        "give|take|set <target:OfflinePlayer> <amount>",
    }, aliases = "eco", serverOnly = true, opLevel = 3)
    public static void commandEco(CommandContext ctx) throws CommandException {
        EconomyManager economyManger = (EconomyManager) ctx.server().getEconomyManager();
        if (!economyManger.isEnabled()) {
            throw new CommandException("commands.economy.disabled");
        }

        OfflinePlayer target = ctx.get("target").asOfflinePlayer();
        float amount = economyManger.roundUpToFormat(ctx.get("amount").asFloat(0, Float.MAX_VALUE));

        switch (ctx.action(0)) {
            case "give": {
                economyManger.give(target, amount);
                Text<?, ?> a = economyManger.format(amount).darkGray();
                ctx.sendNotification(
                    Text.translation("commands.economy.given").gray()
                        .arg(a)
                        .arg(target.getName(), Text::darkGray)
                );
                break;
            }
            case "take": {
                amount = economyManger.take(target, amount);
                Text<?, ?> a = economyManger.format(amount).darkGray();
                ctx.sendNotification(
                    Text.translation("commands.economy.taken").gray()
                        .arg(a)
                        .arg(target.getName(), Text::darkGray)
                );
                break;
            }
            case "set": {
                Text<?, ?> a = economyManger.format(amount).darkGray();
                economyManger.setBalance(target, amount);
                ctx.sendNotification(
                    Text.translation("commands.economy.set").gray()
                        .arg(target.getName(), Text::darkGray)
                        .arg(a)
                );
                break;
            }
        }
    }
}
