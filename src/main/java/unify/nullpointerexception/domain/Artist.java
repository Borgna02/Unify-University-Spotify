package unify.nullpointerexception.domain;

public class Artist {
    
    private Integer id;
    private String stageName;

    public Artist(Integer id, String stageName) {
        this.id = id;
        this.stageName = stageName;
    }

    public Artist() {
    }

    public Integer getId() {
        return id;
    }

    public String getStageName() {
        return this.stageName;
    }

    //setter

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStageName(String stageName){
        this.stageName = stageName;
    }


    public String toString(){
        return stageName;
    }
    public boolean equals(Artist a){
        return this.id.equals(a.id);
    }

}
