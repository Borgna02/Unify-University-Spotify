package unify.nullpointerexception.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import unify.nullpointerexception.business.ArtistsService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.view.ViewDispatcher;

public class CreateOrEditArtistController implements Initializable, DataInitializable<Artist> {

    private ViewDispatcher dispatcher;

    private File newAvatarFile;
    private byte[] newAvatar;

    @FXML
    private Text nameAction;

    @FXML
    private ImageView tempAvatar;

    @FXML
    private TextField stageNameField;

    @FXML
    private HBox dataErrorMsg;

    @FXML
    private Text dataErrorTxt;

    @FXML
    private Button saveBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button editAlbumsBtn;

    @FXML
    private Button editSongsBtn;

    @FXML
    private TextArea biographyBox;

    @FXML
    private Button editPicturesBtn;

    private Artist artist;
    private Boolean isNew;
    private String oldBio;

    private ArtistsService artistsService;

    // costruttore
    public CreateOrEditArtistController() {
        dispatcher = ViewDispatcher.getInstance();
        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();

        artistsService = factory.getArtistsService();
    }

    // inizializzo il controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    // inizializzo i dati del controller
    @Override
    public void initializeData(Artist artist) {
        this.artist = artist;
        this.isNew = artist == null;

        if (!isNew) {

            nameAction.setText("Modifica Artista");
            stageNameField.setText(artist.getStageName());
            oldBio = artistsService.getBiography(artist);
            biographyBox.setText(oldBio);

            try {
                byte[] avatar = artistsService.fetchAvatar(artist);
                if (avatar != null)
                    tempAvatar.setImage(new Image(new ByteArrayInputStream(avatar)));
            } catch (BusinessException e) {
                e.printStackTrace();
            }

            editAlbumsBtn.setText("Modifica album");
            editSongsBtn.setText("Modifica canzoni");
            editPicturesBtn.setText("Modifica foto");

        } else {
            nameAction.setText("Crea Artista");
            deleteBtn.setDisable(true);
            editAlbumsBtn.setText("Crea album");
            editAlbumsBtn.setDisable(true);
            editSongsBtn.setText("Crea canzoni");
            editSongsBtn.setDisable(true);
            editPicturesBtn.setText("Aggiungi foto");
            editPicturesBtn.setDisable(true);
        }

        fieldsComplete();
    }

    // metodo per caricare un file
    @FXML
    public void browseFiles(ActionEvent events) {
        // apro l'esplora risorse e caricare il file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Scegli immagine");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        newAvatarFile = dispatcher.uploadFile(fileChooser);

        // carico la nuova immagine nella "preview" all'interno della vista per la
        // gestione dell'avatar

        if (newAvatarFile != null) {

            try {
                newAvatar = Files.readAllBytes(newAvatarFile.toPath());
                tempAvatar.setImage(new Image(new ByteArrayInputStream(newAvatar)));
                saveBtn.setDisable(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // metodo per salvare le modifiche
    @FXML
    public void saveChanges(ActionEvent event) {

        try {
            Artist newArtist = new Artist();
            if (!isNew) {
                newArtist.setId(this.artist.getId());
            }
            newArtist.setStageName(stageNameField.getText());

            if (isNew) {
                artistsService.createArtist(newArtist, biographyBox.getText(), newAvatar);

            } else {
                artistsService.editArtist(newArtist, biographyBox.getText(), newAvatar);

            }
            dataErrorMsg.setVisible(false);
            goBack();

        } catch (BusinessException e) {
            dataErrorMsg.setVisible(true);
            dataErrorTxt.setText(e.getMessage());
        }

    }

    // metodo per mostrare gli album
    @FXML
    public void showAlbums() {
        try {
            dispatcher.renderView("adminAlbums", artist);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }

    // metodo per eliminare l'artista
    @FXML
    public void deleteArtist() {
        try {
            if (dispatcher.confirmAlert("Vuoi davvero eliminare questo artista?")) {
                try {
                    artistsService.deleteArtist(this.artist);
                    dispatcher.renderView("adminArtists", null);
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }

    // metodo per mostrare le canzoni
    @FXML
    public void showSongs() {
        try {
            dispatcher.renderView("adminSongs", artist);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }

    // metodo per mostrare le immagini ufficiali
    @FXML
    public void showPictures() {
        try {
            dispatcher.renderView("editArtistPictures", artist);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }

    // metodo chiamato quando si modifica il campo del nome d'arte
    @FXML
    public void editStageNameField(KeyEvent event) {
        fieldsComplete();
    }

    // metodo per tornare alla schermata precedente
    @FXML
    public void goBack() {
        dispatcher.goBack();
    }

    // metodo che controlla che i campi obbligatori siano pieni
    public void fieldsComplete() {
        if (!(stageNameField.getText().isEmpty())) {
            saveBtn.setDisable(false);
            return;
        }
        saveBtn.setDisable(true);
    }
}
