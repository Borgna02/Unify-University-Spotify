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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.business.UserService;
import unify.nullpointerexception.domain.User;
import unify.nullpointerexception.view.ViewDispatcher;

public class UserOptionsController implements Initializable, DataInitializable<User> {

    private ViewDispatcher dispatcher;

    private User user;
    private byte[] avatar;
    private boolean avatarModified = false;

    private Pattern regexPasswordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$");
    private Pattern regexEmailPattern = Pattern
            .compile("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$");

    @FXML
    private ImageView tempAvatar;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private TextField confirmNewPasswordField;

    @FXML
    private HBox dataErrorMsg;

    @FXML
    private Text dataErrorTxt;

    private UserService userService;

    // costruttore
    public UserOptionsController() {
        dispatcher = ViewDispatcher.getInstance();
    }

    // inizializzo il controller
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
        userService = factory.getUserService();
    }

    // inizializzo i dati del controller
    @Override
    public void initializeData(User user) {

        this.user = user;

        try {
            avatar = userService.fetchAvatar(user);
            tempAvatar.setImage(new Image(new ByteArrayInputStream(avatar)));
        } catch (BusinessException e) {
            e.printStackTrace();
        }

        usernameField.setText(user.getUsername());
        emailField.setText(user.getEmail());

    }

    // metodo per caricare un file
    @FXML
    public void browseFiles(ActionEvent events) {
        // apro l'esplora risorse e caricare il file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Scegli la tua immagine");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File newAvatarFile = dispatcher.uploadFile(fileChooser);

        // carico la nuova immagine nella "preview" all'interno della vista per la
        // gestione dell'avatar
        if (newAvatarFile != null) {

            try {

                avatar = Files.readAllBytes(newAvatarFile.toPath());
                tempAvatar.setImage(new Image(new ByteArrayInputStream(avatar)));
                avatarModified = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // metodo per salvare le modifiche
    @FXML
    public void saveChanges(ActionEvent event) {

        String email = emailField.getText();
        String password = newPasswordField.getText();
        String confirmPassword = confirmNewPasswordField.getText();

        try {

            if (!regexEmailPattern.matcher(email).matches())
                throw new InvalidDataException("email non valida");

            if (!password.isEmpty()) {

                if (!regexPasswordPattern.matcher(password).matches())
                    throw new InvalidDataException("la nuova password non e' valida");

                if (!password.equals(confirmPassword))
                    throw new InvalidDataException("le password non corrispondono");

                user.setPassword(password);
            }

            user.setUsername(usernameField.getText());
            user.setEmail(email);

            if (avatarModified)
                userService.editUser(user, avatar);
            else
                userService.editUser(user, null);

            dataErrorMsg.setVisible(false);
            dispatcher.confirmAlert("Modifiche avvenute con successo");

        } catch (BusinessException | InvalidDataException e) {
            dataErrorMsg.setVisible(true);
            dataErrorTxt.setText(e.getMessage());
        }
    }

    // metodo per tornare alla schermata precedente
    @FXML
    public void goBack(ActionEvent event) {
        try {
            dispatcher.renderView("home", user);
        } catch (IOException | BusinessException e) {
            e.printStackTrace();
        }
    }

    // metodo per eliminare l'account
    @FXML
    public void deleteAccount(ActionEvent event) {
        if (dispatcher.confirmAlert("Vuoi davvero eliminare questo account?")) {
            try {
                userService.deleteUser(user);
            } catch (BusinessException e) {
                e.printStackTrace();
            }
            dispatcher.logout();
        }
    }
}
