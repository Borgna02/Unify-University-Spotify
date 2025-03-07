package unify.nullpointerexception.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.util.Pair;
import unify.nullpointerexception.business.AlbumService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Album;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.view.ViewDispatcher;

public class AdminAlbumsController implements Initializable, DataInitializable<Artist> {

    private ViewDispatcher dispatcher;
    private Artist artist;
    private AlbumService albumService;

    private List<Album> albums;

    private final String IMAGES_PATH = "src/main/resources/img/";

    @FXML
    private Button createAlbum;

    @FXML
    private TableView<Album> table;

    @FXML
    private TableColumn<Album, String> idColumn;

    @FXML
    private TableColumn<Album, String> titleColumn;

    @FXML
    private TableColumn<Album, String> genreColumn;

    @FXML
    private TableColumn<Album, String> yearColumn;

    @FXML
    private TableColumn<Album, Button> editColumn;

    @FXML
    private TableColumn<Album, Button> deleteColumn;

    @FXML
    private Text pageTitle;

    @FXML
    private TextField searchBar;

    // costruttore
    public AdminAlbumsController() {
        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
        albumService = factory.getAlbumService();

        dispatcher = ViewDispatcher.getInstance();
    }

    // inizializzo il controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    // inizializzo il costruttore
    @Override
    public void initializeData(Artist artist) {

        this.artist = artist;

        pageTitle.setText("Elenco degli album di " + artist.getStageName());

        try {
            albums = albumService.findAllAlbumByArtist(artist);
        } catch (BusinessException e) {
            e.printStackTrace();
        }

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        genreColumn.setCellValueFactory((CellDataFeatures<Album, String> param) -> {
            return new SimpleObjectProperty<String>(param.getValue().getGenre().getName());
        });

        yearColumn.setCellValueFactory((CellDataFeatures<Album, String> param) -> {

            Album album = param.getValue();
            return new SimpleObjectProperty<String>(Integer.toString(album.getReleaseDate().getYear()));
        });

        editColumn.setStyle("-fx-alignment: CENTER;");
        editColumn.setCellValueFactory((CellDataFeatures<Album, Button> param) -> {
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
                    dispatcher.renderView("createOrEditAlbum", new Pair<Album, Artist>(param.getValue(), artist));
                } catch (IOException | BusinessException e) {
                    e.printStackTrace();
                }
            });
            return new SimpleObjectProperty<Button>(editBtn);

        });

        deleteColumn.setStyle("-fx-alignment: CENTER;");
        deleteColumn.setCellValueFactory((CellDataFeatures<Album, Button> param) -> {

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
                        albumService.deleteAlbum(param.getValue());
                        albums.remove(param.getValue());
                        table.getItems().clear();
                        table.getItems().addAll(albums);
                    }
                } catch (BusinessException e) {
                    e.printStackTrace();
                }

            });
            return new SimpleObjectProperty<Button>(deleteBtn);
        });

        table.getItems().addAll(albums);

    }

    // metodo per la ricerca degli album
    @FXML
    public void search(KeyEvent event) {

        table.getItems().clear();
        String text = searchBar.getText().toLowerCase();

        if (text.isEmpty()) {
            table.getItems().addAll(albums);
        } else {
            for (Album album : albums) {
                if (album.getName().toLowerCase().contains(text))
                    table.getItems().add(album);
            }

        }

    }

    // metodo chiamato quando si clicca sul tasto per creare un nuovo album
    @FXML
    public void createAlbum() {

        try {
            dispatcher.renderView("createOrEditAlbum", new Pair<Album, Artist>(null, artist));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }

    // metodo per tornare alla schermata precedente
    @FXML
    public void goBack() {
        dispatcher.goBack();
    }
}
