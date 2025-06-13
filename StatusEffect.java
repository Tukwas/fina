/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bosakon.tobehunter;

class StatusEffect {
    private String name;
    private int duration;
    private int maxDuration;
    private int damagePerTurn;
    private double damageModifier;
    private double defenseModifier;
    private boolean isBuff;
    private boolean isPersistent;

    public StatusEffect(String name, int duration, int damagePerTurn, 
                       double damageModifier, double defenseModifier, 
                       boolean isBuff, boolean isPersistent) {
        this.name = name;
        this.duration = duration;
        this.maxDuration = duration;
        this.damagePerTurn = damagePerTurn;
        this.damageModifier = damageModifier;
        this.defenseModifier = defenseModifier;
        this.isBuff = isBuff;
        this.isPersistent = isPersistent;
    }

    public boolean applyEffect() {
        if (duration > 0) {
            duration--;
            return true;
        }
        return false;
    }

    // AI-based effect renewal - smarter status management
    public boolean shouldRenew() {
        if (isPersistent) return false;
        return duration < maxDuration * 0.3 && Math.random() > 0.7;
    }

    public String getName() { return name; }
    public int getDamagePerTurn() { return damagePerTurn; }
    public double getDamageModifier() { return damageModifier; }
    public double getDefenseModifier() { return defenseModifier; }
    public boolean isBuff() { return isBuff; }
    public boolean isPersistent() { return isPersistent; }
    public int getDuration() { return duration; }
    public int getMaxDuration() { return maxDuration; }
}

