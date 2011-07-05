package com.metservice.kanban.charts.cycletime;

public class CycleTimeFragment {
    
    private String name;
    private Integer weight;
    
    public CycleTimeFragment(String name, Integer weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public Integer getWeight() {
        return weight;
    }
}
