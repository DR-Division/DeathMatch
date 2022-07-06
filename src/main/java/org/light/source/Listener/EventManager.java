package org.light.source.Listener;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.light.source.Command.KillDeathCommand;
import org.light.source.DeathMatch;
import org.light.source.Game.GameManager;
import org.light.source.Game.KillDeathManager;
import org.light.source.Game.NoKnockbackObject;
import org.light.source.Game.UserMananger;
import org.light.source.Log.MinimizeLogger;
import org.light.source.Singleton.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventManager implements Listener {

    private DeathMatch Plugin;

    public EventManager(DeathMatch Plugin) {
        this.Plugin = Plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (event.getRawSlot() == -999)
            return;
        if (GameManager.getInstance().contains(p.getUniqueId()) && GameManager.getInstance().isGaming()) {
            event.setCancelled(true);
            if (event.getClick() == ClickType.MIDDLE && event.getCurrentItem() != null) {
                CSDirector director = CrackShotApi.getPlugin();
                String[] node = director.itemParentNode(event.getCurrentItem(), null);
                if (node != null) {
                    if (director.getBoolean(node[0] + ".Extras.One_Time_Use")) {
                        p.getInventory().setHeldItemSlot(0);
                        p.getInventory().setItem(0, CrackShotApi.generateRandomWeapon());
                    }
                    else {
                        for (UserMananger data : GameManager.getInstance().getUsers()) {
                            if (data.getUUID().equals(p.getUniqueId())) {
                                if (data.getReRoll() >= DataManager.getInstance().getMaxReroll()) {
                                    p.sendMessage("§4최대 리롤 횟수를 초과하셨습니다..");
                                }
                                else if (EconomyApi.getInstance().currentMoney(p) < DataManager.getInstance().getReRollMoney()) {
                                    p.sendMessage("§6돈이 부족합니다.");
                                }
                                else {
                                    data.setReRoll(data.getReRoll() + 1);
                                    EconomyApi.getInstance().subtractMoney(p, DataManager.getInstance().getReRollMoney());
                                    p.getInventory().setItem(0, CrackShotApi.generateNotOPWeapon());
                                    p.sendTitle("", "§c-" + DataManager.getInstance().getReRollMoney() + " §6포인트", 5, 40, 5);
                                    //p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§f남은 리롤 횟수 §7: §b" + data.getReRoll() + " §7/ §6" + DataManager.getInstance().getMaxReroll()));
                                    p.closeInventory();
                                }
                            }
                        }
                    }
                }
            }
        }
        else if (!GameManager.getInstance().contains(p.getUniqueId()) && event.getView().getTitle().contains("랭크")) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getRawSlot() == 51) {
                //다음페이지 (서로 위치 바꾸기)
                ItemStack stack = event.getCurrentItem();
                stack.getItemMeta().getDisplayName();
                int i = 0;
                flushData(event.getInventory());
                if (stack.getItemMeta().getDisplayName().contains("뎃")) {
                    //킬랭킹
                    for (ItemStack st : KillDeathCommand.createKillDeathRank())
                        event.getInventory().setItem(i++, st);
                }
                else {
                    //킬뎃 랭킹
                    for (ItemStack st : KillDeathCommand.createKillRank())
                        event.getInventory().setItem(i++, st);
                }
                swapItem(event.getInventory());
            }
            //제발 돌아가라 흑흑
        }

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!event.getPlayer().isOp() && GameManager.getInstance().contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onOffhandMove(PlayerSwapHandItemsEvent event) {
        Player target = event.getPlayer();
        if (GameManager.getInstance().isGaming() && GameManager.getInstance().contains(target.getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo().clone().subtract(new Vector(0, 1, 0)).getBlock().getType() == Material.SPONGE) {
            event.getPlayer().setVelocity(new Vector(0, 2.05, 0));
            event.getPlayer().spawnParticle(Particle.FIREWORKS_SPARK, event.getTo(), 30, 0.1, 0.1, 0.1, 0.5);
        }
    }

    @EventHandler
    public void onVelocity(PlayerVelocityEvent event) {
        Vector velocity = event.getVelocity();
        if (!NoKnockbackObject.getInstance().getKnockbackState(event.getPlayer())) {
            event.setCancelled(true);
            NoKnockbackObject.getInstance().setKnockBackState(event.getPlayer(), true);
        }
        if (velocity.getY() > 0.5 && velocity.getY() != 2.05)
            event.setVelocity(velocity.setY(0.5));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player target = event.getPlayer();
        target.setGameMode(GameMode.ADVENTURE);
        TeamManager.getInstance().removePlayer(target);
        setName(target);
        if (DataManager.getInstance().getLocations() != null && DataManager.getInstance().getLocation() != null && !target.getWorld().getName().contains("lobby"))
            target.teleport(DataManager.getInstance().getLocation());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (GameManager.getInstance().contains(p.getUniqueId()))
            GameManager.getInstance().removePlayer(p);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (GameManager.getInstance().isGaming() && GameManager.getInstance().contains(event.getEntity().getUniqueId())) {
            event.setKeepInventory(true);
            event.setDeathMessage("");
            event.getDrops().clear();
            addDeath(event.getEntity().getUniqueId());
            //event.getEntity().spigot().respawn();
            resetDamage(event.getEntity());
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType().name().toUpperCase().endsWith("_PLATE"))
                event.setCancelled(false);
        }
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEntityEvent event) {
        if (event.getVictim() instanceof Player && GameManager.getInstance().isGaming()) {
            Player killer = event.getPlayer();
            Player victim = (Player) event.getVictim();
            addDamage(killer, victim, (int) event.getDamage());
            if (killer.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
                event.setCancelled(true);
            else if (GameManager.getInstance().contains(killer.getUniqueId()) && GameManager.getInstance().contains(victim.getUniqueId())) {
                if (victim.getHealth() - event.getDamage() <= 0) {
                    int killerData = 0;
                    HashMap<UUID, Integer> damageMap = new HashMap<>();
                    for (UserMananger data : GameManager.getInstance().getUsers()) {
                        if (data.getUUID().equals(killer.getUniqueId())) {
                            data.setKills(data.getKills() + 1);
                            killerData = data.getKills();
                            killer.getInventory().setItem(8, getKillItem(data.getKills()));
                        }
                        else if (data.getUUID().equals(victim.getUniqueId())) {
                            victim.getInventory().setItem(0, new ItemStack(Material.AIR));
                            event.setDamage(0.0);
                            victim.setHealth(80.0);
                            sendRespawn(victim);
                            damageMap = data.getDamageMap();
                            if (victim.getName().equalsIgnoreCase(RatingManager.getInstance().getFirst())) {
                                //1위 인경우
                                int back, to;
                                back = data.getKills() / DataManager.getInstance().getKilltolevel();
                                to = (data.getKills() - 1) / DataManager.getInstance().getKilltolevel();
                                if (to >= (DataManager.getInstance().getRounds() / 2) && back != to) {
                                    sendLevelDown(victim, back, to);
                                    data.setKills(data.getKills() - 1);
                                }
                            }
                        }
                    }
                    damageMap.remove(killer.getUniqueId());
                    int maxHealth = (int) victim.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                    for (UUID data : damageMap.keySet()) {
                        if (damageMap.get(data) > maxHealth)
                            damageMap.put(data, maxHealth);
                    }
                    ItemStack stack = CrackShotApi.getCSWeapon(event.getWeaponTitle()) == null ? createDummyItem() : CrackShotApi.getCSWeapon(event.getWeaponTitle());
                    if (stack.getType() == Material.IRON_AXE || stack.getType() == Material.DIAMOND_AXE)
                        sendKillMsg(killer, victim, "§c" + killer.getName() + " §f(" + stack.getItemMeta().getDisplayName() + "§f)" + " §7メ §b" + victim.getName(), damageMap);
                    else
                        sendKillMsg(killer, victim, "§c" + killer.getName() + " §f(" + stack.getItemMeta().getDisplayName() + "§f)" + " §7➾ §b" + victim.getName(), damageMap);

                    RatingManager.getInstance().updateRank();
                    addKill(killer.getUniqueId());
                    addDeath(victim.getUniqueId());
                    if (DataManager.getInstance().getRounds() * DataManager.getInstance().getKilltolevel() <= killerData) {
                        //승리!
                        Bukkit.broadcastMessage("§b" + killer.getName() + "§f님이 §6" + DataManager.getInstance().getRounds() + " §f레벨을 달성하여 게임을 승리하셨습니다!");
                        GameManager.getInstance().stop();
                    }
                    else {
                        //레벨업 및 경고
                        int back = (killerData - 1) / DataManager.getInstance().getKilltolevel();
                        int to = killerData / DataManager.getInstance().getKilltolevel();
                        if (killerData - 1 != 0 && back != to) {
                            if (killerData == DataManager.getInstance().getKilltolevel() * (DataManager.getInstance().getRounds() - 1))
                                Bukkit.broadcastMessage("§b" + killer.getName() + " §f님이 §6" + (DataManager.getInstance().getRounds() - 1) + "§f레벨에 도달하셨습니다!");
                            sendLevelUp(killer, back, to);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(Plugin, () -> {
            Player target = event.getPlayer();
            if (GameManager.getInstance().isGaming() && GameManager.getInstance().contains(target.getUniqueId())) {
                for (UserMananger mgr : GameManager.getInstance().getUsers()) {
                    if (mgr.getUUID().equals(target.getUniqueId())) {
                        target.getInventory().setItem(0, new ItemStack(Material.AIR));
                        sendRespawn(target);
                    }
                }
            }
            else
                Bukkit.getScheduler().runTaskLater(Plugin, () -> target.teleport(DataManager.getInstance().getLocation()), 1L);
        }, 1L);

    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework)
            event.setCancelled(true);
    }

    @EventHandler
    public void onAnyDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING)
            event.setCancelled(true);
        else if (event.getCause() == EntityDamageEvent.DamageCause.FALL && !event.getEntity().getWorld().getName().contains("dayz_"))
            event.setCancelled(true);
        else if (event.getCause() == EntityDamageEvent.DamageCause.VOID)
            event.setDamage(event.getDamage() * 2);
    }

    public void sendLevelUp(Player p, int back, int to) {
        p.sendTitle("§bLevel UP!", "§6" + back + " §f=> §b" + to, 5, 50, 5);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        p.setLevel(to);
        p.setExp(GameManager.getInstance().calcLevelProgress(to));
    }

    public void sendLevelDown(Player p, int back, int to) {
        p.sendTitle("§cLevel Down..", "§6" + back + " §f=> §b" + to, 5, 50, 5);
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        p.setLevel(to);
        p.setExp(GameManager.getInstance().calcLevelProgress(to));
    }

    public void sendKillMsg(Player killer, Player victim, String msg, Map<UUID, Integer> damageMap) {
        //킬당 참여보상의 1/10 지급, 연속킬시 킬보상의 1/10 * 반올림(연속킬수 / 2), 제압킬시 참여 보상의 1/5 * 연속킬 횟수지급, 전부다 합연산으로 지급
        UserMananger killManager = null, victimManager = null;
        int reward = 0;
        for (UserMananger mananger : GameManager.getInstance().getUsers()) {
            if (mananger.getUUID().equals(killer.getUniqueId())) {
                killManager = mananger;
            }
            else if (mananger.getUUID().equals(victim.getUniqueId())) {
                victimManager = mananger;
            }
        }
        if (killManager != null && victimManager != null) {
            //죽은사람이 연속킬중일 경우
            reward += DataManager.getInstance().getJoinMoney() / 10;
            killManager.setKillMaintain(killManager.getKillMaintain() + 1);
            if (victimManager.getKillMaintain() >= 2) {
                createKillLog("§4§oShutDown! " + msg);
                reward += DataManager.getInstance().getJoinMoney() / 5 * victimManager.getKillMaintain();

            }
            else {
                //아닌경우 그냥 출력
                if (killManager.calcKillStay()) {
                    //이어갈 수 있는경우
                    if (killManager.getKillMaintain() != 1) {
                        if (killManager.getKillMaintain() == 2)
                            createKillLog("§e§oDouble Kill! " + msg);
                        else if (killManager.getKillMaintain() == 3)
                            createKillLog("§b§oTriple Kill! " + msg);
                        else if (killManager.getKillMaintain() == 4)
                            createKillLog("§a§oQuadra Kill! " + msg);
                        else if (killManager.getKillMaintain() == 5)
                            createKillLog("§d§oPenta Kill! " + msg);
                        else if (killManager.getKillMaintain() == 6)
                            createKillLog("§4§oHexa Kill! " + msg);
                        else
                            createKillLog("§6§oLegendary! " + msg);
                        reward += DataManager.getInstance().getJoinMoney() / 10 * (int) (Math.floor((double) killManager.getKillMaintain() / 2));
                    }
                    else
                        createKillLog(msg);
                }
                else {
                    createKillLog(msg);
                    killManager.setKillMaintain(1);
                }
            }
            victimManager.setKillMaintain(0);
            killManager.setLastKillTime(System.currentTimeMillis());
            killer.sendTitle("", "+ " + reward + " §6포인트", 0, 20, 0);
            MinimizeLogger.getInstance().appendLog(killer.getName() + "님이 " + victim.getName() + "님을 죽여 " + reward + "원을 흭득");
            killManager.setCalcResultMoney(killManager.getCalcResultMoney() + reward);
            for (UUID data : damageMap.keySet()) {
                if (damageMap.get(data) != 0) {
                    double percent = damageMap.get(data) / victim.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                    int assistReward = (int) (reward / 2 * percent);
                    Player target = Bukkit.getPlayer(data);
                    if (target != null) {
                        target.sendMessage("§6[ §f! §6] §bAssist! §6 +" + assistReward + " 포인트");
                        MinimizeLogger.getInstance().appendLog(target.getName() + "님이 어시스트로 " + assistReward + "원 흭득");
                        for (UserMananger value : GameManager.getInstance().getUsers())
                            if (value.getUUID().equals(data))
                                value.setCalcResultMoney(value.getCalcResultMoney() + assistReward);
                    }
                }
            }
            victimManager.getDamageMap().clear();
        }

    }

    public void sendRespawn(Player victim) {
        //게임 끝날때 사망 리스폰 처리
        victim.playSound(victim.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f);
        //즉시 리스폰
        if (GameManager.getInstance().isGaming() && GameManager.getInstance().contains(victim.getUniqueId())) {
            for (UserMananger mgr : GameManager.getInstance().getUsers()) {
                if (mgr.getUUID().equals(victim.getUniqueId())) {
                    victim.getInventory().setHeldItemSlot(4);
                    victim.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(80.0);
                    victim.setHealthScaled(true);
                    victim.setHealth(80.0);
                    victim.getActivePotionEffects().forEach(data -> victim.removePotionEffect(data.getType()));
                    victim.teleport(GameManager.getInstance().getTeleportLocation(DataManager.getInstance().getLocations()[GameManager.getInstance().getRandomNumber()], DataManager.getInstance().getLocations()[GameManager.getInstance().getRandomNumber() + 1]));
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 2, true, false), false);
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1, true, false), false);
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 5, true, false));
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 9999, 100, true, false));
                    victim.getInventory().setHelmet(new ItemStack(Material.AIR));
                    if (victim.getName().equalsIgnoreCase(RatingManager.getInstance().getFirst()))
                        victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 9999, 5, true, true));
                    victim.getInventory().setItem(0, CrackShotApi.generateRandomWeapon());
                }
            }
        }
        else
            victim.teleport(DataManager.getInstance().getLocation());
    }

    private void createKillLog(String message) {
        ObjectArrayList<UUID> list = new ObjectArrayList<>();
        StringBuilder builder = new StringBuilder();
        String stripMsg = ChatColor.stripColor(message);
        for (int i = 0; i < (105 - stripMsg.length() - (stripMsg.replaceAll("[^ㄱ-힣]", "").length() + (stripMsg.contains("▄") ? 4 : 0) + (stripMsg.contains("▬▬") ? 2 : 0))); i++)
            builder.append(" ");
        builder.append(message);
        BossBar bar = Bukkit.createBossBar(builder.toString(), BarColor.WHITE, BarStyle.SOLID);
        GameManager.getInstance().getUsers().forEach(p -> {
            bar.addPlayer(Bukkit.getPlayer(p.getUUID()));
            list.add(p.getUUID());
        });
        Bukkit.getScheduler().runTaskLaterAsynchronously(Plugin, () -> {
            list.stream().filter(p -> Bukkit.getPlayer(p) != null).forEach(p -> bar.removePlayer(Bukkit.getPlayer(p)));
            bar.removeAll();
        }, 70L);
    }

    private ItemStack createDummyItem() {
        ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("§cError §f<<X>>");
        stack.setItemMeta(meta);
        return stack;
    }

    private void flushData(Inventory inv) {
        for (int i = 0; i < 45; i++)
            inv.setItem(i, new ItemStack(Material.AIR));
    }

    private void addKill(UUID uuid) {
        KillDeathObject object = KillDeathManager.getInstance().getValue(uuid);
        object.setKill(object.getKill() + 1);
    }

    private void addDeath(UUID uuid) {
        KillDeathObject object = KillDeathManager.getInstance().getValue(uuid);
        object.setDeath(object.getDeath() + 1);
    }

    private void swapItem(Inventory inv) {
        ItemStack previous, next;
        previous = inv.getItem(47).clone();
        next = inv.getItem(51);
        inv.setItem(47, next);
        inv.setItem(51, previous);
    }

    private void setName(Player p) {
        KillDeathManager.getInstance().getValue(p.getUniqueId()).setName(p.getName());
    }

    private ItemStack getKillItem(int amount) {
        ItemStack kill = new ItemStack(Material.NETHER_STAR, amount);
        ItemMeta meta = kill.getItemMeta();
        meta.setDisplayName("§6[ §f! §6] §cKill Stack");
        meta.setLore(Arrays.asList(" ", " §8-  §f현재 당신의 킬 횟수입니다.", " "));
        kill.setItemMeta(meta);
        return kill;
    }

    private void addDamage(Player killer, Player victim, int damage) {
        for (UserMananger manager : GameManager.getInstance().getUsers()) {
            if (manager.getUUID().equals(victim.getUniqueId())) {
                HashMap<UUID, Integer> damageMap = manager.getDamageMap();
                if (damageMap.containsKey(killer.getUniqueId()))
                    damageMap.put(killer.getUniqueId(), damageMap.get(killer.getUniqueId()) + damage);
                else
                    damageMap.put(killer.getUniqueId(), damage);
            }
        }
    }

    private void resetDamage(Player victim) {
        GameManager.getInstance().getUsers().stream().filter(data -> data.getUUID().equals(victim.getUniqueId())).forEach(data -> data.getDamageMap().clear());
    }

}
