/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bosakon.tobehunter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

class HunterAIAssistant {
    private Map<String, List<String>> adviceDatabase;
    
    public HunterAIAssistant() {
        adviceDatabase = new HashMap<>();
        adviceDatabase.put("low_health", Arrays.asList(
            "Consider using a health potion or defensive skill",
            "Try to create distance and heal",
            "Switch to a defensive tactic"
        ));
        
        adviceDatabase.put("monster_low_health", Arrays.asList(
            "Finish it with your strongest attack!",
            "Don't get reckless now - maintain your strategy",
            "Use a skill to ensure the kill"
        ));
        
        adviceDatabase.put("status_effect", Arrays.asList(
            "Address negative status effects immediately",
            "Use this opportunity to press your advantage",
            "Consider items to counter the effect"
        ));
        
        adviceDatabase.put("enraged_monster", Arrays.asList(
            "Beware! The monster is enraged and more dangerous",
            "Focus on defense until the rage subsides",
            "Use crowd control skills if available"
        ));
    }
    
    public String generateCombatAdvice(Hunter player, Monster monster) {
        double playerHealthRatio = (double) player.getHP() / player.getMaxHP();
        double monsterHealthRatio = (double) monster.getHealth() / monster.getHealth();
        
        if (playerHealthRatio < 0.3) {
            return getRandomAdvice("low_health");
        } else if (monsterHealthRatio < 0.3) {
            return getRandomAdvice("monster_low_health");
        } else if (player.hasNegativeStatus()) {
            return getRandomAdvice("status_effect");
        } else if (monster.isEnraged()) {
            return getRandomAdvice("enraged_monster");
        }
        
        // Default tactical advice
        String tactic = player.decideCombatTactic(monster);
        return "Recommended tactic: " + tactic.toUpperCase() + ". " +
               getTacticSpecificAdvice(tactic);
    }
    
    private String getRandomAdvice(String category) {
        List<String> options = adviceDatabase.get(category);
        return options.get(new Random().nextInt(options.size()));
    }
    
    private String getTacticSpecificAdvice(String tactic) {
        switch(tactic) {
            case "offensive": 
                return "Focus on aggressive skills and high-damage attacks";
            case "defensive":
                return "Prioritize defense and healing, wait for openings";
            case "balanced":
                return "Mix attacks and defenses based on opportunities";
            default:
                return "Adapt to the changing battle conditions";
        }
    }
}
