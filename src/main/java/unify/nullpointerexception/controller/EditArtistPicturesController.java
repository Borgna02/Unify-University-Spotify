package unify.nullpointerexception.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import unify.nullpointerexception.business.ArtistPicturesService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.ArtistPicture;
import unify.nullpointerexception.view.ViewDispatcher;

public class EditArtistPicturesController implements Initializable, DataInitializable<Artist> {

    private ViewDispatcher dispatcher;
    private ArtistPicturesService artistPicturesService;

    private List<ArtistPicture> artistPictures;

    @FXML
    private Text pageTitle;

    @FXML
    private ListView<ArtistPicture> addedPicturesList;
    @FXML
    private Button removeBtn;

    @FXML
    private ImageView imgPreview;

    private Image defaultUser;

    @FXML
    private Button addPictureBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private TextField captionField;

    private List<ArtistPicture> deletedPictures = new ArrayList<>();
    private List<ArtistPicture> addedPictures = new ArrayList<>();
    private byte[] newImageBytes;
    private Artist artist;

    // costruttore
    public EditArtistPicturesController() {
        dispatcher = ViewDispatcher.getInstance();
        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
        artistPicturesService = factory.getArtistPicturesService();
    }

    // inizializzo il controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    // inizializzo i dati del controller
    @Override
    public void initializeData(Artist artist) {
        this.artist = artist;
        try {
            this.artistPictures = artistPicturesService.findAllPicturesByArtist(artist);
            this.defaultUser = imgPreview.getImage();

            pageTitle.setText("Foto di " + artist.getStageName());
            if (!this.artistPictures.isEmpty()) {
                addedPicturesList.setItems(FXCollections.observableArrayList(this.artistPictures));

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

    // metodo per caricare un file
    @FXML
    public void browseFiles(ActionEvent event) {
        // apro l'esplora risorse e caricare il file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Scegli immagine");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File newImgFile = dispatcher.uploadFile(fileChooser);

        // carico la nuova immagine nella "preview" all'interno della vista
        if (newImgFile != null) {

            try {
                newImageBytes = Files.readAllBytes(newImgFile.toPath());
                imgPreview.setImage(new Image(new ByteArrayInputStream(newImageBytes)));
                captionField.clear();
                addPictureBtn.setDisable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // metodo per controllare le modifiche fatte
    @FXML
    public void checkChanges(KeyEvent event) {
        if (!captionField.getText().isBlank() && newImageBytes != null)
            addPictureBtn.setDisable(false);
    }

    // metodo per cancellare una foto
    @FXML
    public void removePicture(ActionEvent event) {
        deletedPictures.add(addedPicturesList.getSelectionModel().getSelectedItem());
        artistPictures.remove(addedPicturesList.getSelectionModel().getSelectedItem());
        imgPreview.setImage(defaultUser);
        captionField.clear();
        addedPicturesList.setItems(FXCollections.observableArrayList(artistPictures));
        saveBtn.setDisable(false);
    }

    // metodo per aggiungere una foto
    @FXML
    public void addPicture(ActionEvent event) {
        ArtistPicture newPicture = new ArtistPicture();
        newPicture.setCaption(captionField.getText());
        newPicture.setData(newImageBytes);
        artistPictures.add(newPicture);
        addedPictures.add(newPicture);
        addedPicturesList.setItems(FXCollections.observableArrayList(artistPictures));
        saveBtn.setDisable(false);
    }

    // metodo chiamato quando si clicca su una foto
    @FXML
    public void selectAddedPicture(MouseEvent event) {
        removeBtn.setDisable(false);
        newImageBytes = addedPicturesList.getSelectionModel().getSelectedItem().getData();
        imgPreview.setImage(
                new Image(new ByteArrayInputStream(newImageBytes)));
        captionField.setText(addedPicturesList.getSelectionModel().getSelectedItem().getCaption());
    }

    // metodo per salvare le modifiche
    @FXML
    public void save(ActionEvent event) {

        for (ArtistPicture artistPicture : deletedPictures) {
            try {
                artistPicturesService.deleteArtistPicture(artistPicture);
            } catch (BusinessException e) {

                e.printStackTrace();
            }

        }

        for (ArtistPicture artistPicture : addedPictures) {
            try {
                artistPicturesService.createArtistPicture(artist, artistPicture);
            } catch (BusinessException e) {

                e.printStackTrace();
            }
        }
        goBack();
    }
}
