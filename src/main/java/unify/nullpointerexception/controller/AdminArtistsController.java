package unify.nullpointerexception.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.lang.model.type.NullType;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import unify.nullpointerexception.business.ArtistsService;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.domain.Artist;
import unify.nullpointerexception.view.ViewDispatcher;

public class AdminArtistsController implements Initializable, DataInitializable<NullType> {

	private ArtistsService artistsService;
	private ViewDispatcher dispatcher;

	private final String IMAGES_PATH = "src/main/resources/img/";

	@FXML
	private ChoiceBox<String> choice;

	@FXML
	private TableView<Artist> table;

	@FXML
	private TableColumn<Artist, String> idColumn;

	@FXML
	private TableColumn<Artist, String> stageNameColumn;

	@FXML
	private TableColumn<Artist, Button> editColumn;

	@FXML
	private TableColumn<Artist, Button> deleteColumn;

	@FXML
	private Button creaUsers;

	@FXML
	private Button deleteUsers;

	@FXML
	private Button modifyUser;

	@FXML
	private TextField searchBar;

	@FXML
	private ListView<Artist> list;

	// inizializzo il costruttore
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		dispatcher = ViewDispatcher.getInstance();
		UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
		artistsService = factory.getArtistsService();

		choice.setItems(FXCollections.observableArrayList("ID", "Nome d'arte"));
		choice.getSelectionModel().selectFirst();

		try {
			List<Artist> artists = artistsService.getAllArtists();

			idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
			stageNameColumn.setCellValueFactory(new PropertyValueFactory<>("stageName"));

			editColumn.setStyle("-fx-alignment: CENTER;");
			editColumn.setCellValueFactory((CellDataFeatures<Artist, Button> param) -> {
				final Button editBtn = new Button();
				try {
					ImageView editIcon = new ImageView(
							new Image(new FileInputStream(new File(IMAGES_PATH + "whiteEdit.png"))));
					editIcon.setFitWidth(20);
					editIcon.setFitHeight(20);
					editBtn.setGraphic(editIcon);
					editBtn.setStyle("-fx-background-color: #25408F");
					editBtn.setCursor(Cursor.HAND);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				editBtn.setOnAction((ActionEvent event) -> {
					try {
						dispatcher.renderView("createOrEditArtist", param.getValue());
					} catch (IOException | BusinessException e) {
						e.printStackTrace();
					}
				});
				return new SimpleObjectProperty<Button>(editBtn);

			});

			deleteColumn.setStyle("-fx-alignment: CENTER;");
			deleteColumn.setCellValueFactory((CellDataFeatures<Artist, Button> param) -> {

				final Button deleteBtn = new Button();
				ImageView deleteIcon;
				try {
					deleteIcon = new ImageView(
							new Image(new FileInputStream(new File(IMAGES_PATH + "whiteTrashBin.png"))));
					deleteIcon.setFitWidth(12);
					deleteIcon.setFitHeight(20);
					deleteBtn.setGraphic(deleteIcon);
					deleteBtn.setStyle("-fx-background-color: #25408F");
					deleteBtn.setCursor(Cursor.HAND);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				deleteBtn.setOnAction((ActionEvent event) -> {
					try {
						if (dispatcher.confirmAlert("Vuoi davvero eliminare questo artista?")) {
							artistsService.deleteArtist(param.getValue());
							table.getItems().clear();
							table.getItems().addAll(artistsService.getAllArtists());
						}
					} catch (BusinessException e) {
						e.printStackTrace();
					}

				});
				return new SimpleObjectProperty<Button>(deleteBtn);
			});

			table.getItems().addAll(artists);
		} catch (BusinessException e1) {
			e1.printStackTrace();
		}
	}

	// metodo per la ricerca
	@FXML
	public void search(KeyEvent event) {

		String text = searchBar.getText();
		List<Artist> artists = new ArrayList<Artist>();

		try {

			if (choice.getSelectionModel().getSelectedIndex() == 0) {
				if (Pattern.matches("[0-9]+", text)) {
					artists.clear();
					artists.add(artistsService.findArtistById(Integer.parseInt(text)));
				} else {
					searchBar.setText("");
					artists = artistsService.getAllArtists();
				}

			} else
				artists = artistsService.findArtistByNamePrefix(text);

			table.getItems().clear();
			table.getItems().addAll(artists);

		} catch (BusinessException e) {
			e.printStackTrace();
		}

	}

	// metodo chiamato quando si clicca sul tasto per creare un nuovo artista
	@FXML
	public void createArtist(ActionEvent event) {

		try {
			dispatcher.renderView("createOrEditArtist", null);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	// metodo per tornare alla schermata precedente
	@FXML
	public void goBack(ActionEvent event) {
		dispatcher.goBack();
	}
}
