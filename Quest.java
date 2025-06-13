/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bosakon.tobehunter;

class Quest {
    private String title;
    private String objective;
    private int rewardXP;
    private int rewardGold;
    private String unlockSkill;
    private String factionReward;
    private int reputationReward;

    public Quest(String title, String objective, int rewardXP, int rewardGold, 
                String unlockSkill, String factionReward, int reputationReward) {
        this.title = title;
        this.objective = objective;
        this.rewardXP = rewardXP;
        this.rewardGold = rewardGold;
        this.unlockSkill = unlockSkill;
        this.factionReward = factionReward;
        this.reputationReward = reputationReward;
    }

    public void complete(Hunter player) {
        GameSystem.out.println("\n> QUEST COMPLETE: " + title);
        GameSystem.out.println("> Rewards: " + rewardXP + " XP, " + rewardGold + " Gold");
        player.addExperience(rewardXP);
        player.addGold(rewardGold);
        
        if (unlockSkill != null) {
            player.unlockSkill(unlockSkill);
        }
        
        if (factionReward != null && reputationReward != 0) {
            player.addReputation(factionReward, reputationReward);
            GameSystem.out.println("> Gained " + reputationReward + " reputation with " + factionReward);
        }
    }

    public String getTitle() { return title; }
    public String getObjective() { return objective; }
}
