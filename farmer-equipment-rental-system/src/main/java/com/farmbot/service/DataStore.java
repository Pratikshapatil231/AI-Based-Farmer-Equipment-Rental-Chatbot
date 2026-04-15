package com.farmbot.service;

import com.farmbot.model.Equipment;

import java.util.List;

public class DataStore {
    public List<Equipment> equipmentList() {
        return List.of(
                new Equipment("Mahindra Tractor 575", "tractor", 2500, true, 8.5, 4.5, "Ramesh Patil", "9876543210"),
                new Equipment("Power Tiller PT-9", "tiller", 1200, true, 5.0, 4.1, "Suresh Pawar", "9988776655"),
                new Equipment("Rotavator RV-5", "rotavator", 1600, false, 12.0, 4.0, "Ganesh More", "9090909090"),
                new Equipment("Seed Drill SD-2", "seeding", 900, true, 3.2, 4.4, "Vijay Jadhav", "9123456780"),
                new Equipment("Combine Harvester CH-4", "harvester", 5500, true, 20.0, 4.7, "Anil Shinde", "9765432108"),
                new Equipment("Sprayer SP-3", "sprayer", 700, true, 2.5, 4.2, "Rajendra Kale", "9001122334")
        );
    }
}
