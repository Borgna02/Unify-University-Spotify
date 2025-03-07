package unify.nullpointerexception.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import unify.nullpointerexception.business.UserService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.User;
import unify.nullpointerexception.view.ViewDispatcher;

public class CreateOrEditUserController implements Initializable, DataInitializable<User> {

    private User user;
    private boolean isNew;
    private ViewDispatcher dispatcher;
    private byte[] newAvatar;

    @FXML
    private Text title;

    @FXML
    private Text passwordText;

    @FXML
    private Text confirmPasswordText;

    @FXML
    private ImageView tempAvatar;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField confirmPasswordField;

    @FXML
    private CheckBox adminCheck;

    @FXML
    private HBox errorBox;

    @FXML
    private Text errorTxt;

    @FXML
    private Button deleteBtn;

    private UserService utenteService;

    public CreateOrEditUserController() {
        dispatcher = ViewDispatcher.getInstance();
    }

    // inizializzo il costruttore
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
        utenteService = factory.getUserService();
    }

    // inizializzo i dati del costruttore
    @Override
    public void initializeData(User user) {
        isNew = (user == null);

        if (!isNew) {

            this.user = user;

            title.setText("Modifica utente");
            passwordText.setText("Nuova password");
            confirmPasswordText.setText("Conferma nuova password");

            usernameField.setText(user.getUsername());
            emailField.setText(user.getEmail());
            passwordField.setText(user.getPassword());
            confirmPasswordField.setText(user.getPassword());
            adminCheck.setSelected(user.isAdmin());

            try {

                newAvatar = utenteService.fetchAvatar(user);
                tempAvatar.setImage(new Image(new ByteArrayInputStream(newAvatar)));

            } catch (BusinessException e) {
                e.printStackTrace();
            }

        } else {

            this.user = new User();
            deleteBtn.setDisable(true);
            deleteBtn.setVisible(false);
        }
    }

    // metodo per caricare un file
    @FXML
    public void browseFiles(ActionEvent events) {
        // apro l'esplora risorse e caricare il file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Scegli immagine");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File newAvatarFile = dispatcher.uploadFile(fileChooser);

        // carico la nuova immagine nella "preview" all'interno della vista per la
        // gestione dell'avatar
        if (newAvatarFile != null) {

            try {
                newAvatar = Files.readAllBytes(newAvatarFile.toPath());
                tempAvatar.setImage(new Image(new ByteArrayInputStream(newAvatar)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // metodo per salvare le modifiche
    @FXML
    public void saveChanges(ActionEvent event) {

        Pattern regexPasswordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$");

        Pattern regexEmailPattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$");

        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        try {

            if (!regexEmailPattern.matcher(email).matches())
                throw new InvalidDataException("email non valida");

            if (!regexPasswordPattern.matcher(password).matches())
                throw new InvalidDataException("password non valida");

            if (isNew && !password.equals(confirmPassword))
                throw new InvalidDataException("le password non corrispondono");

            user.setUsername(usernameField.getText());
            user.setEmail(emailField.getText());
            user.setPassword(passwordField.getText());
            user.setAdmin(adminCheck.isSelected());

            if (isNew)
                utenteService.createUser(user, newAvatar);
            else
                utenteService.editUser(user, newAvatar);

            goBack();

        } catch (BusinessException | InvalidDataException e) {
            errorBox.setVisible(true);
            errorTxt.setText(e.getMessage());
        }
    }

    // metodo per eliminare un utente
    @FXML
    public void deleteUser(ActionEvent event) {
        if (dispatcher.confirmAlert("Vuoi davvero eliminare questo utente?")) {
            try {
                utenteService.deleteUser(user);
            } catch (BusinessException e) {

                e.printStackTrace();
            }
            goBack();
        }
    }

    // metodo per tornare alla schermata precedente
    @FXML
    public void goBack() {
        dispatcher.goBack();
    }

}
