package unify.nullpointerexception.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
import unify.nullpointerexception.business.GenreService;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Genre;
import unify.nullpointerexception.domain.User;
import unify.nullpointerexception.view.ViewDispatcher;

public class GenresController implements Initializable, DataInitializable<User> {

    private ViewDispatcher dispatcher;
    private GenreService genreService;

    @FXML
    private FlowPane genresBox;

    // inizializzo il controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dispatcher = ViewDispatcher.getInstance();

        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
        genreService = factory.getGenreService();

        try {

            for (Genre genre : genreService.getAllGenres()) {

                Text genreName = new Text(genre.getName());

                byte[] cover = genreService.fetchCover(genre);
                ImageView coverView = new ImageView(new Image(new ByteArrayInputStream(cover)));

                coverView.setFitHeight(120);
                coverView.setFitWidth(120);
                genreName.setFont(Font.font("System", 14));
                genreName.setTextAlignment(TextAlignment.CENTER);

                VBox box = new VBox(coverView, genreName);

                box.setAlignment(Pos.CENTER);
                box.setCursor(Cursor.HAND);
                box.setOnMouseClicked(event -> {

                    try {
                        dispatcher.renderView("genre", genre);
                    } catch (IOException | BusinessException e) {
                        e.printStackTrace();
                    }

                });

                genresBox.getChildren().add(box);

            }
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }
}
