package unify.nullpointerexception.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Album {
    private Integer id;
    private Genre genre;
    private String name;
    private LocalDate releaseDate;
    private Artist artist;
    private List<Song> songs = new ArrayList<>();

    public Album() {
    }

    public Album(Integer id, Genre genre, String name, LocalDate releaseDate, Artist artist) {
        this.id = id;
        this.genre = genre;
        this.name = name;
        this.releaseDate = releaseDate;
        this.artist = artist;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public Genre getGenre() {
        return this.genre;
    }

    public Artist getArtist() {
        return this.artist;
    }

    public List<Song> getSongs() {
        return this.songs;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }


    //setters

    
    public void setId(Integer id) {
        this.id = id;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public void setSongs(List<Song> songs){
        this.songs = songs;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }


    public String toString(){

        return name + " - " + artist.getStageName() + " - " + genre.getName();
    }

    public boolean equals(Album album){
        return this.id.equals(album.id);
    }

    
    
}
