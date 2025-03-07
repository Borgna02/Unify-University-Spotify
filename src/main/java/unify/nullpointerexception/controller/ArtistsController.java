package unify.nullpointerexception.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import unify.nullpointerexception.business.ArtistsService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.User;
import unify.nullpointerexception.view.ViewDispatcher;

public class ArtistsController implements Initializable, DataInitializable<User> {

	private ArtistsService artistsService;
	private final Integer LIST_ROW_HEIGHT = 24;
	private ViewDispatcher dispatcher;

	@FXML
	private TextField searchBar;

	@FXML
	private ListView<Artist> list;

	// costruttore
	public ArtistsController() {
	}

	// inizializzo il controller
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		dispatcher = ViewDispatcher.getInstance();
		UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
		artistsService = factory.getArtistsService();
	}

	// metodo per cercare un artista per nome
	@FXML
	public void search(KeyEvent event) {

		String text = searchBar.getText();
		List<Artist> artists;

		try {

			artists = artistsService.findArtistByNamePrefix(text);

			if (text.isEmpty()) {

				list.setVisible(false);
				list.getItems().clear();
			} else {

				list.setItems(FXCollections.observableArrayList(artists));
				list.maxHeightProperty()
						.bind(Bindings.size(FXCollections.observableArrayList(artists)).multiply(LIST_ROW_HEIGHT));

				list.setVisible(true);
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}

	}

	// metodo chiamato quando clicco su un elemento della lista che apre la pagina
	// dell'artista scelto
	@FXML
	public void listItemClick(MouseEvent event) throws IOException, BusinessException {
		if (list.getSelectionModel().getSelectedItem() != null)
			dispatcher.renderView("artist", list.getSelectionModel().getSelectedItem());
	}
}
