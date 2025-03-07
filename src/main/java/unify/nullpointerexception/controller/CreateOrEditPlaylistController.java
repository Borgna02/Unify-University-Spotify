package unify.nullpointerexception.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.business.PlaylistService;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.Playlist;
import unify.nullpointerexception.domain.Song;
import unify.nullpointerexception.view.ViewDispatcher;

public class CreateOrEditPlaylistController implements Initializable, DataInitializable<Playlist> {

    private ViewDispatcher dispatcher;
    private PlaylistService playlistService;
    private MusicService musicService;
    private Playlist playlist;
    private Boolean isPlaylistNew;
    private final Integer LIST_ROW_HEIGHT = 24;

    @FXML
    private TextField searchBar;

    @FXML
    private ChoiceBox<String> choice;

    @FXML
    private ListView<Song> list;

    @FXML
    private Button removeSong;

    @FXML
    private TextField newPlaylistName;

    @FXML
    private ListView<Song> addedSongsList;

    @FXML
    private Button savePlaylistButton;

    @FXML
    private Button addSongButton;

    // costruttore
    public CreateOrEditPlaylistController() {
        dispatcher = ViewDispatcher.getInstance();
        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
        playlistService = factory.getPlaylistService();
        musicService = factory.getMusicService();
    }

    // inizializzo il controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        choice.setItems(FXCollections.observableArrayList("Titolo", "Genere"));
        choice.getSelectionModel().selectFirst();
    }

    // inizializzo i dati del controller
    @Override
    public void initializeData(Playlist playlist) {

        this.playlist = playlist;
        this.isPlaylistNew = (playlist.getId() == null);

        if (!isPlaylistNew) {

            newPlaylistName.setText(playlist.getName());
            addedSongsList.setItems(FXCollections.observableArrayList(playlist.getSongs()));
            savePlaylistButton.setDisable(false);

        }

    }

    // metodo per tornare alla schermata precedente
    @FXML
    public void goBack(ActionEvent event) throws IOException, BusinessException {
        dispatcher.goBack();
    }

    // metodo per la ricerca delle canzoni
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
                list.setVisible(false);
                list.getItems().clear();
            } else {
                list.setItems(FXCollections.observableArrayList(songs));
                list.setVisible(true);
                // in questo modo l'altezza della lista è sempre pari alla somma di quelle degli
                // elementi che la compongono
                list.maxHeightProperty()
                        .bind(Bindings.size(FXCollections.observableArrayList(songs)).multiply(LIST_ROW_HEIGHT));
            }
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }

    // metodo che gestisce il click di una canzone tra quelle risultate dalla
    // ricerca
    @FXML
    public void listItemClick(MouseEvent event) throws IOException, BusinessException {
        list.setVisible(false);
        String artistsString = new String();
        for (Artist a : list.getSelectionModel().getSelectedItem().getArtists()) {
            artistsString = artistsString + " - " + a.getStageName();
        }
        searchBar.setText(list.getSelectionModel().getSelectedItem().getTitle() + artistsString);
        // rendo il tasto per aggiungere la canzone attivo solo quando la canzone è
        // effettivamente selezionata
        addSongButton.setDisable(false);
    }

    // metodo che aggiunge la canzone alla playlist
    @FXML
    public void addSongToPlaylist() {
        playlist.getSongs().add(list.getSelectionModel().getSelectedItem());
        addedSongsList.setItems(FXCollections.observableArrayList(playlist.getSongs()));
        if (!newPlaylistName.getText().equals(""))
            savePlaylistButton.setDisable(false);
    }

    // metodo che attiva il tasto per rimuovere una canzone dalla playlist solo una
    // volta selezionata nella lista delle canzoni già aggiunte
    @FXML
    public void selectAddedSong(MouseEvent event) {
        removeSong.setDisable(false);
    }

    // metodo che rimuove la canzone dalla playlist se si clicca sull'apposito
    // Button
    @FXML
    public void removeSong(ActionEvent event) {
        playlist.getSongs().remove(addedSongsList.getSelectionModel().getSelectedItem());
        addedSongsList.setItems(FXCollections.observableArrayList(playlist.getSongs()));
        removeSong.setDisable(true);
        if (playlist.getSongs().size() == 0) {
            savePlaylistButton.setDisable(true);
        }
    }

    // metodo che salva la playlist quando clicco sull'apposito Button
    @FXML
    public void savePlaylist(ActionEvent event) {

        playlist.setName(newPlaylistName.getText());

        try {

            if (this.isPlaylistNew)
                playlistService.createPlaylist(playlist);
            else
                playlistService.editPlaylist(playlist);

            goBack(event);

        } catch (BusinessException | IOException e) {
            e.printStackTrace();
        }

    }

    // metodo che rileva quando viene modificato il TextField con il nome della
    // canzone e lo salva nella variabile playlistName
    @FXML
    private void setPlaylistName(KeyEvent event) {
        // saveButton viene attivato solo quando il nome della playlist non è vuoto ed è
        // stata aggiunta almeno una canzone
        if (!playlist.getSongs().isEmpty() && !newPlaylistName.getText().equals(""))
            savePlaylistButton.setDisable(false);
        else
            savePlaylistButton.setDisable(true);
    }

}
