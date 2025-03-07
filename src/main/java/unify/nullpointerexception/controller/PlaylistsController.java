package unify.nullpointerexception.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.PlaylistService;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Playlist;
import unify.nullpointerexception.domain.User;
import unify.nullpointerexception.view.ViewDispatcher;

public class PlaylistsController implements Initializable, DataInitializable<User> {

    private ViewDispatcher dispatcher;
    private PlaylistService playlistService;
    private User user;

    private List<Playlist> userPlaylists = new ArrayList<Playlist>();

    @FXML
    private FlowPane playlistFlowpane;

    @FXML
    private VBox createPlaylistPage;

    @FXML
    private Button createPlaylistBtn;

    // costruttore
    public PlaylistsController() {
        dispatcher = ViewDispatcher.getInstance();
        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
        playlistService = factory.getPlaylistService();
    }

    // inizializzo il controller
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    // inizializzo i dati del controller
    @Override
    public void initializeData(User user) throws IOException, BusinessException {
        this.user = user;
        this.userPlaylists = playlistService.findAllPlaylistsByUser(user);

        showPlaylists();
    }

    // metodo che carica un Button per ogni playlist oltre a quello per crearne una
    public void showPlaylists() {
        playlistFlowpane.getChildren().clear();
        playlistFlowpane.getChildren().add(createPlaylistBtn);

        for (Playlist playlist : userPlaylists) {
            playlistFlowpane.getChildren().add(createPlaylistBtn(playlist));
        }
    }

    // metodo che crea un Button
    public Button createPlaylistBtn(Playlist playlist) {
        Button newPlaylistBtn = new Button(playlist.getName());

        // impostazioni grafiche del Button
        newPlaylistBtn.setPrefWidth(80);
        newPlaylistBtn.setPrefHeight(80);
        FlowPane.setMargin((Node) newPlaylistBtn, new Insets(2.5));
        newPlaylistBtn.setCursor(Cursor.HAND);

        // metodo chiamato dal Button
        newPlaylistBtn.setOnAction(event -> {
            try {
                dispatcher.renderView("playlist", playlist);
            } catch (IOException | BusinessException e) {
                e.printStackTrace();
            }
        });

        return newPlaylistBtn;
    }

    // metodo che carica la vista per creare una nuova playlist
    @FXML
    public void createPlaylist(ActionEvent event) {
        Playlist newPlaylist = new Playlist();
        newPlaylist.setCreator(user);
        try {
            dispatcher.renderView("createOrEditPlaylist", newPlaylist);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }

}
