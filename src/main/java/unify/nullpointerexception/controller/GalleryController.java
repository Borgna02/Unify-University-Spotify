package unify.nullpointerexception.controller;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import unify.nullpointerexception.domain.ArtistPicture;
import unify.nullpointerexception.view.ViewDispatcher;

public class GalleryController implements Initializable, DataInitializable<ArtistPicture> {

    private ViewDispatcher dispatcher;
    @FXML
    public Text caption;
    @FXML
    public ImageView image;

    // costruttore
    public GalleryController() {
        dispatcher = ViewDispatcher.getInstance();
    }

    // inizializzo il controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    // inizializzo i dati del controller
    @Override
    public void initializeData(ArtistPicture picture) {
        caption.setText(picture.getCaption());
        image.setImage(new Image(new ByteArrayInputStream(picture.getData())));
    }

    // metodo per tornare alla schermata precedente
    @FXML
    public void goBack(MouseEvent event) {
        dispatcher.goBack();
    }
}
