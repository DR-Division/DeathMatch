package org.light.source.Singleton;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyApi {

    private static EconomyApi instance;
    private Economy economy;

    static
    {
        instance = new EconomyApi();
    }

    private EconomyApi(){
        if (!setEconomy())
            Bukkit.getServer().getLogger().info("DeathMatch 플러그인 구동에 필요한 Vault, Economy플러그인이 적용되어 있지 않습니다.");
    }

    private boolean setEconomy(){
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        RegisteredServiceProvider<Economy> EcoProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (EcoProvider == null)
            return false;
        economy = EcoProvider.getProvider();
        return true;
    }

    public static EconomyApi getInstance(){
        return instance;
    }

    public void giveMoney(Player p, double value){
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(p.getUniqueId());
        economy.depositPlayer(offlinePlayer, value);
    }

    public void subtractMoney(Player p, double value){
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(p.getUniqueId());
        economy.withdrawPlayer(offlinePlayer, value);
    }
    public double currentMoney(Player p){
        return economy.getBalance(p);
    }

}
