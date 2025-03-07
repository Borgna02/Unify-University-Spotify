package unify.nullpointerexception.domain;

public class Genre {
    private Integer id;
    private String name;

    
    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public Genre() {
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }    


    public String toString(){
        return name;
    }
    public boolean equals(Genre other){
        return this.id.equals(other.id);
    }
}
