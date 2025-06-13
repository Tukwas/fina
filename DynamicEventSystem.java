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

class DynamicEventSystem {
    private Map<String, Double> eventProbabilities;
    private List<String> recentEvents;
    
    public DynamicEventSystem() {
        eventProbabilities = new HashMap<>();
        eventProbabilities.put("merchant_event", 0.3);
        eventProbabilities.put("monster_event", 0.4);
        eventProbabilities.put("treasure_event", 0.25);
        eventProbabilities.put("trap_event", 0.2);
        eventProbabilities.put("npc_event", 0.15);
        
        recentEvents = new ArrayList<>();
    }
    
    public String generateEvent(Hunter player) {
        // Adjust probabilities based on player state
        if (player.getGold() < 50) {
            eventProbabilities.put("treasure_event", 0.4);
        }
        if (player.getHP() < player.getMaxHP() * 0.3) {
            eventProbabilities.put("monster_event", 0.2);
        }
        
        // Select event based on weighted probabilities
        double totalWeight = eventProbabilities.values().stream().mapToDouble(Double::doubleValue).sum();
        double random = Math.random() * totalWeight;
        double cumulative = 0.0;
        
        for (Map.Entry<String, Double> entry : eventProbabilities.entrySet()) {
            cumulative += entry.getValue();
            if (random <= cumulative && !recentEvents.contains(entry.getKey())) {
                recentEvents.add(entry.getKey());
                if (recentEvents.size() > 3) recentEvents.remove(0);
                return entry.getKey();
            }
        }
        
        return "standard";
    }
    
    public void handleEvent(String eventType, Hunter player) {
        switch(eventType) {
            case "merchant_event":
                System.out.println("> A wandering merchant appears!");
                player.addItem("Health Potion");
                player.addItem("Mana Potion");
                System.out.println("> You receive healing supplies from the merchant!");
                break;
            case "monster_event":
                System.out.println("> Ambush! Enemies attack from the shadows!");
                break;
            case "treasure_event":
                int goldFound = 50 + new Random().nextInt(100);
                System.out.println("> You discover a hidden treasure chest!");
                System.out.println("> Found " + goldFound + " gold!");
                player.addGold(goldFound);
                break;
            case "trap_event":
                System.out.println("> You trigger an ancient trap!");
                player.takeDamage(20);
                System.out.println("> You take 20 damage!");
                break;
            case "npc_event":
                System.out.println("> A fellow hunter needs your help!");
                player.addReputation("Guild", 10);
                System.out.println("> Gained 10 reputation with the Guild!");
                break;
        }
    }
}
