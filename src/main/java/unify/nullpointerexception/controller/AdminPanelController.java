package unify.nullpointerexception.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.domain.User;
import unify.nullpointerexception.view.ViewDispatcher;

public class AdminPanelController implements Initializable, DataInitializable<User> {

    private ViewDispatcher dispatcher;

    @FXML
    private Button showArtists;

    @FXML
    private Button showUsers;

    // inizializzo il controller
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dispatcher = ViewDispatcher.getInstance();
    }

    // metodo che apre il pannello degli utenti
    @FXML
    public void showUsers(ActionEvent event) {

        try {
            dispatcher.renderView("adminUsers", null);
        } catch (IOException | BusinessException e) {

            e.printStackTrace();
        }
    }

    // metodo che apre il pannello degli artisti
    @FXML
    public void showArtists(ActionEvent event) {

        try {
            dispatcher.renderView("adminArtists", null);
        } catch (IOException | BusinessException e) {

            e.printStackTrace();
        }
    }

    // metodo che apre il pannello delle canzoni
    @FXML
    public void showSongs(ActionEvent event) {
        try {
            dispatcher.renderView("adminSongs", null);
        } catch (IOException | BusinessException e) {

            e.printStackTrace();
        }
    }

}
