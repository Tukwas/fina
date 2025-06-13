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

class Monster {
    private String name;
    private int health;
    private int maxHealth;
    private int damage;
    private int expReward;
    private int goldReward;
    private int level;
    private String behavior;
    private List<String> abilities;
    private Map<String, Double> behaviorWeights;
    private String lastBehavior;
    private boolean enraged;
    
    private static final String[] PREFIXES = {"Crimson", "Shadow", "Frost", "Plagued", "Vengeful"};
    private static final String[] SUFFIXES = {"of Despair", "the Cursed", "from Beyond", "of Chaos"};
    private static final String[] MONSTER_NAMES = {"Goblin", "Orc", "Slime", "Skeleton", "Bat", "Spider", "Zombie"};
    private static final String[] BOSS_NAMES = {"Dragon", "Lich", "Demon", "Giant", "Behemoth"};
    private static final String[] ABILITIES = {"Fire Breath", "Poison Sting", "Frost Bite", "Life Drain", "Thunder Strike"};
    private static final String[] BEHAVIORS = {"Aggressive", "Defensive", "Cunning", "Berserk", "Tactical"};
    
    public Monster(int floor, String dungeonRank) {
        Random rand = new Random();
        this.level = rand.nextInt(floor + 1);
        this.maxHealth = 10 + (level * 5) + rand.nextInt(10);
        this.health = maxHealth;
        this.damage = 3 + level + rand.nextInt(3);
        this.expReward = 5 + (level * 3);
        this.goldReward = 2 + level;
        this.enraged = false;
        
        this.behaviorWeights = new HashMap<>();
        behaviorWeights.put("Aggressive", 0.6);
        behaviorWeights.put("Defensive", 0.3);
        behaviorWeights.put("Cunning", 0.4);
        behaviorWeights.put("Berserk", 0.2);
        behaviorWeights.put("Tactical", 0.5);
        
        this.abilities = new ArrayList<>();
        this.name = generateRandomName(dungeonRank);
        
        // Generate abilities
        int abilityCount = 1 + rand.nextInt(2);
        for (int i = 0; i < abilityCount; i++) {
            abilities.add(ABILITIES[rand.nextInt(ABILITIES.length)]);
        }
        
        if (dungeonRank.equals("S") || rand.nextDouble() < 0.1) {
            // Boss monster
            health *= 2;
            maxHealth *= 2;
            damage *= 2;
            expReward *= 3;
            goldReward *= 3;
            abilities.add("Ultimate Attack");
        }
    }
    
    private String generateRandomName(String rank) {
        Random rand = new Random();
        if (rank.equals("S") && rand.nextDouble() < 0.3) {
            String name = BOSS_NAMES[rand.nextInt(BOSS_NAMES.length)];
            if (rand.nextDouble() < 0.7) {
                name = PREFIXES[rand.nextInt(PREFIXES.length)] + " " + name;
            }
            if (rand.nextDouble() < 0.5) {
                name += " " + SUFFIXES[rand.nextInt(SUFFIXES.length)];
            }
            return name;
        }
        return MONSTER_NAMES[rand.nextInt(MONSTER_NAMES.length)];
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
        if (health < maxHealth * 0.3) {
            enraged = true;
        }
    }
    
    public boolean isAlive() {
        return health > 0;
    }
    
    public boolean isEnraged() {
        return enraged;
    }
    
    // Adaptive behavior selection
    public String selectBehavior(Hunter player) {
        double healthRatio = (double) health / maxHealth;
        double playerThreat = (double) player.calculateDamage() / health;
        
        // Dynamic weight adjustment
        if (healthRatio < 0.3) {
            behaviorWeights.put("Defensive", behaviorWeights.get("Defensive") * 1.5);
            behaviorWeights.put("Berserk", behaviorWeights.get("Berserk") * 1.3);
        }
        
        if (playerThreat > 0.7) {
            behaviorWeights.put("Tactical", behaviorWeights.get("Tactical") * 1.4);
        }
        
        // Select behavior based on weights
        double totalWeight = behaviorWeights.values().stream().mapToDouble(Double::doubleValue).sum();
        double random = Math.random() * totalWeight;
        double cumulative = 0.0;
        
        for (Map.Entry<String, Double> entry : behaviorWeights.entrySet()) {
            cumulative += entry.getValue();
            if (random <= cumulative) {
                lastBehavior = entry.getKey();
                return lastBehavior;
            }
        }
        
        lastBehavior = "Aggressive";
        return lastBehavior;
    }
    
    // Learn from combat outcomes
    public void updateBehaviorEffectiveness(boolean effective) {
        double adjustment = effective ? 0.1 : -0.1;
        double newValue = behaviorWeights.get(lastBehavior) + adjustment;
        behaviorWeights.put(lastBehavior, Math.max(0.1, Math.min(1.5, newValue)));
    }
    
    public String getBattleBehavior(Hunter player) {
        return selectBehavior(player);
    }
    
    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getDamage() { return damage; }
    public int getExpReward() { return expReward; }
    public int getGoldReward() { return goldReward; }
    public int getLevel() { return level; }
    public List<String> getAbilities() { return abilities; }
    public String getBehavior() { return behavior; }
}
