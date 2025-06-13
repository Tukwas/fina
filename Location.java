/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bosakon.tobehunter;

class Location {
    private String name;
    private String description;
    
    public Location(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public void enter() {
        System.out.println("\n======================================");
        System.out.println("                " + name.toUpperCase());
        System.out.println("======================================");
        System.out.println(description);
    }
    
    public String getName() { return name; }
}
