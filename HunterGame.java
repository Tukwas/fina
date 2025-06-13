package com.bosakon.tobehunter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class HunterGame {
    private Hunter player;
    private Scanner scanner;
    private boolean inGame;
    private boolean atHome;
    private GameSystem gameSystem;
    private Map<String, Double> difficultySettings;
    private HunterAIAssistant aiAssistant;
    private DynamicEventSystem eventSystem;
    
    // Starting weapons (Enhanced)
    private static final Item LAPIS = new Item("Lapis", 8, 0.2, "20% critical hit chance", 50, 50, 0);
    private static final Item TIRUNGAN = new Item("Tirungan", 6, 0.1, "25% chance to inflict Tetanus", 40, 40, 0);
    private static final Item FLAT_SCREW = new Item("Flat Screw", 5, 0.6, "Duslak ability (60% crit)", 35, 35, 0);
    
    // Special weapons (Enhanced)
    private static final Item DOS_PURDOS = new Item("Dos Purdos", 12, 0.3, "High critical hit chance", 60, 150, 2);
    private static final Item SANGGA = new Item("Sanga sa Kamunggay", 7, 0.15, "20% chance to heal on hit", 45, 120, 1);
    
    // Locations and NPCs
    private Map<String, Location> locations;
    private Map<String, NPC> npcs;
    private Stack<Dungeon> dungeonStack = new Stack<>();
    
    public HunterGame() {
        scanner = new Scanner(System.in);
        inGame = true;
        atHome = false;
        gameSystem = new GameSystem();
        this.difficultySettings = new HashMap<>();
        difficultySettings.put("monster_aggression", 0.5);
        difficultySettings.put("item_rarity", 0.7);
        difficultySettings.put("dungeon_complexity", 0.6);
        this.aiAssistant = new HunterAIAssistant();
        this.eventSystem = new DynamicEventSystem();
    }
    
    public void startGame() {
        System.out.println("=====================================");
        System.out.println("         WELCOME TO THE HUNTER GAME");
        System.out.println("=====================================");
        System.out.print("Enter your Hunter's name: ");
        String name = scanner.nextLine();
        
        System.out.print("\nChoose your starting weapon (Lapis, Tirungan, Flat Screw): ");
        String weaponChoice = scanner.nextLine().toLowerCase();
        
        Item weapon;
        if (weaponChoice.contains("lapis")) {
            weapon = LAPIS;
        } else if (weaponChoice.contains("tirungan")) {
            weapon = TIRUNGAN;
        } else if (weaponChoice.contains("flat") || weaponChoice.contains("screw")) {
            weapon = FLAT_SCREW;
        } else {
            System.out.println("Invalid choice, defaulting to Lapis");
            weapon = LAPIS;
        }
        
        player = new Hunter(name, weapon);
        System.out.println("\nWelcome Hunter " + name + "!");
        
        gameSystem.assignStarterQuest(player);
        initializeWorld();
        mainMenu();
    }
    
    private void initializeWorld() {
        // Create locations
        locations = new HashMap<>();
        locations.put("market", new Location("Market", "Trade loot, potions, and equipment"));
        locations.put("gym", new Location("Gym", "Exercise to boost your stats"));
        locations.put("center", new Location("Hunter Center", "Check your stats and rank information"));
        locations.put("home", new Location("Home", "Rest and manage your inventory"));
        
        // Create NPCs (Enhanced)
        npcs = new HashMap<>();
        npcs.put("market_merchant", new NPC("merchant", "Old Man Jenkins", "Merchants"));
        npcs.put("center_clerk", new NPC("friend", "Clerk Sarah", "Guild"));
        npcs.put("reporter", new NPC("reporter", "Lois Lane", "Council"));
        npcs.put("trainer", new NPC("trainer", "Master Li", "Guild"));
    }
    
    private void mainMenu() {
        while(inGame && player.isAlive()) {
            System.out.println("\nWhat would you like to do?");
            System.out.println("1. Enter Dungeon");
            System.out.println("2. Check Stats");
            System.out.println("3. Check Inventory");
            System.out.println("4. Go Home");
            System.out.println("0. Exit Game");
            System.out.print("Please enter your choice: ");
            
            int choice = getIntInput();
            scanner.nextLine(); // Consume newline
            
            switch(choice) {
                case 1: enterDungeon(); break;
                case 2: player.displayStats(); break;
                case 3: player.displayInventory(); break;
                case 4: goHome(); break;
                case 0: inGame = false; break;
                default: System.out.println("Invalid choice");
            }
        }
        
        if(!player.isAlive()) {
            System.out.println("\nGAME OVER - You have been defeated");
        }
        
        System.out.println("Thanks for playing!");
    }
    
    private int getIntInput() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
            }
        }
    }
    
    private void goHome() {
        atHome = true;
        while(atHome && inGame) {
            locations.get("home").enter();
            
            System.out.println("1. Save Game");
            System.out.println("2. Load Game");
            System.out.println("3. Sleep (Recover Health)");
            System.out.println("4. Go Outside");
            System.out.println("5. Enter Dungeon");
            System.out.println("0. Exit Game");
            System.out.print("Please enter your choice: ");
            
            int choice = getIntInput();
            scanner.nextLine(); // Consume newline
            
            switch(choice) {
                case 1: saveGame(); break;
                case 2: loadGame(); break;
                case 3: 
                    player.heal(player.getMaxHP());
                    System.out.println("You sleep and recover all health!");
                    break;
                case 4: goOutside(); break;
                case 5: enterDungeon(); break;
                case 0: 
                    inGame = false;
                    atHome = false;
                    break;
                default: System.out.println("Invalid choice");
            }
        }
    }
    
    private void saveGame() {
        try (FileWriter writer = new FileWriter("hunter_save.txt")) {
            writer.write(player.getName() + "\n");
            writer.write(player.getRank() + "\n");
            writer.write(player.getLevel() + "\n");
            writer.write(player.getExperience() + "\n");
            writer.write(player.getHP() + "\n");
            writer.write(player.getMaxHP() + "\n");
            writer.write(player.getMana() + "\n");
            writer.write(player.getMaxMana() + "\n");
            writer.write(player.getGold() + "\n");
            writer.write(player.getFame() + "\n");
            writer.write(player.getWeapon().getName() + "\n");
            
            // Save reputation
            writer.write(player.getReputation("Guild") + "\n");
            writer.write(player.getReputation("Merchants") + "\n");
            writer.write(player.getReputation("Council") + "\n");
            
            System.out.println("> Game saved successfully!");
        } catch (Exception e) {
            System.out.println("> Failed to save game: " + e.getMessage());
        }
    }
    
    private void loadGame() {
        try (BufferedReader reader = new BufferedReader(new FileReader("hunter_save.txt"))) {
            String name = reader.readLine();
            String rank = reader.readLine();
            int level = Integer.parseInt(reader.readLine());
            int exp = Integer.parseInt(reader.readLine());
            int hp = Integer.parseInt(reader.readLine());
            int maxHP = Integer.parseInt(reader.readLine());
            int mana = Integer.parseInt(reader.readLine());
            int maxMana = Integer.parseInt(reader.readLine());
            int gold = Integer.parseInt(reader.readLine());
            int fame = Integer.parseInt(reader.readLine());
            String weaponName = reader.readLine();
            
            // Load reputation
            int guildRep = Integer.parseInt(reader.readLine());
            int merchantRep = Integer.parseInt(reader.readLine());
            int councilRep = Integer.parseInt(reader.readLine());
            
            Item weapon;
            switch(weaponName) {
                case "Lapis": weapon = LAPIS; break;
                case "Tirungan": weapon = TIRUNGAN; break;
                case "Flat Screw": weapon = FLAT_SCREW; break;
                case "Sanga sa Kamunggay": weapon = SANGGA; break;
                case "Dos Purdos": weapon = DOS_PURDOS; break;
                default: weapon = LAPIS;
            }
            
            player = new Hunter(name, weapon);
            player.addGold(gold);
            player.addFame(fame);
            player.addReputation("Guild", guildRep);
            player.addReputation("Merchants", merchantRep);
            player.addReputation("Council", councilRep);
            
            // Set advanced stats
            player.setLevel(level);
            player.setExperience(exp);
            player.setMaxHP(maxHP);
            player.setCurrentHP(hp);
            player.setMaxMana(maxMana);
            player.setCurrentMana(mana);
            
            System.out.println("> Game loaded successfully!");
        } catch (Exception e) {
            System.out.println("> Failed to load game: " + e.getMessage());
        }
    }
    
    private void goOutside() {
        boolean outside = true;
        while(outside && inGame) {
            System.out.println("\nWhere would you like to go?");
            System.out.println("1. Visit Market");
            System.out.println("2. Go to Gym");
            System.out.println("3. Visit Center");
            System.out.println("0. Go Back Inside");
            System.out.print("Please enter your choice: ");
            
            int choice = getIntInput();
            scanner.nextLine(); // Consume newline
            
            switch(choice) {
                case 1: 
                    visitMarket();
                    break;
                case 2: 
                    locations.get("gym").enter();
                    System.out.println("You spend an hour training...");
                    player.addExperience(15);
                    player.increaseSkillMastery("Tenacity");
                    break;
                case 3: 
                    visitCenter();
                    break;
                case 0: outside = false; break;
                default: System.out.println("Invalid choice");
            }
            
            // Random event chance
            generateRandomEvent();
        }
    }
    
    private void visitMarket() {
        locations.get("market").enter();
        npcs.get("market_merchant").interact(player);
        
        // Reputation-based discounts
        int discount = 0;
        int reputation = player.getReputation("Merchants");
        if (reputation > 20) discount = 25;
        else if (reputation > 10) discount = 15;
        else if (reputation > 0) discount = 5;
        
        if (discount > 0) {
            System.out.println("Merchant Affinity: " + reputation + " (" + discount + "% discount)");
        }
        
        System.out.println("\nItems available:");
        System.out.println("1. Health Potion - 20 gold");
        System.out.println("2. Mana Potion - 30 gold");
        System.out.println("3. Weapon Repair Kit - 50 gold");
        System.out.println("4. Back");
        
        System.out.print("Select an item to buy: ");
        int choice = getIntInput();
        scanner.nextLine();
        
        int cost = 0;
        String item = "";
        switch(choice) {
            case 1: 
                cost = (int)(20 * (1 - discount/100.0));
                item = "Health Potion";
                break;
            case 2:
                cost = (int)(30 * (1 - discount/100.0));
                item = "Mana Potion";
                break;
            case 3:
                cost = (int)(50 * (1 - discount/100.0));
                item = "Weapon Repair Kit";
                break;
            case 4: return;
            default: 
                System.out.println("Invalid choice");
                return;
        }
        
        if (player.getGold() >= cost) {
            player.addGold(-cost);
            player.addItem(item);
            System.out.println("Purchased " + item + " for " + cost + " gold");
        } else {
            System.out.println("Not enough gold!");
        }
    }
    
    private void visitCenter() {
        boolean inCenter = true;
        while (inCenter && inGame) {
            locations.get("center").enter();
            
            System.out.println("\nCenter Services:");
            System.out.println("1. 2D Heart Echo Scan");
            System.out.println("2. Mana Crystal Rank Assessment");
            System.out.println("3. Speak with Clerk Sarah");
            System.out.println("4. Visit Trainer");
            System.out.println("0. Exit Center");
            System.out.print("Select service: ");
            
            int choice = getIntInput();
            scanner.nextLine();  // Consume newline
            
            switch (choice) {
                case 1:
                    displayHeartEcho();
                    break;
                case 2:
                    displayManaCrystal();
                    break;
                case 3:
                    npcs.get("center_clerk").interact(player);
                    break;
                case 4:
                    npcs.get("trainer").interact(player);
                    System.out.println("You train your skills...");
                    for (Skill skill : player.getSkills().values()) {
                        player.increaseSkillMastery(skill.getName());
                    }
                    break;
                case 0:
                    inCenter = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
    
    private void displayHeartEcho() {
        int heartSize = 10;
        int currentHP = player.getHP();
        int maxHP = player.getMaxHP();
        
        // Validate HP values
        if (maxHP <= 0) maxHP = 1;
        if (currentHP < 0) currentHP = 0;
        if (currentHP > maxHP) currentHP = maxHP;
        
        int healthPercent = (int) Math.round((double) currentHP / maxHP * 100);

        System.out.println("\n--- 2D HEART ECHO SCAN ---");
        System.out.println("Cardiovascular Status: " + healthPercent + "% functionality");
        System.out.println("Current HP: " + currentHP + "/" + maxHP);

        // ASCII heart visualization
        for (int y = -heartSize; y <= heartSize; y++) {
            for (int x = -2 * heartSize; x <= 2 * heartSize; x++) {
                double formula = Math.pow(x * 0.04, 2) + Math.pow(y * 0.1, 2) - Math.pow(heartSize * 0.1, 2);
                if (Math.abs(formula) < heartSize * 0.3) {
                    // Color based on health status
                    if (healthPercent > 70) {
                        System.out.print("\u001B[32m♥\u001B[0m");  // Green
                    } else if (healthPercent > 30) {
                        System.out.print("\u001B[33m♥\u001B[0m"); // Yellow
                    } else {
                        System.out.print("\u001B[31m♥\u001B[0m");  // Red
                    }
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
    
    private void displayManaCrystal() {
        String[] rankColors = {"\u001B[37m", "\u001B[34m", "\u001B[36m", "\u001B[32m", "\u001B[33m", "\u001B[31m"};
        String[] rankNames = {"E-Rank", "D-Rank", "C-Rank", "B-Rank", "A-Rank", "S-Rank"};
        int rankIndex = Arrays.asList("E", "D", "C", "B", "A", "S").indexOf(player.getRank());
        rankIndex = Math.max(0, Math.min(5, rankIndex)); // Ensure valid index
        
        System.out.println("\n--- MANA CRYSTAL ANALYSIS ---");
        System.out.println("Current Rank: " + rankNames[rankIndex]);
        
        // ASCII crystal visualization
        String color = rankColors[rankIndex];
        System.out.println(color + "    /\\");
        System.out.println("   /  \\");
        System.out.println("  /    \\");
        System.out.println(" /      \\");
        System.out.println(" \\      /");
        System.out.println("  \\    /");
        System.out.println("   \\  /");
        System.out.println("    \\/\u001B[0m");
        
        // Rank progression
        System.out.println("\nRank Progression:");
        for (int i = 0; i <= 5; i++) {
            String indicator = (i <= rankIndex) ? "◉" : "○";
            System.out.println(rankColors[i] + indicator + " " + rankNames[i] + "\u001B[0m");
        }
    }
    
    private void enterDungeon() {
        System.out.println("\n===== DUNGEON SELECTION =====");
        System.out.println("1. E-Rank Dungeon (Beginner)");
        System.out.println("2. D-Rank Dungeon (Easy)");
        System.out.println("3. C-Rank Dungeon (Red Gate)");
        System.out.println("4. B-Rank Dungeon (Medium)");
        System.out.println("5. A-Rank Dungeon (Hard)");
        System.out.println("6. S-Rank Dungeon (Deadly)");
        System.out.println("7. Return to Main Menu");
        System.out.print("Select: ");
        
        String rankChoice;
        int choice = getIntInput();
        scanner.nextLine(); // Consume newline
        switch(choice) {
            case 1: rankChoice = "E"; break;
            case 2: rankChoice = "D"; break;
            case 3: rankChoice = "C"; break;
            case 4: rankChoice = "B"; break;
            case 5: rankChoice = "A"; break;
            case 6: rankChoice = "S"; break;
            case 7: return;
            default: 
                System.out.println("Invalid choice, defaulting to E-Rank");
                rankChoice = "E";
        }
        
        // Check rank requirements
        String playerRank = player.getRank();
        if (getRankValue(rankChoice) > getRankValue(playerRank)) {
            System.out.println("\n> Access denied! You need at least " + rankChoice + "-Rank");
            return;
        }
        
        Dungeon dungeon = new Dungeon(rankChoice);
        
        // Red gate mechanics
        if (dungeon.isRedGate()) {
            System.out.println("\n> WARNING: RED GATE DETECTED!");
            System.out.println("> You cannot leave until you clear all floors!");
        }
        
        // Double dungeon chance
        Random rand = new Random();
        if(rand.nextDouble() < 0.05) {
            Dungeon doubleDungeon = new Dungeon("S");
            System.out.println("\n> WARNING: Hidden Double Dungeon detected!");
            System.out.println("> You've been transported to " + doubleDungeon.getName() + " Dungeon!");
            dungeon = doubleDungeon;
        }
        
        System.out.println("\n==============================");
        System.out.println("You are entering the " + dungeon.getName() + " Dungeon.");
        System.out.println("==============================");
        
        dungeonStack.push(dungeon);
        exploreDungeon(dungeon);
    }
    
    private int getRankValue(String rank) {
        switch(rank) {
            case "E": return 1;
            case "D": return 2;
            case "C": return 3;
            case "B": return 4;
            case "A": return 5;
            case "S": return 6;
            default: return 0;
        }
    }
    
    private void exploreDungeon(Dungeon dungeon) {
        Random rand = new Random();
        boolean inDungeon = true;
        while(inDungeon && player.isAlive() && dungeon.getCurrentFloor() <= dungeon.getTotalFloors()) {
            System.out.println("\n==============================");
            System.out.println("You are on Floor " + dungeon.getCurrentFloor() + "/" + dungeon.getTotalFloors());
            System.out.println("Layout: " + dungeon.getFloorLayout(dungeon.getCurrentFloor()));
            System.out.println("==============================");
            
            // Apply floor event
            String floorEvent = dungeon.getFloorEvent(dungeon.getCurrentFloor());
            if (!floorEvent.isEmpty()) {
                System.out.println("> ENVIRONMENT: " + floorEvent);
                applyFloorEffect(floorEvent);
            }
            
            // Dynamic events
            if (Math.random() < 0.4) {
                String event = eventSystem.generateEvent(player);
                eventSystem.handleEvent(event, player);
            }
            
            System.out.println("Your Health: " + player.getHP());
            
            // Generate monsters
            int monsterCount = generateMonsterCount(dungeon);
            List<Monster> monsters = new ArrayList<>();
            for (int i = 0; i < monsterCount; i++) {
                monsters.add(new Monster(dungeon.getCurrentFloor(), dungeon.getRank()));
            }
            
            System.out.println("Monsters: " + monsterCount);
            
            // List monsters with behaviors
            for (int i = 0; i < monsters.size(); i++) {
                Monster m = monsters.get(i);
                System.out.println((i+1) + ". " + m.getName() + 
                                  " | " + m.getBattleBehavior());
            }
            
            System.out.println("0. Leave Dungeon");
            System.out.print("Choose a monster to attack (1-" + monsterCount + "): ");
            
            int choice = getIntInput();
            scanner.nextLine(); // Consume newline
            
            if (choice == 0) {
                if (dungeon.isRedGate()) {
                    System.out.println("Cannot leave Red Gate dungeon!");
                } else {
                    System.out.println("You leave the dungeon");
                    return;
                }
            }
            
            if (choice < 1 || choice > monsterCount) {
                System.out.println("Invalid choice!");
                continue;
            }
            
            Monster target = monsters.get(choice-1);
            System.out.println("You attack the " + target.getName() + "!");
            
            // Combat with selected monster
            boolean monsterDefeated = combat(target, dungeon);
            player.recordBattleResult(monsterDefeated);
            
            if (!player.isAlive()) {
                System.out.println("You have been defeated!");
                if (dungeon.getRank().equals("S")) {
                    gameSystem.awakenHunter(player);
                    player.heal(player.getMaxHP());
                    System.out.println("> SYSTEM: You have been revived by the System!");
                }
                return;
            }
            
            if (monsterDefeated) {
                // Check if all monsters are defeated
                boolean allDefeated = true;
                for (Monster m : monsters) {
                    if (m.isAlive()) {
                        allDefeated = false;
                        break;
                    }
                }
                
                if (allDefeated) {
                    System.out.println("\n> All monsters on this floor defeated!");
                    
                    // Last floor check
                    if (dungeon.isLastFloor()) {
                        System.out.println("\n> DUNGEON CLEARED! Congratulations!");
                        player.checkQuestCompletion(dungeon.getName(), dungeon.getRank());
                        player.addFame(20);
                        player.addReputation("Guild", 10);
                        return;
                    }
                    
                    System.out.println("> Advancing to next floor...");
                    dungeon.nextFloor();
                    
                    // Chance to find healing
                    if (rand.nextDouble() < 0.4) {
                        System.out.println("> You found a Health Potion!");
                        player.addItem("Health Potion");
                    }
                }
            }
        }
    }
    
    private int generateMonsterCount(Dungeon dungeon) {
        int baseCount = 1 + dungeon.getCurrentFloor();
        if (dungeon.isLastFloor()) {
            return 1; // Boss only
        }
        return Math.min(baseCount, 5);
    }
    
    private void applyFloorEffect(String event) {
        switch(event) {
            case "Mysterious Fog: Visibility reduced":
                System.out.println("Accuracy reduced by 20%");
                break;
            case "Energized Air: Mana regeneration doubled":
                System.out.println("Mana regenerates faster");
                break;
            case "Toxic Spores: Take 5 damage per turn":
                player.takeDamage(5);
                System.out.println("You take 5 damage from toxic spores!");
                break;
            case "Ancient Runes: Skill effectiveness increased":
                System.out.println("Skills are 30% more effective");
                break;
            case "Haunted Grounds: Random status effects":
                StatusEffect effect = new StatusEffect("Haunted", 3, 0, 0.9, 1.1, false, false);
                player.setStatusEffect(effect);
                System.out.println("You feel haunted!");
                break;
        }
    }
    
    private boolean combat(Monster monster, Dungeon dungeon) {
        boolean inCombat = true;
        boolean playerTurn = true;
        int round = 1;
        
        System.out.println("\n===== BATTLE START =====");
        System.out.println(monster.getBattleBehavior());
        
        while(inCombat && player.isAlive() && monster.isAlive()) {
            System.out.println("\n--- Round " + round + " ---");
            
            if (playerTurn) {
                // Player's turn
                playerTurn = false;
                
                // Regenerate mana
                int manaRegen = 2 + (player.getIntelligence() / 5);
                player.addMana(manaRegen);
                System.out.println("Regenerated " + manaRegen + " mana");
                
                System.out.println("\n" + monster.getName() + " | Health: " + monster.getHealth());
                System.out.println("Your HP: " + player.getHP() + " | Mana: " + player.getMana());
                System.out.println("1. Attack");
                
                // Only show skills if awakened
                if (player.isAwakened()) {
                    System.out.println("2. Use Skill");
                    System.out.println("3. Use Item");
                    System.out.println("4. Attempt Flee");
                } else {
                    System.out.println("2. Use Item");
                    System.out.println("3. Attempt Flee");
                }
                
                System.out.print("Select: ");
                
                int action = getIntInput();
                scanner.nextLine(); // Consume newline
                int damage = 0;
                
                if (player.isAwakened()) {
                    switch(action) {
                        case 1: 
                            if (player.getWeapon().use()) {
                                damage = player.calculateDamage();
                                System.out.println("You deal " + damage + " damage!");
                                monster.takeDamage(damage);
                            } else {
                                System.out.println("Your weapon broke! Attack failed.");
                            }
                            break;
                        case 2: 
                            useSkill();
                            break;
                        case 3: 
                            useItem();
                            break;
                        case 4: 
                            if (attemptFlee(dungeon)) return false;
                            break;
                        default:
                            System.out.println("Invalid choice, you hesitate...");
                    }
                } else {
                    switch(action) {
                        case 1: 
                            if (player.getWeapon().use()) {
                                damage = player.calculateDamage();
                                System.out.println("You deal " + damage + " damage!");
                                monster.takeDamage(damage);
                            } else {
                                System.out.println("Your weapon broke! Attack failed.");
                            }
                            break;
                        case 2: 
                            useItem();
                            break;
                        case 3: 
                            if (attemptFlee(dungeon)) return false;
                            break;
                        default:
                            System.out.println("Invalid choice, you hesitate...");
                    }
                }
            } else {
                // Monster's turn
                playerTurn = true;
                round++;
                
                // AI Assistant
                if (round % 3 == 0) {
                    provideCombatAdvice(player, monster);
                }
                
                // Monster behavior-based actions
                String behavior = monster.selectBehavior(player);
                int baseDamage = monster.getDamage();
                int modifiedDamage = baseDamage;
                
                switch(behavior) {
                    case "Aggressive":
                        modifiedDamage = (int)(baseDamage * 1.3);
                        System.out.println(monster.getName() + " attacks aggressively!");
                        break;
                    case "Defensive":
                        modifiedDamage = (int)(baseDamage * 0.7);
                        System.out.println(monster.getName() + " takes defensive stance");
                        break;
                    case "Cunning":
                        if (new Random().nextDouble() < 0.4) {
                            System.out.println(monster.getName() + " uses hit-and-run tactics");
                            modifiedDamage = (int)(baseDamage * 0.8);
                        }
                        break;
                    case "Berserk":
                        modifiedDamage = (int)(baseDamage * (1 + round * 0.1));
                        System.out.println(monster.getName() + " grows more berserk!");
                        break;
                }
                
                // Use random ability
                if (!monster.getAbilities().isEmpty() && new Random().nextDouble() < 0.6) {
                    String ability = monster.getAbilities().get(
                        new Random().nextInt(monster.getAbilities().size()));
                    System.out.println(">> " + monster.getName() + " uses " + ability + "!");
                    modifiedDamage = applyMonsterAbility(ability, modifiedDamage, monster);
                }
                
                System.out.println(monster.getName() + " attacks for " + modifiedDamage + " damage!");
                player.takeDamage(modifiedDamage);
                
                // Update AI learning
                boolean effective = (modifiedDamage > baseDamage * 1.2);
                monster.updateBehaviorEffectiveness(effective);
            }
            
            // Apply end of turn effects
            player.applyEndOfTurnEffects();
        }
        
        if(!player.isAlive()) {
            return false;
        }
        
        if(!monster.isAlive()) {
            // Victory rewards
            System.out.println("\n> Victory! You defeated the " + monster.getName());
            System.out.println("> Rewards: " + monster.getExpReward() + " XP, " + 
                              monster.getGoldReward() + " Gold");
            player.addExperience(monster.getExpReward());
            player.addGold(monster.getGoldReward());
            player.addFame(2);
            
            // Boss drops
            if (dungeon.getRank().equals("S") || dungeon.getRank().equals("A")) {
                if (new Random().nextDouble() < 0.3) {
                    System.out.println("> The monster dropped a special weapon!");
                    player.setWeapon(new Random().nextDouble() < 0.5 ? DOS_PURDOS : SANGGA);
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    private int applyMonsterAbility(String ability, int baseDamage, Monster monster) {
        switch(ability) {
            case "Fire Breath":
                System.out.println("You're set on fire!");
                player.setStatusEffect(new StatusEffect("Burning", 3, 5, 1.0, 1.0, false, false));
                return (int)(baseDamage * 1.2);
            case "Poison Sting":
                System.out.println("You've been poisoned!");
                player.setStatusEffect(new StatusEffect("Poisoned", 5, 3, 1.0, 1.0, false, false));
                return (int)(baseDamage * 0.9);
            case "Frost Bite":
                System.out.println("You're chilled!");
                player.setStatusEffect(new StatusEffect("Chilled", 2, 0, 0.8, 1.0, false, false));
                return baseDamage;
            case "Life Drain":
                int healAmount = (int)(baseDamage * 0.5);
                monster.takeDamage(-healAmount);
                System.out.println(monster.getName() + " drains your life and heals " + healAmount + " HP!");
                return baseDamage;
            case "Thunder Strike":
                System.out.println("You're stunned!");
                player.setStatusEffect(new StatusEffect("Stunned", 1, 0, 1.0, 1.0, false, false));
                return (int)(baseDamage * 1.3);
            case "Ultimate Attack":
                System.out.println("> ULTIMATE ATTACK!");
                return (int)(baseDamage * 2.5);
            default:
                return baseDamage;
        }
    }

    private void useSkill() {
        if (!player.isAwakened()) return;
        
        Map<String, Skill> skills = player.getSkills();
        if (skills.isEmpty()) {
            System.out.println("No skills available!");
            return;
        }
        
        System.out.println("Available Skills:");
        List<Skill> skillList = new ArrayList<>(skills.values());
        for (int i = 0; i < skillList.size(); i++) {
            Skill skill = skillList.get(i);
            System.out.println((i+1) + ". " + skill.getName() + 
                              " (" + skill.getManaCost() + " MP)" +
                              " [Mastery Lv " + skill.getMasteryLevel() + "]");
        }
        System.out.println((skillList.size()+1) + ". Cancel");
        
        System.out.print("Select skill: ");
        int skillChoice = getIntInput();
        scanner.nextLine();
        if (skillChoice == skillList.size()+1) return;
        
        if (skillChoice > 0 && skillChoice <= skillList.size()) {
            Skill selectedSkill = skillList.get(skillChoice-1);
            
            // Update skill effectiveness based on context
            if (player.getHP() < player.getMaxHP() * 0.3) {
                selectedSkill.updateSituationalBoost("low_health");
            } else {
                selectedSkill.updateSituationalBoost("dungeon");
            }
            
            if (player.getMana() >= selectedSkill.getManaCost()) {
                player.setCurrentMana(player.getMana() - selectedSkill.getManaCost());
                switch(selectedSkill.getName()) {
                    case "Healing":
                        int healAmount = (int)(20 * selectedSkill.getEffectiveBonus());
                        player.heal(healAmount);
                        System.out.println("> Healed " + healAmount + " HP!");
                        player.increaseSkillMastery("Healing");
                        break;
                    case "Flux":
                        player.setStatusEffect(new StatusEffect("Flux", 3, 0, 1.3, 1.3, true, false));
                        System.out.println("> Attack and defense boosted by 30%!");
                        player.increaseSkillMastery("Flux");
                        break;
                    default:
                        System.out.println("Skill effect not implemented");
                }
            } else {
                System.out.println("Not enough mana!");
            }
        } else {
            System.out.println("Invalid choice!");
        }
    }
    
    private void useItem() {
        System.out.println("1. Health Potion");
        System.out.println("2. Mana Potion");
        System.out.println("3. Weapon Repair Kit");
        System.out.println("4. Cancel");
        System.out.print("Select: ");
        
        int itemChoice = getIntInput();
        scanner.nextLine();
        switch(itemChoice) {
            case 1: 
                player.useItem("Health Potion");
                break;
            case 2:
                player.useItem("Mana Potion");
                break;
            case 3:
                player.useItem("Weapon Repair Kit");
                break;
        }
    }
    
    private boolean attemptFlee(Dungeon dungeon) {
        if (dungeon.isRedGate()) {
            System.out.println("Cannot flee from Red Gate dungeons!");
            return false;
        }
        
        if (new Random().nextDouble() < 0.4) {
            System.out.println("You escaped successfully!");
            return true;
        }
        
        System.out.println("Escape failed!");
        return false;
    }
    
    private void provideCombatAdvice(Hunter player, Monster monster) {
        String advice = aiAssistant.generateCombatAdvice(player, monster);
        System.out.println("\n> COMBAT ASSISTANT: " + advice);
    }
    
    // Adaptive difficulty system
    public void adjustDifficulty(Hunter player) {
        double winRate = player.getSuccessRate();
        double aggression = difficultySettings.get("monster_aggression");
        
        if (winRate > 0.7) {
            difficultySettings.put("monster_aggression", Math.min(0.9, aggression + 0.05));
            difficultySettings.put("item_rarity", Math.max(0.3, difficultySettings.get("item_rarity") - 0.03));
        } else if (winRate < 0.4) {
            difficultySettings.put("monster_aggression", Math.max(0.3, aggression - 0.05));
            difficultySettings.put("item_rarity", Math.min(0.9, difficultySettings.get("item_rarity") + 0.03));
        }
        
        // Dynamic notifications
        if (aggression != difficultySettings.get("monster_aggression")) {
            System.out.println("> SYSTEM: Game difficulty has " + 
                              (aggression < difficultySettings.get("monster_aggression") ? 
                              "increased" : "decreased") + " based on your performance");
        }
    }
    
    private void generateRandomEvent() {
        String[] events = {
            "You meet a fellow hunter who shares a healing potion with you",
            "A reporter recognizes you and asks for an interview",
            "You find a hidden cache of supplies",
            "A sudden storm forces you to take shelter",
            "You stumble upon a merchant's lost caravan"
        };
        
        if (new Random().nextDouble() < 0.3) {
            String event = events[new Random().nextInt(events.length)];
            System.out.println("\n> RANDOM EVENT: " + event);
            applyEventEffect(event);
        }
    }
    
    private void applyEventEffect(String event) {
        switch(event) {
            case "You meet a fellow hunter who shares a healing potion with you":
                player.addItem("Health Potion");
                player.addReputation("Guild", 5);
                break;
            case "A reporter recognizes you and asks for an interview":
                player.addFame(10);
                break;
            case "You find a hidden cache of supplies":
                player.addItem("Health Potion");
                player.addItem("Mana Potion");
                break;
            case "A sudden storm forces you to take shelter":
                player.takeDamage(10);
                break;
            case "You stumble upon a merchant's lost caravan":
                player.addGold(50);
                player.addReputation("Merchants", 10);
                break;
        }
    }

    public static void main(String[] args) {
        HunterGame game = new HunterGame();
        game.startGame();
    }
}