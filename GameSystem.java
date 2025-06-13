/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bosakon.tobehunter;

import java.util.ArrayList;
import java.util.List;

class GameSystem {
    private List<Quest> quests;
    
    public GameSystem() {
        quests = new ArrayList<>();
        initializeQuests();
    }
    
    private void initializeQuests() {
        quests.add(new Quest("First Hunt", "Clear any E-Rank dungeon", 100, 50, null, "Guild", 5));
        quests.add(new Quest("Red Gate Challenge", "Clear a C-Rank dungeon", 300, 150, "Tenacity", "Guild", 15));
        quests.add(new Quest("Double Dungeon", "Discover a hidden S-Rank dungeon", 500, 300, "Dungeon Sense", "Council", 25));
    }
    
    public void assignStarterQuest(Hunter player) {
        if (!quests.isEmpty()) {
            player.addQuest(quests.get(0));
        }
    }
    
    public void awakenHunter(Hunter player) {
        System.out.println("\n> SYSTEM: " + player.getName() + " has been awakened!");
        player.unlockSkill("Will to Recover");
        player.unlockSkill("Tenacity");
    }
}

