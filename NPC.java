/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bosakon.tobehunter;

import java.util.HashMap;
import java.util.Map;

class NPC {
    private String type;
    private String name;
    private int affinity;
    private String faction;
    private Map<String, Integer> relationshipMemory;
    
    public NPC(String type, String name, String faction) {
        this.type = type;
        this.name = name;
        this.faction = faction;
        this.affinity = 0;
        this.relationshipMemory = new HashMap<>();
    }
    
    public void interact(Hunter player) {
        int playerReputation = player.getReputation(faction);
        int interactionCount = relationshipMemory.getOrDefault(player.getName(), 0);
        relationshipMemory.put(player.getName(), interactionCount + 1);
        
        String greeting = "";
        if (playerReputation < -10) greeting = "What do you want, outcast?";
        else if (playerReputation < 0) greeting = "Can I help you?";
        else if (playerReputation < 20) greeting = "Good to see you again!";
        else greeting = "Welcome back, honored hunter!";
        
        // Adaptive dialogue based on history
        if (interactionCount < 3) {
            greeting = "I don't know you well yet.";
        } else if (interactionCount < 10) {
            greeting = "Good to see you again!";
        } else {
            greeting = "Welcome back, old friend!";
        }
        
        switch(type) {
            case "merchant":
                System.out.println(name + ": \"" + greeting + "\"");
                if (playerReputation >= 20) {
                    System.out.println("As a valued member, you get 20% discount!");
                }
                break;
            case "trainer":
                System.out.println(name + ": \"" + greeting + " Ready for training?\"");
                break;
            case "friend":
                System.out.println(name + ": \"Hey " + player.getName() + ", how's the hunting?\"");
                break;
            case "reporter":
                if (player.getFame() > 50) {
                    System.out.println(name + ": \"Can I get an interview about your latest dungeon clear?\"");
                    player.addFame(10);
                }
                break;
        }
    }
    
    public void modifyAffinity(int amount) {
        affinity += amount;
    }
    
    public String getType() { return type; }
    public String getName() { return name; }
    public String getFaction() { return faction; }
    public int getAffinity() { return affinity; }
}
