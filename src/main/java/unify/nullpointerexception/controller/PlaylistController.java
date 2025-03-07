package unify.nullpointerexception.controller;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.business.PlaylistService;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Playlist;
import unify.nullpointerexception.domain.Song;
import unify.nullpointerexception.view.ViewDispatcher;

public class PlaylistController implements Initializable, DataInitializable<Playlist> {

    private ViewDispatcher dispatcher;
    private MusicService musicService;
    private PlaylistService playlistService;
    @FXML
    private Text playlistName;

    @FXML
    private FlowPane songsBox;

    private Playlist playlist;

    // costruttore
    public PlaylistController() {
        dispatcher = ViewDispatcher.getInstance();
        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();

        musicService = factory.getMusicService();
        playlistService = factory.getPlaylistService();
    }

    // inizializzo il controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    // inizializzo i dati del controller
    @Override
    public void initializeData(Playlist playlist) throws FileNotFoundException {
        this.playlist = playlist;
        this.playlistName.setText(playlist.getName());

        Integer counter = 0;
        // costruzione dinamica della vista all'interno della playlist
        for (Song song : playlist.getSongs()) {

            Text title = new Text(song.getTitle());

            ImageView coverView = new ImageView();
            try {
                byte[] cover = musicService.fetchCover(song);
                coverView.setImage(new Image(new ByteArrayInputStream(cover)));

            } catch (BusinessException e) {

                e.printStackTrace();
            }

            coverView.setFitHeight(90);
            coverView.setFitWidth(90);
            title.setFont(Font.font("System", 14));
            title.setTextAlignment(TextAlignment.CENTER);

            VBox box = new VBox(coverView, title);

            box.setId(counter.toString());
            box.setAlignment(Pos.CENTER);
            box.setCursor(Cursor.HAND);
            box.setOnMouseClicked(event -> {
                String id = ((VBox) event.getSource()).getId();

                try {
                    dispatcher.renderView("song", playlist.getSongs().get(Integer.parseInt(id)));
                } catch (IOException | NumberFormatException e) {
                    e.printStackTrace();
                } catch (BusinessException e) {
                    e.printStackTrace();
                }

            });

            songsBox.getChildren().add(box);

            counter++;
        }
    }

    // metodo chiamato quando si preme il tasto di play/pausa
    @FXML
    public void playButtonPressed(ActionEvent event) {

        dispatcher.loadQueue(playlist.getSongs(), -1);
    }

    // metodo per aggiungere la playlist alla coda
    @FXML
    public void addToQueue(ActionEvent event) {

        dispatcher.addToQueue(playlist.getSongs());

    }

    // metodo per modificare la playlist
    @FXML
    public void editPlaylist(ActionEvent event) {
        try {
            dispatcher.renderView("createOrEditPlaylist", playlist);
        } catch (IOException | BusinessException e) {
            e.printStackTrace();
        }
    }

    // metodo per eliminare la playlist
    @FXML
    public void deletePlaylist(ActionEvent event) {
        try {
            if (dispatcher.confirmAlert("Vuoi davvero eliminare questa playlist?")) {
                playlistService.deletePlaylist(playlist);
                goBack();
            }
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
