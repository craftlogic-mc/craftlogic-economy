package ru.craftlogic.economy.network.message;

import ru.craftlogic.api.network.AdvancedBuffer;
import ru.craftlogic.api.network.AdvancedMessage;
import ru.craftlogic.api.network.AdvancedNetwork;
import ru.craftlogic.economy.CraftEconomy;
import ru.craftlogic.util.ReflectiveUsage;

import java.io.IOException;

public class MessageBalance extends AdvancedMessage {
    private float balance;
    private String currency;
    private String format;

    @Deprecated
    @ReflectiveUsage
    public MessageBalance() {}

    public MessageBalance(float balance, String currency, String format) {
        this.balance = balance;
        this.currency = currency;
        this.format = format;
    }

    @Override
    public AdvancedNetwork getNetwork() {
        return CraftEconomy.NETWORK;
    }

    public float getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }

    public String getFormat() {
        return format;
    }

    @Override
    protected void read(AdvancedBuffer buf) throws IOException {
        this.balance = buf.readFloat();
        this.currency = buf.readString(10);
        this.format = buf.readString(20);
    }

    @Override
    protected void write(AdvancedBuffer buf) {
        buf.writeFloat(this.balance);
        buf.writeString(this.currency);
        buf.writeString(this.format);
    }
}
