package unify.nullpointerexception.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import unify.nullpointerexception.business.AlbumService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Album;
import unify.nullpointerexception.domain.Genre;
import unify.nullpointerexception.domain.Song;
import unify.nullpointerexception.view.ViewDispatcher;

public class GenreController implements Initializable, DataInitializable<Genre> {

    private ViewDispatcher dispatcher;
    private MusicService musicService;
    private AlbumService albumService;

    private List<Song> actualGenreSongs;
    private List<Album> actualGenreAlbums;

    @FXML
    private Text genreName;

    @FXML
    private HBox singlesBox;

    @FXML
    private HBox albumsBox;

    // inizializza il controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dispatcher = ViewDispatcher.getInstance();

        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
        musicService = factory.getMusicService();
        albumService = factory.getAlbumService();
    }

    // inizializza i dati del controller
    @Override
    public void initializeData(Genre genre) {
        genreName.setText(genre.getName());

        // aggiunta singoli

        try {

            actualGenreSongs = musicService.findAllMusicByGenreName(genre.getName());
            createSongsBtns(actualGenreSongs);

            actualGenreAlbums = albumService.findAllAlbumByGenrePrefix(genre.getName());
            createAlbumsBtns(actualGenreAlbums);

        } catch (BusinessException e) {
            e.printStackTrace();
        }

    }

    // metodo chiamato quando premo play sui singoli
    @FXML
    public void singlesPlayButtonPressed(ActionEvent event) {

        dispatcher.loadQueue(actualGenreSongs, -1);
    }

    // metodo che aggiunge i singoli del genere alla coda
    @FXML
    public void addSinglesToQueue(ActionEvent event) {

        dispatcher.addToQueue(actualGenreSongs);

    }

    // metodo chiamato quando premo play sugli album
    @FXML
    public void albumPlayButtonPressed(ActionEvent event) {

        dispatcher.loadQueue(getSongsFromAlbums(actualGenreAlbums), -1);
    }

    // metodo che aggiunge gli album del genere alla coda
    @FXML
    public void addAlbumToQueue(ActionEvent event) {

        for (Album album : actualGenreAlbums)
            dispatcher.addToQueue(album.getSongs());
    }

    // metodo per tornare alla schermata precedente
    @FXML
    public void goBack() {
        dispatcher.goBack();

    }

    // metodo per estrarre le canzoni dagli album
    private List<Song> getSongsFromAlbums(List<Album> albums) {
        List<Song> result = new ArrayList<Song>();
        for (Album album : albums)
            result.addAll(album.getSongs());

        return result;
    }

    // metodo per creare i button delle canzoni
    private void createSongsBtns(List<Song> songs) {
        for (Song song : songs) {

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

            box.setAlignment(Pos.CENTER);
            box.setCursor(Cursor.HAND);
            box.setOnMouseClicked(event -> {
                try {
                    dispatcher.renderView("song", song);
                } catch (IOException | NumberFormatException e) {
                    e.printStackTrace();
                } catch (BusinessException e) {
                    e.printStackTrace();
                }

            });

            singlesBox.getChildren().add(box);

        }
    }

    // metodo per creare i button degli album
    private void createAlbumsBtns(List<Album> albums) {
        for (Album album : albums) {

            Text title = new Text(album.getName());
            ImageView coverView = new ImageView();
            try {
                byte[] cover = albumService.fetchCover(album);
                coverView.setImage(new Image(new ByteArrayInputStream(cover)));

            } catch (BusinessException e) {

                e.printStackTrace();
            }

            coverView.setFitHeight(90);
            coverView.setFitWidth(90);
            title.setFont(Font.font("System", 14));
            title.setTextAlignment(TextAlignment.CENTER);

            VBox box = new VBox(coverView, title);

            box.setId(Integer.toString(albums.indexOf(album)));
            box.setAlignment(Pos.CENTER);
            box.setCursor(Cursor.HAND);
            box.setOnMouseClicked(event -> {

                try {
                    dispatcher.renderView("album", album);
                } catch (IOException | NumberFormatException e) {
                    e.printStackTrace();
                } catch (BusinessException e) {
                    e.printStackTrace();
                }

            });

            albumsBox.getChildren().add(box);

        }
    }
}
