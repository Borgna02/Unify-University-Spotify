package unify.nullpointerexception.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import unify.nullpointerexception.business.AlbumService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Album;
import unify.nullpointerexception.domain.User;
import unify.nullpointerexception.view.ViewDispatcher;

public class SearchAlbumController implements Initializable, DataInitializable<User> {

	private AlbumService albumService;
	private final Integer LIST_ROW_HEIGHT = 24;
	private ViewDispatcher dispatcher;

	@FXML
	private TextField searchBar;

	@FXML
	private ChoiceBox<String> choice;

	@FXML
	private ListView<Album> list;

	// costruttore
	public SearchAlbumController() {
	}

	// inizializzo il controller
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		dispatcher = ViewDispatcher.getInstance();
		UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
		albumService = factory.getAlbumService();

		choice.setItems(FXCollections.observableArrayList("Titolo", "Genere"));
		choice.getSelectionModel().selectFirst();
	}

	// ricerca di un album
	@FXML
	public void search(KeyEvent event) {

		String text = searchBar.getText();
		List<Album> albums;

		try {

			if (choice.getSelectionModel().getSelectedIndex() == 0)
				albums = albumService.findAllAlbumByPrefix(text);
			else
				albums = albumService.findAllAlbumByGenrePrefix(text);

			if (text.isEmpty()) {

				list.setVisible(false);
				list.getItems().clear();

			} else {

				list.setItems(FXCollections.observableArrayList(albums));
				list.maxHeightProperty()
						.bind(Bindings.size(FXCollections.observableArrayList(albums)).multiply(LIST_ROW_HEIGHT));

				list.setVisible(true);
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}

	}

	// metodo chiamato al click di un elemento della lista
	@FXML
	public void listItemClick(MouseEvent event) {

		try {
			dispatcher.renderView("album", list.getSelectionModel().getSelectedItem());
		} catch (IOException e) {

			e.printStackTrace();
		} catch (BusinessException e) {

			e.printStackTrace();
		}
	}
}
