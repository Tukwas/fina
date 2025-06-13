/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bosakon.tobehunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

class Hunter {
    private String name;
    private String rank;
    private int level;
    private int experience;
    private int gold;
    private int fame;
    private boolean isAwakened;
    private int successCount;
    private int totalBattles;
    
    // Stats
    private int strength;
    private int dexterity;
    private int intelligence;
    private int currentHP;
    private int maxHP;
    private int currentMana;
    private int maxMana;
    
    // Systems
    private Map<String, Integer> inventory;
    private Item equippedWeapon;
    private Map<String, Skill> skills;
    private StatusEffect currentStatus;
    private List<Quest> activeQuests;
    
    // Enhanced features
    private Map<String, Integer> reputation; // Faction → reputation score
    private Map<String, Integer> skillMastery; // Skill → mastery level
    private Map<String, Double> tacticalPreferences;
    
    // Rank progression
    private static final int[] RANK_THRESHOLDS = {100, 300, 700, 1500, 3000};
    private static final String[] RANKS = {"E", "D", "C", "B", "A", "S"};
    
    public Hunter(String name, Item startingWeapon) {
        this.name = name;
        this.rank = "E";
        this.level = 1;
        this.experience = 0;
        this.gold = 100;
        this.fame = 0;
        this.isAwakened = false;
        this.successCount = 0;
        this.totalBattles = 0;
        
        // Base stats
        this.strength = 5;
        this.dexterity = 5;
        this.intelligence = 5;
        this.maxHP = 100;
        this.currentHP = maxHP;
        this.maxMana = 10;
        this.currentMana = maxMana;
        
        this.inventory = new HashMap<>();
        this.equippedWeapon = startingWeapon;
        this.skills = new HashMap<>();
        this.activeQuests = new ArrayList<>();
        
        // Enhanced initialization
        this.reputation = new HashMap<>();
        this.skillMastery = new HashMap<>();
        this.tacticalPreferences = new HashMap<>();
        tacticalPreferences.put("offensive", 0.7);
        tacticalPreferences.put("defensive", 0.3);
        tacticalPreferences.put("balanced", 0.5);
        
        // Initialize factions
        reputation.put("Guild", 0);
        reputation.put("Merchants", 0);
        reputation.put("Council", 0);
    }
    
    public void awaken() {
        isAwakened = true;
    }
    
    public void unlockSkill(String skillName) {
        // Create skills on demand
        Map<String, Skill> skillMap = new HashMap<>();
        skillMap.put("Will to Recover", new Skill("Will to Recover", false, 0, "Heal 5 HP per turn"));
        skillMap.put("Tenacity", new Skill("Tenacity", false, 0, "Reduce damage by 50% when HP <30%"));
        skillMap.put("Healing", new Skill("Healing", true, 10, "Restore 20 HP"));
        skillMap.put("Flux", new Skill("Flux", true, 15, "Boost ATK/DEF by 30% for 3 turns"));
        skillMap.put("Dungeon Sense", new Skill("Dungeon Sense", false, 0, "Warn of rare events"));
        
        if (skillMap.containsKey(skillName)) {
            skills.put(skillName, skillMap.get(skillName));
            System.out.println("> Learned new skill: " + skillName);
        }
    }
    
    public void addReputation(String faction, int amount) {
        reputation.put(faction, reputation.getOrDefault(faction, 0) + amount);
    }
    
    public int getReputation(String faction) {
        return reputation.getOrDefault(faction, 0);
    }
    
    public void increaseSkillMastery(String skillName) {
        if (skills.containsKey(skillName)) {
            skills.get(skillName).increaseMastery();
            System.out.println("> Mastery increased for " + skillName + "!");
        }
    }
    
    public void addExperience(int exp) {
        experience += exp;
        while (experience >= level * 100) {
            experience -= level * 100;
            levelUp();
        }
        checkRankUp();
    }
    
    private void checkRankUp() {
        for (int i = 0; i < RANK_THRESHOLDS.length; i++) {
            if (experience >= RANK_THRESHOLDS[i] && rank.equals(RANKS[i])) {
                rank = RANKS[i+1];
                System.out.println("\n> Rank up! You're now " + rank + "-Rank Hunter!");
                break;
            }
        }
    }
    
    private void levelUp() {
        level++;
        strength += 2;
        dexterity += 2;
        intelligence += 2;
        maxHP += 10;
        maxMana += 5;
        currentHP = maxHP;
        currentMana = maxMana;
        System.out.println("\n> Level up! You're now level " + level);
    }
    
    public void addItem(String item) {
        inventory.put(item, inventory.getOrDefault(item, 0) + 1);
    }
    
    public boolean useItem(String item) {
        if (inventory.containsKey(item) && inventory.get(item) > 0) {
            inventory.put(item, inventory.get(item) - 1);
            
            switch(item) {
                case "Health Potion":
                    heal(25);
                    System.out.println("> Healed 25 HP!");
                    return true;
                case "Mana Potion":
                    currentMana = Math.min(maxMana, currentMana + 20);
                    System.out.println("> Restored 20 Mana!");
                    return true;
                case "Weapon Repair Kit":
                    equippedWeapon.repair();
                    System.out.println("> Weapon repaired!");
                    return true;
            }
        }
        return false;
    }
    
    public void takeDamage(int damage) {
        // Apply defense modifiers from status effects
        double defenseMod = 1.0;
        if (currentStatus != null && !currentStatus.isBuff()) {
            defenseMod = currentStatus.getDefenseModifier();
        }
        
        // Tenacity skill
        if (currentHP < maxHP * 0.3 && skills.containsKey("Tenacity")) {
            defenseMod *= 0.5;
        }
        
        currentHP -= (int)(damage * defenseMod);
        if (currentHP < 0) currentHP = 0;
    }
    
    public void heal(int amount) {
        currentHP = Math.min(maxHP, currentHP + amount);
    }
    
    public void addMana(int amount) {
        currentMana = Math.min(maxMana, currentMana + amount);
    }
    
    public boolean isAlive() {
        return currentHP > 0;
    }
    
    public int calculateDamage() {
        int baseDamage = equippedWeapon.getBaseDamage();
        double critChance = equippedWeapon.getCritChance();
        
        // Apply damage modifiers
        double damageMod = 1.0;
        if (currentStatus != null && currentStatus.isBuff()) {
            damageMod = currentStatus.getDamageModifier();
        }
        
        // Apply weapon special effects
        Random rand = new Random();
        switch(equippedWeapon.getName()) {
            case "Tirungan":
                if (rand.nextDouble() < 0.25) {
                    currentStatus = new StatusEffect("Tetanus", 3, 3, 1.0, 1.0, false, false);
                    System.out.println("> Tirungan inflicted Tetanus!");
                }
                break;
            case "Sanga sa Kamunggay":
                if (rand.nextDouble() < 0.2) {
                    heal(5);
                    System.out.println("> Sanga sa Kamunggay healed you!");
                }
                break;
            case "Flat Screw":
                if (rand.nextDouble() < 0.6) {
                    System.out.println("> Duslak ability activated!");
                    damageMod *= 2;
                }
                break;
            case "Dos Purdos":
                if (rand.nextDouble() < 0.3) {
                    System.out.println("> Critical focus activated!");
                    critChance += 0.2;
                }
                break;
        }
        
        // Critical hit calculation
        if (rand.nextDouble() < critChance) {
            return (int)(baseDamage * 2 * damageMod);
        }
        return (int)(baseDamage * damageMod);
    }
    
    public void applyEndOfTurnEffects() {
        // Will to Recover skill
        if (skills.containsKey("Will to Recover")) {
            heal(5);
        }
        
        // Status effects
        if (currentStatus != null) {
            if (currentStatus.applyEffect()) {
                takeDamage(currentStatus.getDamagePerTurn());
                System.out.println("> " + currentStatus.getName() + " deals " + 
                                  currentStatus.getDamagePerTurn() + " damage!");
            } else {
                currentStatus = null;
            }
        }
    }
    
    public void displayStats() {
        System.out.println("\n--- HUNTER STATS ---");
        System.out.println(name + " | Rank: " + rank + " | Level: " + level);
        System.out.println("HP: " + currentHP + "/" + maxHP + " | Mana: " + currentMana + "/" + maxMana);
        System.out.println("Gold: " + gold + " | Fame: " + fame);
        System.out.println("Weapon: " + equippedWeapon.getName() + 
                          " (DMG: " + equippedWeapon.getBaseDamage() + 
                          ", CRIT: " + (int)(equippedWeapon.getCritChance() * 100) + "%)");
        
        // Display reputation
        System.out.println("\nReputation:");
        System.out.println("- Guild: " + reputation.getOrDefault("Guild", 0));
        System.out.println("- Merchants: " + reputation.getOrDefault("Merchants", 0));
        System.out.println("- Council: " + reputation.getOrDefault("Council", 0));
        
        if (!skills.isEmpty()) {
            System.out.println("\nSkills:");
            for (Skill skill : skills.values()) {
                System.out.println("- " + skill.getName() + " (Lv " + skill.getMasteryLevel() + 
                                  "): " + skill.getEffect());
            }
        }
    }
    
    public void displayInventory() {
        System.out.println("\n--- INVENTORY ---");
        System.out.println("Gold: " + gold);
        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty");
        } else {
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                System.out.println("- " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }
    
    public void addQuest(Quest quest) {
        activeQuests.add(quest);
        System.out.println("> New Quest: " + quest.getTitle());
    }
    
    public void checkQuestCompletion(String dungeonName, String rank) {
        for (Quest quest : activeQuests) {
            if (quest.getObjective().contains(dungeonName) || 
                quest.getObjective().contains(rank + "-Rank")) {
                quest.complete(this);
            }
        }
    }
    
    public void recordBattleResult(boolean victory) {
        totalBattles++;
        if (victory) successCount++;
    }
    
    public double getSuccessRate() {
        if (totalBattles == 0) return 0.0;
        return (double) successCount / totalBattles;
    }
    
    public boolean hasSkill(String skillName) {
        return skills.containsKey(skillName);
    }
    
    public boolean hasNegativeStatus() {
        return currentStatus != null && !currentStatus.isBuff();
    }
    
    // AI-based combat decision
    public String decideCombatTactic(Monster monster) {
        double healthRatio = (double) currentHP / maxHP;
        double monsterThreat = (double) monster.getDamage() / maxHP;
        
        if (healthRatio < 0.3) {
            return "defensive";
        } else if (monsterThreat > 0.5) {
            return "balanced";
        } else if (healthRatio > 0.7 && monsterThreat < 0.3) {
            return "offensive";
        }
        
        // Adaptive learning based on previous success
        if (Math.random() < 0.3) {
            String bestTactic = "";
            double bestSuccessRate = 0.0;
            for (Map.Entry<String, Double> entry : tacticalPreferences.entrySet()) {
                if (entry.getValue() > bestSuccessRate) {
                    bestSuccessRate = entry.getValue();
                    bestTactic = entry.getKey();
                }
            }
            return bestTactic;
        }
        
        return "balanced";
    }
    
    // Update tactics based on combat outcome
    public void updateTacticSuccess(String tactic, boolean success) {
        double adjustment = success ? 0.05 : -0.05;
        double newValue = tacticalPreferences.get(tactic) + adjustment;
        tacticalPreferences.put(tactic, Math.max(0.1, Math.min(0.9, newValue)));
    }
    
    // Getters
    public String getName() { return name; }
    public int getHP() { return currentHP; }
    public int getMaxHP() { return maxHP; }
    public int getMana() { return currentMana; }
    public int getGold() { return gold; }
    public int getFame() { return fame; }
    public String getRank() { return rank; }
    public Item getWeapon() { return equippedWeapon; }
    public boolean isAwakened() { return isAwakened; }
    public Map<String, Skill> getSkills() { return skills; }
    public int getIntelligence() { return intelligence; }
    public int getExperience() { return experience; }
    public int getLevel() { return level; }
    
    public void setStatusEffect(StatusEffect effect) {
        currentStatus = effect;
    }
    
    public void addGold(int amount) { gold += amount; }
    public void addFame(int amount) { fame += amount; }
    public void setWeapon(Item weapon) { equippedWeapon = weapon; }
    public void setCurrentMana(int mana) { currentMana = mana; }
    public void setCurrentHP(int hp) { currentHP = Math.min(maxHP, Math.max(0, hp)); }
    public void setLevel(int level) { this.level = level; }
    public void setExperience(int exp) { this.experience = exp; }
    public void setMaxHP(int maxHP) { this.maxHP = maxHP; }
    public void setMaxMana(int maxMana) { this.maxMana = maxMana; }
}
