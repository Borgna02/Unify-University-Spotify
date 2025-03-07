package unify.nullpointerexception.domain;

import java.util.*;

public class Playlist {

    private Integer id;
    private User creator;
    private String name;
    private ArrayList<Song> songs;

    public Playlist(Integer id, User creator, String name) {
        this.id = id;
        this.creator = creator;
        this.name = name;
    }

    public Playlist() {
        this.songs = new ArrayList<Song>();
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Song> getSongs() {
        return this.songs;
    }

    public User getCreator() {
        return this.creator;
    }

    public boolean equals(Playlist other) {
        return this.id.equals(other.id);
    }
}
