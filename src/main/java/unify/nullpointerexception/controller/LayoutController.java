package unify.nullpointerexception.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.business.UserService;
import unify.nullpointerexception.domain.User;
import unify.nullpointerexception.view.ViewDispatcher;
import unify.nullpointerexception.view.ViewException;

public class LayoutController implements Initializable, DataInitializable<User> {

    @FXML
    private Label sidebarUsernameField;

    @FXML
    private ImageView userAvatar;

    @FXML
    private HBox avatarBox;

    @FXML
    private VBox menuElements;

    private ViewDispatcher dispatcher;

    // funzione per il logout
    @FXML
    public void logoutAction(ActionEvent event) {
        ViewDispatcher dispatcher = ViewDispatcher.getInstance();
        dispatcher.logout();
        dispatcher.clearQueue();
    }

    // voce del menu specifica per gli admin
    private static final MenuElement MENU_ADMIN = new MenuElement("Pannello admin", "adminPanel");

    // utente loggato
    private User user;

    private UserService userService;

    // voci del menu generiche per qualsiasi utente
    private static final MenuElement[] MENU_NORMALE = { new MenuElement("Home", "home"),
            new MenuElement("Playlist", "playlists"),
            new MenuElement("Artisti", "artists"), new MenuElement("Album", "searchAlbum"),
            new MenuElement("Generi", "genres") };

    // costruttore
    public LayoutController() {
        dispatcher = ViewDispatcher.getInstance();
        UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
        userService = factory.getUserService();
    }

    // inizializzo il controller
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    // inizializzo i dati del controller
    @Override
    public void initializeData(User user) throws IOException {
        this.user = user;

        try {
            userAvatar.setImage(new Image(new ByteArrayInputStream(userService.fetchAvatar(user))));
        } catch (BusinessException e) {
            e.printStackTrace();
        }

        for (MenuElement menu : MENU_NORMALE) {
            menuElements.getChildren().add(createButton(menu));
        }
        if (user.isAdmin()) {
            menuElements.getChildren().add(createButton(MENU_ADMIN));

        }
        // spazio tra i buttons della sidebar
        menuElements.setSpacing(3);
        // inserisce l'username dell'utente nel layout
        sidebarUsernameField.setText(user.getUsername());

    }

    // funzione che crea i vari button delle voci del menu
    private Button createButton(MenuElement viewItem) {
        Button button = new Button(viewItem.getName());

        // impostazioni di stile dei button
        button.setStyle("-fx-background-color: #25408F; -fx-font-size: 14;");
        button.setTextFill(Paint.valueOf("white"));
        button.setPrefHeight(12);
        button.setPrefWidth(180);
        button.setCursor(Cursor.HAND);
        button.setOnAction((ActionEvent event) -> {
            try {
                dispatcher.renderView(viewItem.getView(), user);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (BusinessException e) {
                e.printStackTrace();
            }
        });
        return button;
    }

    // metodo che apre la schermata di modifica dell'account
    @FXML
    public void openUserOptions(ActionEvent event) throws ViewException, IOException, BusinessException {
        dispatcher.renderView("userOptions", user);
    }

}
