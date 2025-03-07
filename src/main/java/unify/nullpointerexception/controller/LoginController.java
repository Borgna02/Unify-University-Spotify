package unify.nullpointerexception.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.business.UserService;
import unify.nullpointerexception.domain.User;
import unify.nullpointerexception.view.ViewDispatcher;

public class LoginController implements Initializable, DataInitializable<Object> {

    private ViewDispatcher dispatcher = ViewDispatcher.getInstance();

    private UserService userService;

    // costruttore
    public LoginController() {
        dispatcher = ViewDispatcher.getInstance();
        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
        userService = factory.getUserService();
    }

    // inizializzo il controller
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        signInButton.setDefaultButton(true);
        loginHBox.getChildren().remove(signUpForm);
        signInErrorMsg.setVisible(false);
        signUpErrorMsg.setVisible(false);
    }

    // SIGN IN
    // -----------------------------------------------------------------------------------------------

    @FXML
    private HBox loginHBox;

    @FXML
    private Button goToSignUp;

    @FXML
    private GridPane signInForm;

    @FXML
    private Button goToSignInButton;

    @FXML
    private GridPane signUpForm;

    @FXML
    public void showSignIn(ActionEvent event) {
        // premendo ENTER verrà cliccato il tasto login
        signUpButton.setDefaultButton(false);
        signInButton.setDefaultButton(true);

        // viene mostrato il form di login e nascosto quello di registrazione
        loginHBox.getChildren().remove(signUpForm);
        GridPane.setConstraints(signInForm, 1, 0);
        loginHBox.getChildren().add(signInForm);

        signUpErrorMsg.setVisible(false);
    }

    @FXML
    public void showSignUp(ActionEvent event) {
        // premendo ENTER verrà cliccato il tasto registrati
        signUpButton.setDefaultButton(false);
        signInButton.setDefaultButton(true);

        // viene mostrato il form di registrazione e nascosto quello di login
        loginHBox.getChildren().remove(signInForm);
        GridPane.setConstraints(signUpForm, 1, 0);
        loginHBox.getChildren().add(signUpForm);

        // cambiando form il messaggio d'errore viene reso invisibile
        signInErrorMsg.setVisible(false);
    }

    @FXML
    private Button signInButton;

    @FXML
    private TextField signInEmail;

    @FXML
    private PasswordField signInPassword;

    @FXML
    private HBox signInErrorMsg;

    @FXML
    public void signIn(ActionEvent event) {

        try {
            User user = userService.authenticate(signInEmail.getText().toLowerCase(), signInPassword.getText());
            dispatcher.loggedIn(user);
        } catch (BusinessException | IOException e) {
            // dispatcher.renderError(e);
            signInErrorMsg.setVisible(true);
        }
    }

    // SIGN UP - REGISTAZIONE
    // -----------------------------------------------------------------------------------------------------------

    @FXML
    private Button signUpButton;

    @FXML
    private TextField signUpUsername;

    @FXML
    private TextField signUpEmail;

    @FXML
    private PasswordField signUpPassword;

    @FXML
    private PasswordField signUpConfirmPassword;

    @FXML
    private HBox signUpErrorMsg;

    @FXML
    private Text signUpErrorText;

    @FXML
    public void signUp(ActionEvent event) {

        Pattern regexPasswordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$");
        Pattern regexEmailPattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$");

        String email = signUpEmail.getText();
        String password = signUpPassword.getText();
        String confirmPassword = signUpConfirmPassword.getText();

        try {

            if (!regexEmailPattern.matcher(email).matches())
                throw new InvalidDataException("email non valida");

            if (!regexPasswordPattern.matcher(password).matches())
                throw new InvalidDataException("password non valida");

            if (!password.contains(confirmPassword))
                throw new InvalidDataException("le password non corrispondono");

            User user = new User();
            user.setUsername(signUpUsername.getText());
            user.setEmail(email);
            user.setPassword(password);
            user.setAdmin(false);

            userService.createUser(user, null);
            dispatcher.loggedIn(user);

        } catch (BusinessException | InvalidDataException | IOException e) {
            signUpErrorMsg.setVisible(true);
            signUpErrorText.setText(e.getMessage());
        }
    }

}
