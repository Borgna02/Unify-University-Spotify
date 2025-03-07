package unify.nullpointerexception.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import unify.nullpointerexception.business.AlbumService;
import unify.nullpointerexception.business.ArtistPicturesService;
import unify.nullpointerexception.business.ArtistsService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Album;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.ArtistPicture;
import unify.nullpointerexception.domain.Song;
import unify.nullpointerexception.view.ViewDispatcher;

public class ArtistController implements Initializable, DataInitializable<Artist> {

    private MusicService musicService;
    private AlbumService albumService;
    private ArtistsService artistsService;
    private ArtistPicturesService artistPicturesService;

    private List<Song> songs;
    private List<Album> albumList;
    private List<ArtistPicture> pictures;

    @FXML
    private ImageView artistMainPicture;
    @FXML
    private Text name;
    @FXML
    private HBox discoBox;
    @FXML
    private HBox albumBox;
    @FXML
    private HBox picturesBox;
    @FXML
    private Text biographyBox;

    private ViewDispatcher dispatcher;

    // costruttore
    public ArtistController() {
        dispatcher = ViewDispatcher.getInstance();
        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();

        musicService = factory.getMusicService();
        albumService = factory.getAlbumService();
        artistsService = factory.getArtistsService();
        artistPicturesService = factory.getArtistPicturesService();
    }

    // inizializzo il controller
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    // inizializzo i dati del controller
    @Override
    public void initializeData(Artist artist) {

        name.setText(artist.getStageName());

        try {
            byte[] avatar = artistsService.fetchAvatar(artist);
            artistMainPicture.setImage(new Image(new ByteArrayInputStream(avatar)));
        } catch (BusinessException e) {
            e.printStackTrace();
        }

        try {
            songs = musicService.findAllMusicByArtist(artist); // carico la discografia

            for (Song song : songs) {

                Text title = new Text(song.getTitle());

                ImageView coverView = new ImageView();
                try {
                    byte[] cover = musicService.fetchCover(song);
                    coverView.setImage(new Image(new ByteArrayInputStream(cover)));

                } catch (BusinessException e) {

                    e.printStackTrace();
                }

                coverView.setFitHeight(120);
                coverView.setFitWidth(120);
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

                discoBox.getChildren().add(box);

            }
        } catch (BusinessException e) {

            e.printStackTrace();
        }

        try {
            albumList = albumService.findAllAlbumByArtist(artist); // carico gli album

            for (Album album : albumList) {

                Text title = new Text(album.getName());
                ImageView coverView = new ImageView();
                try {
                    byte[] cover = albumService.fetchCover(album);
                    coverView.setImage(new Image(new ByteArrayInputStream(cover)));

                } catch (BusinessException e) {

                    e.printStackTrace();
                }

                coverView.setFitHeight(120);
                coverView.setFitWidth(120);
                title.setFont(Font.font("System", 14));
                title.setTextAlignment(TextAlignment.CENTER);

                VBox box = new VBox(coverView, title);

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

                albumBox.getChildren().add(box);

            }

        } catch (BusinessException e) {
            e.printStackTrace();
        }

        try {
            pictures = artistPicturesService.findAllPicturesByArtist(artist); // carico le foto ufficiali

            for (ArtistPicture picture : pictures) {

                ImageView image = new ImageView(new Image(new ByteArrayInputStream(picture.getData())));

                image.setFitHeight(160);
                image.setFitWidth(240);
                image.setCursor(Cursor.HAND);
                image.setOnMouseClicked(event -> {

                    try {
                        dispatcher.renderView("gallery", picture);
                    } catch (IOException | NumberFormatException e) {
                        e.printStackTrace();
                    } catch (BusinessException e) {
                        e.printStackTrace();
                    }
                });

                Tooltip text = new Tooltip(picture.getCaption());
                text.setShowDelay(Duration.seconds(0.3));
                Tooltip.install(image, text);

                picturesBox.getChildren().add(image);

            }

        } catch (BusinessException e) {
            e.printStackTrace();
        }

        biographyBox.setText(artistsService.getBiography(artist));
    }

    // metodo per tornare alla schermata precedente
    @FXML
    public void goBack() {
        dispatcher.goBack();
    }
}
