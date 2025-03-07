package unify.nullpointerexception.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import unify.nullpointerexception.business.AlbumService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Album;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.Song;
import unify.nullpointerexception.view.ViewDispatcher;

public class AdminSongsController implements Initializable, DataInitializable<Artist> {

    private MusicService musicService;
    private AlbumService albumService;

    private ViewDispatcher dispatcher;

    private List<Song> songs;

    private final String IMAGES_PATH = "src/main/resources/img/";

    @FXML
    private ChoiceBox<String> choice;

    @FXML
    private Button createSong;

    @FXML
    private TableView<Song> table;

    @FXML
    private TableColumn<Song, String> idColumn;

    @FXML
    private TableColumn<Song, String> titleColumn;

    @FXML
    private TableColumn<Song, String> artistColumn;

    @FXML
    private TableColumn<Song, String> genreColumn;

    @FXML
    private TableColumn<Song, String> yearColumn;

    @FXML
    private TableColumn<Song, Button> editColumn;

    @FXML
    private TableColumn<Song, Button> deleteColumn;

    @FXML
    private Text pageTitle;

    @FXML
    private TextField searchBar;

    @FXML
    private ListView<Artist> list;

    // inizializzo il controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dispatcher = ViewDispatcher.getInstance();

        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
        musicService = factory.getMusicService();
        albumService = factory.getAlbumService();
        // todo rimuovere filtro artista quando artista != null
        choice.setItems(FXCollections.observableArrayList("Titolo", "Artista", "Genere"));
        choice.getSelectionModel().selectFirst();

    }

    // inizializzo i dati del controller
    @Override
    public void initializeData(Artist artist) {

        try {
            if (artist == null)
                songs = musicService.getAllSongs();
            else {
                songs = musicService.findAllMusicByArtist(artist);
                pageTitle.setText("Elenco delle canzoni di " + artist.getStageName());
            }

        } catch (BusinessException e) {
            e.printStackTrace();
            ;
        }

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        artistColumn.setCellValueFactory((CellDataFeatures<Song, String> param) -> {
            String artistList = new String();
            int maxArtistsList = 3;
            for (Artist a : param.getValue().getArtists()) {
                if (maxArtistsList > 0) {
                    artistList = artistList + a.getStageName() + "\n";
                    maxArtistsList--;
                } else {
                    artistList = artistList + "...\n";
                }

            }

            return new SimpleObjectProperty<String>(artistList);
        });

        genreColumn.setCellValueFactory((CellDataFeatures<Song, String> param) -> {
            return new SimpleObjectProperty<String>(param.getValue().getGenre().getName());
        });

        yearColumn.setCellValueFactory((CellDataFeatures<Song, String> param) -> {

            Song song = param.getValue();
            return new SimpleObjectProperty<String>(Integer.toString(song.getReleaseDate().getYear()));
        });

        editColumn.setStyle("-fx-alignment: CENTER;");
        editColumn.setCellValueFactory((CellDataFeatures<Song, Button> param) -> {
            final Button editBtn = new Button();
            try {
                ImageView editIcon = new ImageView(
                        new Image(new FileInputStream(new File(IMAGES_PATH + "whiteEdit.png"))));
                editIcon.setFitWidth(20);
                editIcon.setFitHeight(20);
                editBtn.setGraphic(editIcon);
                editBtn.setStyle("-fx-background-color: #25408F");
                editBtn.setCursor(Cursor.HAND);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            editBtn.setOnAction((ActionEvent event) -> {
                try {
                    dispatcher.renderView("createOrEditSong", param.getValue());
                } catch (IOException | BusinessException e) {
                    e.printStackTrace();
                }
            });
            return new SimpleObjectProperty<Button>(editBtn);

        });

        deleteColumn.setStyle("-fx-alignment: CENTER;");
        deleteColumn.setCellValueFactory((CellDataFeatures<Song, Button> param) -> {

            final Button deleteBtn = new Button();
            ImageView deleteIcon;
            try {
                deleteIcon = new ImageView(
                        new Image(new FileInputStream(new File(IMAGES_PATH + "whiteTrashBin.png"))));
                deleteIcon.setFitWidth(12);
                deleteIcon.setFitHeight(20);
                deleteBtn.setGraphic(deleteIcon);
                deleteBtn.setStyle("-fx-background-color: #25408F");
                deleteBtn.setCursor(Cursor.HAND);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            deleteBtn.setOnAction((ActionEvent event) -> {
                try {
                    if (dispatcher.confirmAlert("Vuoi davvero eliminare questa canzone?")) {
                        List<Album> albums = albumService.findAllAlbumBySong(param.getValue());

                        for (Album album : albums) {

                            Song songToRemove = null;
                            for (Song tempSong : album.getSongs()) {
                                if (tempSong.equals(param.getValue())) {
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
                        musicService.deleteSong(param.getValue());
                        table.getItems().clear();
                        table.getItems().addAll(musicService.getAllSongs());
                    }
                } catch (BusinessException e) {
                    e.printStackTrace();
                }

            });
            return new SimpleObjectProperty<Button>(deleteBtn);
        });

        table.getItems().addAll(songs);

    }

    // metodo per la ricerca delle canzoni per titolo, genere e artista
    @FXML
    public void search(KeyEvent event) {

        table.getItems().clear();
        String text = searchBar.getText().toLowerCase();

        if (text.isEmpty()) {
            table.getItems().addAll(songs);
        } else {
            if (choice.getSelectionModel().getSelectedIndex() == 0) { // ricerca per titolo

                for (Song song : songs) {
                    if (song.getTitle().toLowerCase().contains(text))
                        table.getItems().add(song);
                }

            } else if (choice.getSelectionModel().getSelectedIndex() == 1) { // ricerca per artista

                for (Song song : songs) {

                    for (Artist artist : song.getArtists()) {

                        if (artist.getStageName().toLowerCase().contains(text)) {
                            table.getItems().add(song);
                            break;
                        }

                    }

                }

            } else if (choice.getSelectionModel().getSelectedIndex() == 2) { // ricerca per genere

                for (Song song : songs) {
                    if (song.getGenre().getName().toLowerCase().contains(text))
                        table.getItems().add(song);
                }

            }
        }

    }

    // metodo chiamato quando si clicca sul tasto per creare una canzone
    @FXML
    public void createSong(ActionEvent event) {
        try {
            dispatcher.renderView("createOrEditSong", null);

        } catch (IOException | BusinessException e) {

            e.printStackTrace();
        }
    }

    // metodo per tornare alla schermata precedente
    @FXML
    public void goBack(ActionEvent event) {
        dispatcher.goBack();
    }
}
