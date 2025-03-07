package unify.nullpointerexception.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.Song;
import unify.nullpointerexception.view.ViewDispatcher;

public class SongController implements Initializable, DataInitializable<Song> {

    private Song song;
    private MusicService musicService;
    
    private ViewDispatcher dispatcher;

    @FXML
    private Text title;
    @FXML
    private FlowPane artistsContainer;
    @FXML
    private Text date;
    @FXML
    private ImageView coverArt;
    @FXML
    private Text lyrics;

    public SongController() {
        dispatcher = ViewDispatcher.getInstance();
        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
        musicService = factory.getMusicService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public void initializeData(Song song) {


        this.song = song;
       
        title.setText(song.getTitle());

        
        ArrayList<Text> texts = new ArrayList<>();
        for (Artist artist : song.getArtists()) {
            Text t = new Text(artist.getStageName());
            t.setFont(Font.font(20));
            t.setCursor(Cursor.HAND);
            t.styleProperty().bind( Bindings.when(t.hoverProperty()).then("-fx-underline: true").otherwise("-fx-underline: false"));
            t.setOnMouseClicked(event->{
                try {
                    dispatcher.renderView("artist", artist);
                } catch (BusinessException | IOException e) {
                    e.printStackTrace();
                }
            });
            texts.add(t);

            Text comma = new Text(", "); //rimuovo la virgola dal testo non sottolineato
            comma.setFont(Font.font(20));
            texts.add(comma);
        }

        texts.get(texts.size()-1).setText("");  //rimuovo la ", " dall'ultimo artista

        artistsContainer.getChildren().addAll(texts);
        date.setText(Integer.toString( song.getReleaseDate().getYear()));

		try {
            lyrics.setText(musicService.fetchLyrics(song));

			byte[] cover = musicService.fetchCover(song);
            coverArt.setImage(new Image(new ByteArrayInputStream(cover)));

		} catch (BusinessException e) {
			e.printStackTrace();
		}

        
    }

    @FXML
    public void playButtonPressed(ActionEvent event) {
        dispatcher.loadQueue(song, -1);
    }

    @FXML
    public void goBack() {
        dispatcher.goBack();
    }

    @FXML
    public void addToQueue(ActionEvent event) {
        dispatcher.addToQueue(song);
    }

}
