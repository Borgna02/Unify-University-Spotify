package unify.nullpointerexception.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Song {
    private Integer id;
    private String title;
    private LocalDate releaseDate;

    private Genre genre;

    private List<Artist> artists = new ArrayList<Artist>();

    public Song() {
    }

    public Song(Integer id, String title, LocalDate releaseDate, Genre genre) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.genre = genre;
    }

    public Integer getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public Genre getGenre() {
        return this.genre;
    }

    public List<Artist> getArtists() {
        return this.artists;
    }

    // setter

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setArtist(Artist artist) {
        this.artists.add(0, artist);
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public String toStringWithMainArtist() {
        return title + " - " + artists.get(0).getStageName() + " - " + genre.getName();
    }

    public String toString() {
        String message = new String();
        message =  title + "-" + genre.getName();
        for (Artist a : this.artists){
            message = message + "-" + a.getStageName();
        }
        return message;
    }

    public boolean equals(Song other) {
        return this.id.equals(other.id);
    }


}
