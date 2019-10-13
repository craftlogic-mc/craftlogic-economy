package ru.craftlogic.economy.common.commands;

import net.minecraft.command.CommandException;
import ru.craftlogic.api.command.CommandBase;
import ru.craftlogic.api.command.CommandContext;
import ru.craftlogic.api.text.Text;
import ru.craftlogic.api.world.Player;
import ru.craftlogic.economy.EconomyManager;

public class CommandPay extends CommandBase {
    public CommandPay() {
        super("pay", 0, "<receiver:Player> <amount>");
    }

    @Override
    protected void execute(CommandContext ctx) throws Throwable {
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
}
