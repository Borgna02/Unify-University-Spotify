package unify.nullpointerexception.domain;

public class ArtistPicture {

    private Integer id;
    private byte[] data;
    private Artist artist;
    private String caption;

    // getters

    public ArtistPicture() {
    }

    public ArtistPicture(Integer id, byte[] data, Artist artist, String caption) {
        this.id = id;
        this.data = data;
        this.artist = artist;
        this.caption = caption;
    }

    public Integer getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public String getCaption() {
        return caption;
    }

    public Artist getArtist() {
        return artist;
    }

    // setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public boolean equals(ArtistPicture other) {
        return this.id.equals(other.id);
    }

    public String toString() {
        return caption;
    }

}
