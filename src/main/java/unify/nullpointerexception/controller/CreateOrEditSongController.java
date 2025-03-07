package unify.nullpointerexception.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import unify.nullpointerexception.business.AlbumService;
import unify.nullpointerexception.business.ArtistsService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.GenreService;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Album;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.Genre;
import unify.nullpointerexception.domain.Song;
import unify.nullpointerexception.view.ViewDispatcher;

/*se la canzone è null è una creazione, se è piena è una modifica.*/
public class CreateOrEditSongController implements Initializable, DataInitializable<Song> {

    @FXML
    private Button backButton;

    @FXML
    private Text pageTitle;

    @FXML
    private TextField songName;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField searchArtistBar;

    @FXML
    private ListView<Artist> searchedArtistsList;

    @FXML
    private ListView<Artist> artistsList;

    @FXML
    private TextField searchGenreBar;

    @FXML
    private ListView<Genre> genresList;

    @FXML
    private ImageView songCover;

    @FXML
    private Button browseImage;

    @FXML
    private ImageView tempSongCover;

    @FXML
    private Button browseAudioFile;

    @FXML
    private TextArea lyricBox;

    @FXML
    private Button removeArtistBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private HBox audioUploadBox;

    @FXML
    private Text audioUploadMsg;

    @FXML
    private Button addArtistBtn;

    private ViewDispatcher dispatcher;

    private ArtistsService artistService;
    private GenreService genreService;
    private MusicService musicService;
    private AlbumService albumService;

    private File newSongCoverFile;
    private File newSongFile;
    private byte[] newSongCover;

    private String oldText;

    private Boolean newSongLoaded = false;
    // queste due variabili hanno compiti differenti perché newSongLoaded serve
    // a verificare se la canzone è stata modificata mentre isSongLoaded serve a
    // verificare se esiste una canzone caricata.
    private Boolean isSongLoaded = false;
    private Song song;
    private Boolean isNew;
    private final Integer LIST_ROW_HEIGHT = 24;

    private List<Artist> removedArtists = new ArrayList<>();

    // costruttore
    public CreateOrEditSongController() {

        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
        artistService = factory.getArtistsService();
        genreService = factory.getGenreService();
        musicService = factory.getMusicService();
        albumService = factory.getAlbumService();

        dispatcher = ViewDispatcher.getInstance();
    }

    // inizializzo il controller
    @Override
    public void initialize(URL location, ResourceBundle resource) {

    }

    // inizializzo i dati del controller
    @Override
    public void initializeData(Song song) {
        this.song = song;
        isNew = song == null;

        if (isNew) {
            isSongLoaded = false;
            this.song = new Song();
            pageTitle.setText("Crea canzone");
            deleteBtn.setMaxWidth(0);
            deleteBtn.setVisible(false);
        } else {
            artistsList.setItems(FXCollections.observableArrayList(song.getArtists()));
            pageTitle.setText("Modifica la canzone: " + song.getTitle());
            songName.setText(song.getTitle());
            datePicker.setValue(song.getReleaseDate());
            searchGenreBar.setText(song.getGenre().getName());
            genresList.getSelectionModel().select(song.getGenre());
            try {
                byte[] cover = musicService.fetchCover(song);
                if (cover != null)
                    tempSongCover.setImage(new Image(new ByteArrayInputStream(cover)));
            } catch (BusinessException e) {
                e.printStackTrace();
            }
            audioUploadBox.setStyle("-fx-background-color: #9cd69e; -fx-alignment: center; -fx-text-alignment: center");
            audioUploadMsg.setText("File caricato\nprecedentemente");
            audioUploadMsg.setStyle("-fx-fill:#07b00d;  -fx-alignment: center");
            isSongLoaded = true;
            try {
                lyricBox.setText(this.oldText = musicService.fetchLyrics(song));
            } catch (BusinessException e) {
                e.printStackTrace();
            }

        }

    }

    // metodo per cercare l'artista
    @FXML
    public void searchArtist(KeyEvent event) {
        String text = searchArtistBar.getText();
        List<Artist> artists;

        try {

            artists = artistService.findArtistByNamePrefix(text);

            if (text.isEmpty()) {

                searchedArtistsList.setVisible(false);
                searchedArtistsList.getItems().clear();
            } else {
                if (!artists.isEmpty()) {
                    searchedArtistsList.setItems(FXCollections.observableArrayList(artists));
                    searchedArtistsList.maxHeightProperty()
                            .bind(Bindings.size(FXCollections.observableArrayList(artists)).multiply(LIST_ROW_HEIGHT));

                    searchedArtistsList.setVisible(true);
                } else {
                    searchedArtistsList.setVisible(false);
                    searchedArtistsList.getItems().clear();
                }
            }

        } catch (BusinessException e) {
            e.printStackTrace();
        }

    }

    // metodo chiamato quando si clicca sull'artista scelto
    @FXML
    public void artistsListItemClick(MouseEvent event) {
        searchedArtistsList.setVisible(false);
        searchArtistBar.setText(searchedArtistsList.getSelectionModel().getSelectedItem().getStageName());
        addArtistBtn.setDisable(false);
    }

    // metodo per aggiungere un artista alla canzone
    @FXML
    public void addArtistToSong(ActionEvent event) {
        for (Artist artist : artistsList.getItems()) {
            if (artist.equals(searchedArtistsList.getSelectionModel().getSelectedItem())) {
                return;
            }
        }
        song.getArtists().add(searchedArtistsList.getSelectionModel().getSelectedItem());
        artistsList.setItems(FXCollections.observableArrayList(song.getArtists()));
        fieldsComplete();

    }

    // metodo chiamato quando si seleziona un artista che abilita il tasto elimina
    @FXML
    public void selectAddedArtist(MouseEvent event) {
        removeArtistBtn.setDisable(false);
    }

    // metodo per rimuovere un artista
    @FXML
    public void removeArtist(ActionEvent event) {

        song.getArtists().remove(artistsList.getSelectionModel().getSelectedItem());
        // mantengo gli artisti rimossi dalla canzone per quando salvo le modifiche
        removedArtists.add(artistsList.getSelectionModel().getSelectedItem());

        artistsList.setItems(FXCollections.observableArrayList(song.getArtists()));

        removeArtistBtn.setDisable(true);

        fieldsComplete();
    }

    // metodo chiamato quando si inserisce o rimuove un carattere dal box di ricerca
    @FXML
    public void searchGenre(KeyEvent event) {
        String text = searchGenreBar.getText();
        List<Genre> genres;
        try {
            // choice contiene il filtro della ricerca scelto dall'utente tra titolo e
            // genere

            genres = genreService.findGenresByNamePrefix(text);
            if (text.isEmpty()) {
                genresList.setVisible(false);
                genresList.getItems().clear();
            } else {
                genresList.setItems(FXCollections.observableArrayList(genres));
                genresList.setVisible(true);
                // in questo modo l'altezza della lista è sempre pari alla somma di quelle degli
                // elementi che la compongono
                genresList.maxHeightProperty()
                        .bind(Bindings.size(FXCollections.observableArrayList(genres)).multiply(LIST_ROW_HEIGHT));
            }

            fieldsComplete();
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }

    // metodo chiamato quando si clicca sull'entry del genere scelto
    @FXML
    public void genreListItemClick(MouseEvent event) {
        genresList.setVisible(false);
        genresList.setAccessibleText(genresList.getSelectionModel().getSelectedItem().getName());
        searchGenreBar.setText(genresList.getSelectionModel().getSelectedItem().getName());
        fieldsComplete();
    }

    // metodo per caricare il file immagine
    @FXML
    public void browseImage(ActionEvent event) {
        // apro l'esplora risorse e caricare il file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Scegli immagine");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        newSongCoverFile = dispatcher.uploadFile(fileChooser);

        // carico la nuova immagine nella "preview" all'interno della vista per la
        // gestione della cover

        if (newSongCoverFile != null) {

            try {

                newSongCover = Files.readAllBytes(newSongCoverFile.toPath());
                tempSongCover.setImage(new Image(new ByteArrayInputStream(newSongCover)));
                fieldsComplete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // metodo per caricare il file audio
    @FXML
    public void browseAudio(ActionEvent event) {
        // apro l'esplora risorse e caricare il file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Scegli file audio");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Audio", "*.wav"));
        newSongFile = dispatcher.uploadFile(fileChooser);

        if ((newSongFile != null)) {
            audioUploadBox.setStyle("-fx-background-color: #9cd69e; -fx-alignment: center");
            audioUploadMsg.setText("File caricato");
            audioUploadMsg.setStyle("-fx-fill:#07b00d");
            newSongLoaded = true;
            isSongLoaded = true;

        }
        fieldsComplete();
    }

    // metodo per salvare le modifiche
    @FXML
    public void saveSongChanges(ActionEvent event) {

        song.setTitle(songName.getText());
        song.setReleaseDate(datePicker.getValue());
        song.setGenre(genresList.getSelectionModel().getSelectedItem());

        byte[] newSongByte = null;
        String newText = null;

        if (isNew) {

            try {
                newSongByte = Files.readAllBytes(newSongFile.toPath());
                musicService.createSong(song, lyricBox.getText(), newSongByte, newSongCover);
            } catch (BusinessException | IOException e) {
                e.printStackTrace();
            }

        } else {

            try {

                if (newSongLoaded)
                    newSongByte = Files.readAllBytes(newSongFile.toPath());

                if (!oldText.equals(lyricBox.getText()))
                    newText = lyricBox.getText();

                if (!removedArtists.isEmpty()) {
                    for (Artist artist : removedArtists) {
                        try {
                            List<Album> artistAlbums = albumService.findAllAlbumByArtist(artist);
                            for (Album album : artistAlbums) {
                                Song songToRemove = null;
                                for (Song tempSong : album.getSongs()) {
                                    if (tempSong.equals(song)) {
                                        songToRemove = tempSong;
                                        break;
                                    }
                                }
                                album.getSongs().remove(songToRemove);

                                if (album.getSongs().isEmpty())
                                    albumService.deleteAlbum(album);
                                else
                                    albumService.editAlbum(album, null);
                            }

                        } catch (BusinessException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    musicService.editSong(song, newText, newSongByte, newSongCover);
                } catch (BusinessException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        goBack();

    }

    // metodo per eliminare la canzone
    @FXML
    public void deleteSong(ActionEvent event) {
        try {
            if (dispatcher.confirmAlert("Vuoi davvero eliminare questa canzone?")) {

                List<Album> albums = albumService.findAllAlbumBySong(song);
                for (Album album : albums) {

                    Song songToRemove = null;
                    for (Song tempSong : album.getSongs()) {
                        if (tempSong.equals(song)) {
                            songToRemove = tempSong;
                            break;
                        }
                    }
                    album.getSongs().remove(songToRemove);

                    if (album.getSongs().isEmpty())
                        albumService.deleteAlbum(album);
                    else
                        albumService.editAlbum(album, null);
                }

                musicService.deleteSong(song);
                dispatcher.renderView("adminSongs", null);
            }
        } catch (BusinessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // metodo per controllare che i campi obbligatori siano pieni
    public void fieldsComplete() {

        if (!songName.getText().isEmpty() && datePicker.getValue() != null
                && genresList.getSelectionModel().getSelectedItem() != null && !artistsList.getItems().isEmpty()
                && isSongLoaded) {
            saveBtn.setDisable(false);
            return;
        }
        saveBtn.setDisable(true);

    }

    // metodo per tornare alla schermata precedente
    public void goBack() {
        dispatcher.goBack();
    }

}