package com.example.mainscreen;

public class Troupe {
    private String id;
    private String name;
    private String director;
    private String role;

    public Troupe() {
        // Default constructor required for calls to DataSnapshot.getValue(Troupe.class)
    }

    public Troupe(String id, String name, String director, String role) {
        this.id = id;
        this.name = name;
        this.director = director;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

