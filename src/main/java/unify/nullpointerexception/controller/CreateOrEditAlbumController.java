package unify.nullpointerexception.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Pair;
import unify.nullpointerexception.business.AlbumService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.GenreService;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Album;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.Song;
import unify.nullpointerexception.view.ViewDispatcher;

public class CreateOrEditAlbumController implements Initializable, DataInitializable<Pair<Album, Artist>> {

    private ViewDispatcher dispatcher;
    private AlbumService albumService;
    private MusicService musicService;
    private GenreService genreService;

    private Boolean isAlbumNew;

    private final Integer LIST_ROW_HEIGHT = 24;

    @FXML
    private Text pageTitle;

    @FXML
    private TextField albumName;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ChoiceBox<String> genreChoice;

    @FXML
    private TextField searchBar;

    @FXML
    private Button addSongButton;

    @FXML
    private ChoiceBox<String> choice;

    @FXML
    private ListView<Song> addedSongsList;

    @FXML
    private ListView<Song> searchList;

    @FXML
    private Button saveAlbumBtn;

    @FXML
    private Button removeSong;

    @FXML
    private ImageView tempAvatar;

    @FXML
    private HBox errorBox;

    @FXML
    private Text errorText;

    private File newCoverFile;
    private byte[] cover;

    private Album album;

    // costruttore
    public CreateOrEditAlbumController() {
        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
        albumService = factory.getAlbumService();
        musicService = factory.getMusicService();

        genreService = factory.getGenreService();

        dispatcher = ViewDispatcher.getInstance();
    }

    // inizializzo il controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        choice.setItems(FXCollections.observableArrayList("Titolo", "Genere"));
        choice.getSelectionModel().selectFirst();

        try {

            genreChoice.setItems(FXCollections.observableArrayList(genreService.getAllGenresNames()));

        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }

    // inizializzo i dati del controller
    @Override
    public void initializeData(Pair<Album, Artist> pair) {
        album = pair.getKey();
        isAlbumNew = (album == null);

        if (!isAlbumNew) {
            pageTitle.setText("Modifica " + album.getName());
            albumName.setText(album.getName());
            datePicker.setValue(album.getReleaseDate());
            addedSongsList.setItems(FXCollections.observableArrayList(album.getSongs()));
            genreChoice.getSelectionModel().select(pair.getKey().getGenre().getId());
            ;
            try {
                cover = albumService.fetchCover(album);
                tempAvatar.setImage(new Image(new ByteArrayInputStream(cover)));
            } catch (BusinessException e) {
                e.printStackTrace();
            }
            saveAlbumBtn.setDisable(false);
        } else {

            album = new Album();
            album.setArtist(pair.getValue());
            pageTitle.setText("Crea un nuovo album di: " + pair.getValue().getStageName());

        }
    }

    // metodo per tornare alla schermata precedente
    public void goBack() {
        dispatcher.goBack();
    }

    // metodo chiamato quando si modifica il nome dell'album
    @FXML
    public void editAlbumName(KeyEvent event) {
        // saveButton viene attivato solo quando il nome dell'album non è vuoto
        // e l'album contiene almeno una canzone
        if (!album.getSongs().isEmpty() && !albumName.getText().equals(""))
            saveAlbumBtn.setDisable(false);
        else
            saveAlbumBtn.setDisable(true);

    }

    // metodo per caricare un file
    @FXML
    public void browseFiles(ActionEvent events) {
        // apro l'esplora risorse e caricare il file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Scegli immagine");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        newCoverFile = dispatcher.uploadFile(fileChooser);

        // carico la nuova immagine nella "preview" all'interno della vista per la
        // gestione dell'avatar
        if (newCoverFile != null) {
            try {
                cover = Files.readAllBytes(newCoverFile.toPath());
                tempAvatar.setImage(new Image(new ByteArrayInputStream(cover)));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // metodo per cercare una canzone
    @FXML
    public void search(KeyEvent event) {
        String text = searchBar.getText();
        List<Song> songs;
        try {
            // choice contiene il filtro della ricerca scelto dall'utente tra titolo e
            // genere
            if (choice.getSelectionModel().getSelectedIndex() == 0)
                songs = musicService.findAllMusicByPrefix(text);
            else
                songs = musicService.findAllMusicByGenrePrefix(text);

            if (text.isEmpty()) {
                searchList.setVisible(false);
                searchList.getItems().clear();
            } else {
                searchList.setItems(FXCollections.observableArrayList(songs));
                searchList.setVisible(true);
                // in questo modo l'altezza della lista è sempre pari alla somma di quelle degli
                // elementi che la compongono
                searchList.maxHeightProperty()
                        .bind(Bindings.size(FXCollections.observableArrayList(songs)).multiply(LIST_ROW_HEIGHT));
            }

        } catch (BusinessException e) {
            e.printStackTrace();
        }

    }

    // metodo per aggiungere la canzone cercata all'album
    @FXML
    public void addSongToAlbum(ActionEvent event) {
        for (Song s : addedSongsList.getItems()) {
            if (s.equals(searchList.getSelectionModel().getSelectedItem())) {
                return;
            }
        }
        Song selectedSong = searchList.getSelectionModel().getSelectedItem();
        if (!selectedSong.getArtists().contains(album.getArtist()))
            selectedSong.getArtists().add(album.getArtist());
        album.getSongs().add(selectedSong);
        try {
            // aggiunta dell'artista sul fileF
            musicService.editSong(selectedSong, null, null, null);
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        addedSongsList.setItems(FXCollections.observableArrayList(album.getSongs()));
        if (!albumName.getText().isEmpty())
            saveAlbumBtn.setDisable(false);

    }

    // metodo chiamato quando si clicca su una canzone
    @FXML
    public void selectAddedSong(MouseEvent event) {
        removeSong.setDisable(false);
    }

    // metodo per salvare le modifiche
    @FXML
    public void saveAlbum(ActionEvent event) {

        album.setName(albumName.getText());

        album.setReleaseDate(datePicker.getValue());
        album.setArtist(album.getArtist());
        album.setSongs(album.getSongs());
        try {
            album.setGenre(genreService.findGenreById(genreChoice.getSelectionModel().getSelectedIndex()));
            if (isAlbumNew) {
                albumService.createAlbum(album, cover);
            } else {
                albumService.editAlbum(album, cover);
            }
            goBack();
        } catch (BusinessException e) {

            e.printStackTrace();
        }

    }

    // metodo per rimuovere una canzone dall'album
    @FXML
    public void removeSong(ActionEvent event) {
        album.getSongs().remove(addedSongsList.getSelectionModel().getSelectedItem());
        addedSongsList.setItems(FXCollections.observableArrayList(album.getSongs()));
        removeSong.setDisable(true);
        if (album.getSongs().size() == 0) {
            saveAlbumBtn.setDisable(true);
        }
    }

    // metodo chiamato quando si clicca su un elemento della lista
    @FXML
    public void listItemClick(MouseEvent event) {
        searchList.setVisible(false);
        String artistsString = new String();
        for (Artist a : searchList.getSelectionModel().getSelectedItem().getArtists()) {
            artistsString = artistsString + " - " + a.getStageName();
        }
        searchBar.setText(searchList.getSelectionModel().getSelectedItem().getTitle() + artistsString);
        // rendo il tasto per aggiungere la canzone attivo solo quando la canzone è
        // effettivamente selezionata
        addSongButton.setDisable(false);
    }

    // metodo per l'eliminazione dell'album
    @FXML
    public void deleteAlbum(ActionEvent event) {
        if (dispatcher.confirmAlert("Vuoi davvero eliminare questo artista?")) {
            try {
                albumService.deleteAlbum(album);
            } catch (BusinessException e) {
                e.printStackTrace();
            }
            goBack();
        }
    }

}
