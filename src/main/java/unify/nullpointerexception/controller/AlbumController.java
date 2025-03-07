package unify.nullpointerexception.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import unify.nullpointerexception.business.AlbumService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Album;
import unify.nullpointerexception.domain.Song;
import unify.nullpointerexception.view.ViewDispatcher;

public class AlbumController implements Initializable, DataInitializable<Album> {

    private ViewDispatcher dispatcher;
    private Album album;

    @FXML
    private Text title, author, date;
    @FXML
    private ImageView coverArt;
    @FXML
    private VBox songsBox;

    private MusicService musicService;
    private AlbumService albumService;

    // costruttore
    public AlbumController() {
        dispatcher = ViewDispatcher.getInstance();
        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
        musicService = factory.getMusicService();
        albumService = factory.getAlbumService();
    }

    // inizializzo il controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        author.styleProperty().bind(
                Bindings.when(author.hoverProperty()).then("-fx-underline: true").otherwise("-fx-underline: false"));
    }

    // inizializzo i dati del controller
    @Override
    public void initializeData(Album album) {

        this.album = album;

        title.setText(album.getName());

        author.setText(album.getArtist().getStageName());

        date.setText(Integer.toString(album.getReleaseDate().getYear()));

        try {
            byte[] cover = albumService.fetchCover(album);
            coverArt.setImage(new Image(new ByteArrayInputStream(cover)));

        } catch (BusinessException e) {
            e.printStackTrace();
        }

        for (Song song : album.getSongs()) {

            Text title = new Text(song.getTitle());

            Text year = new Text(Integer.toString(song.getReleaseDate().getYear()));

            ImageView coverView = new ImageView();
            try {
                byte[] cover = musicService.fetchCover(song);
                coverView.setImage(new Image(new ByteArrayInputStream(cover)));

            } catch (BusinessException e) {

                e.printStackTrace();
            }

            coverView.setFitHeight(120);
            coverView.setFitWidth(120);
            title.setFont(Font.font("System", 16));
            year.setFont(Font.font("System", 12));

            VBox textBox = new VBox(title, year);
            textBox.setSpacing(10);
            textBox.setAlignment(Pos.CENTER_LEFT);
            textBox.setPadding(new Insets(0, 0, 0, 5));

            HBox box = new HBox(coverView, textBox);

            box.setCursor(Cursor.HAND);
            box.setOnMouseClicked(event -> {

                try {
                    dispatcher.renderView("song", song);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BusinessException e) {
                    e.printStackTrace();
                }

            });

            songsBox.getChildren().add(box);

        }

    }

    // metodo per tornare alla schermata precedente
    @FXML
    public void goBack() {
        dispatcher.goBack();
    }

    // metodo chiamato quando si preme sul tasto play che riproduce l'album
    @FXML
    public void playButtonPressed(ActionEvent event) {

        dispatcher.loadQueue(album.getSongs(), -1);
    }

    // metodo che aggiunge l'album alla coda
    @FXML
    public void addToQueue(ActionEvent event) {

        dispatcher.addToQueue(album.getSongs());
    }

    // metodo per andare alla pagina dell'artista
    @FXML
    public void goToArtistPage(MouseEvent event) throws BusinessException {
        try {
            dispatcher.renderView("artist", album.getArtist());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}