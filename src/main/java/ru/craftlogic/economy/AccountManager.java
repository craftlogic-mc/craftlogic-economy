package ru.craftlogic.economy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;
import ru.craftlogic.api.util.ConfigurableManager;
import ru.craftlogic.api.world.OfflinePlayer;

import java.nio.file.Path;
import java.util.*;

public class AccountManager extends ConfigurableManager {
    private final Object2FloatMap<UUID> accounts = new Object2FloatOpenHashMap<>();

    public AccountManager(EconomyManager economyManager, Path configFile, Logger logger) {
        super(economyManager.getServer(), configFile, logger);
        this.accounts.defaultReturnValue(0);
    }

    @Override
    protected void load(JsonObject accounts) {
        for (Map.Entry<String, JsonElement> entry : accounts.entrySet()) {
            UUID id = UUID.fromString(entry.getKey());
            float balance = entry.getValue().getAsFloat();
            this.accounts.put(id, balance);
        }
    }

    @Override
    protected void save(JsonObject accounts) {
        for (Map.Entry<UUID, Float> entry : this.accounts.entrySet()) {
            accounts.addProperty(entry.getKey().toString(), entry.getValue());
        }
    }

    public List<Object2FloatMap.Entry<UUID>> getTop(int size) {
        List<Object2FloatMap.Entry<UUID>> top = new ArrayList<>(this.accounts.object2FloatEntrySet());
        top.sort(Collections.reverseOrder(Comparator.comparing(Object2FloatMap.Entry::getFloatValue)));
        return top.subList(0, Math.min(size, top.size()));
    }

    public float getBalance(OfflinePlayer player) {
        return this.getBalance(player.getId());
    }

    public float getBalance(UUID id) {
        return this.accounts.getFloat(id);
    }

    public void setBalance(OfflinePlayer player, float balance) {
        this.setBalance(player.getId(), balance);
    }

    public void setBalance(UUID id, float balance) {
        Validate.isTrue(balance >= 0, "Balance cannot be negative!");
        if (balance == 0) {
            this.accounts.remove(id);
            this.setDirty(true);
        } else {
            if (this.accounts.put(id, balance) != balance) {
                this.setDirty(true);
            }
        }
    }
}
