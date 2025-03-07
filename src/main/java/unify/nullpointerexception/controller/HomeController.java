package unify.nullpointerexception.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import unify.nullpointerexception.business.ArtistsService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.MusicService;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.domain.Song;
import unify.nullpointerexception.view.ViewDispatcher;

public class HomeController implements Initializable, DataInitializable<Object> {

	private MusicService musicService;
	private ArtistsService artistsService;
	private final Integer LIST_ROW_HEIGHT = 24;
	private ViewDispatcher dispatcher;

	@FXML
	private TextField searchBar;

	@FXML
	private ChoiceBox<String> choice;

	@FXML
	private ListView<Song> list;

	// inizializzo il costruttore
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		dispatcher = ViewDispatcher.getInstance();
		UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
		musicService = factory.getMusicService();
		artistsService = factory.getArtistsService();

		choice.setItems(FXCollections.observableArrayList("Titolo", "Artista", "Genere"));
		choice.getSelectionModel().selectFirst();
	}

	// metodo per la ricerca della canzone per titolo, genere e artista
	@FXML
	public void search(KeyEvent event) {

		String text = searchBar.getText();
		List<Song> songs = new ArrayList<Song>();

		try {

			if (choice.getSelectionModel().getSelectedIndex() == 0) {
				songs = musicService.findAllMusicByPrefix(searchBar.getText());

			} else if (choice.getSelectionModel().getSelectedIndex() == 1) {
				songs.clear();
				for (Artist artist : artistsService.findArtistByNamePrefix(searchBar.getText())) {
					songs.addAll(musicService.findAllMusicByArtist(artist));

				}

			} else {
				songs = musicService.findAllMusicByGenrePrefix(searchBar.getText());

			}
			if (text.isEmpty()) {
				list.setVisible(false);
				list.getItems().clear();
			} else {

				list.setItems(FXCollections.observableArrayList(songs));
				list.maxHeightProperty()
						.bind(Bindings.size(FXCollections.observableArrayList(songs)).multiply(LIST_ROW_HEIGHT));

				list.setVisible(true);
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}

	}

	// metodo che apre la canzone quando viene cliccata nella lista
	@FXML
	public void listItemClick(MouseEvent event) throws IOException, BusinessException {
		dispatcher.renderView("song", list.getSelectionModel().getSelectedItem());
	}

}
