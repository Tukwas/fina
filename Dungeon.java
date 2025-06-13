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

class Dungeon {
    private String rank;
    private String name;
    private int currentFloor;
    private int totalFloors;
    private boolean isRedGate;
    private List<String> floorEvents;
    private Map<Integer, String> floorLayouts;
    
    // Dungeon name lists
    private static final String[] E_RANK_NAMES = {"Buhangin", "Milan", "Tigatto", "Bajada", "SPMC", "Cabagiuo"};
    private static final String[] D_RANK_NAMES = {"Mati", "Apokon", "Panabo", "Carmen", "Mawab"};
    private static final String[] C_RANK_NAMES = {"Lasang", "Sirawan", "Bunawan", "Donia Pilar", "LandMark"};
    private static final String[] A_RANK_NAMES = {"Lanang", "Sasa", "Magsaysay", "Roxas", "Uyangureen"};
    private static final String[] S_RANK_NAMES = {"Boulevard", "Matina Aplaya", "San Pedro", "Ubos Bangkerohan"};
    
    public Dungeon(String rank) {
        this.rank = rank;
        this.name = generateRandomName(rank);
        this.isRedGate = "C".equals(rank);
        this.currentFloor = 1;
        this.totalFloors = 3 + new Random().nextInt(3); // 3-5 floors
        generateFloorEvents();
        generateProceduralLayout();
    }
    
    private String generateRandomName(String rank) {
        Random rand = new Random();
        switch(rank) {
            case "E": return E_RANK_NAMES[rand.nextInt(E_RANK_NAMES.length)];
            case "D": return D_RANK_NAMES[rand.nextInt(D_RANK_NAMES.length)];
            case "C": return C_RANK_NAMES[rand.nextInt(C_RANK_NAMES.length)];
            case "A": return A_RANK_NAMES[rand.nextInt(A_RANK_NAMES.length)];
            case "S": return S_RANK_NAMES[rand.nextInt(S_RANK_NAMES.length)];
            default: return "Unknown";
        }
    }
    
    private void generateFloorEvents() {
        floorEvents = new ArrayList<>();
        String[] events = {
            "Mysterious Fog: Visibility reduced",
            "Energized Air: Mana regeneration doubled",
            "Toxic Spores: Take 5 damage per turn",
            "Ancient Runes: Skill effectiveness increased",
            "Haunted Grounds: Random status effects"
        };
        
        for (int i = 0; i < totalFloors; i++) {
            floorEvents.add(events[new Random().nextInt(events.length)]);
        }
    }
    
    private void generateProceduralLayout() {
        floorLayouts = new HashMap<>();
        String[] layouts = {"Linear", "Branching", "Circular", "Maze", "Open"};
        String[] challenges = {"Combat", "Puzzle", "Stealth", "Survival"};
        
        for (int i = 1; i <= totalFloors; i++) {
            String layout = layouts[new Random().nextInt(layouts.length)];
            String challenge = challenges[new Random().nextInt(challenges.length)];
            
            // AI-based difficulty scaling
            if (i == totalFloors) {
                challenge = "Boss " + challenge;
            } else if (i > totalFloors / 2) {
                challenge = "Advanced " + challenge;
            }
            
            floorLayouts.put(i, layout + " - " + challenge);
        }
    }
    
    public String getFloorLayout(int floor) {
        return floorLayouts.getOrDefault(floor, "Standard Layout");
    }
    
    public String getFloorEvent(int floor) {
        if (floor > 0 && floor <= floorEvents.size()) {
            return floorEvents.get(floor - 1);
        }
        return "";
    }
    
    public void nextFloor() {
        currentFloor++;
    }
    
    public boolean isLastFloor() {
        return currentFloor >= totalFloors;
    }
    
    public String getFullName() { 
        return name + " Dungeon (" + rank + "-Rank" + 
               (isRedGate ? " - RED GATE" : "") + ")"; 
    }
    public String getRank() { return rank; }
    public boolean isRedGate() { return isRedGate; }
    public int getCurrentFloor() { return currentFloor; }
    public int getTotalFloors() { return totalFloors; }
    public String getName() { return name; }
}
