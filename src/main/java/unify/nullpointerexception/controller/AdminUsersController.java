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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.business.UnifyBusinessFactory;
import unify.nullpointerexception.business.UserService;
import unify.nullpointerexception.domain.User;
import unify.nullpointerexception.view.ViewDispatcher;

public class AdminUsersController implements Initializable, DataInitializable<NullType> {

	private UserService userService;
	private ViewDispatcher dispatcher;

	private final String IMAGES_PATH = "src/main/resources/img/";

	@FXML
	private TextField searchBar;

	@FXML
	private ChoiceBox<String> choice;

	@FXML
	private Button creaUtente;

	@FXML
	private TableView<User> table;

	@FXML
	private TableColumn<User, String> idColumn;

	@FXML
	private TableColumn<User, String> usernameColumn;

	@FXML
	private TableColumn<User, String> emailColumn;

	@FXML
	private TableColumn<User, String> isAdminColumn;

	@FXML
	private TableColumn<User, Button> editColumn;

	@FXML
	private TableColumn<User, Button> deleteColumn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		dispatcher = ViewDispatcher.getInstance();
		UnifyBusinessFactory factory = UnifyBusinessFactory.getInstance();
		userService = factory.getUserService();

		choice.setItems(FXCollections.observableArrayList("ID", "Email"));
		choice.getSelectionModel().selectFirst();

		try {
			List<User> users = userService.getAllUsers();

			idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
			usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
			emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

			isAdminColumn.setCellValueFactory((CellDataFeatures<User, String> param) -> {

				User user = param.getValue();
				String isAdminToString = "No";
				if (user.isAdmin())
					isAdminToString = "Sì";
				return new SimpleObjectProperty<String>(isAdminToString);
			});

			editColumn.setStyle("-fx-alignment: CENTER;");
			editColumn.setCellValueFactory((CellDataFeatures<User, Button> param) -> {
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
						dispatcher.renderView("createOrEditUser", param.getValue());
					} catch (IOException | BusinessException e) {
						e.printStackTrace();
					}
				});
				return new SimpleObjectProperty<Button>(editBtn);

			});

			deleteColumn.setStyle("-fx-alignment: CENTER;");
			deleteColumn.setCellValueFactory((CellDataFeatures<User, Button> param) -> {

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
						if (dispatcher.confirmAlert(
								"Vuoi davvero eliminare questo utente, il suo avatar e tutte le sue playlist?")) {
							userService.deleteUser(param.getValue());
							table.getItems().clear();
							table.getItems().addAll(userService.getAllUsers());
						}
					} catch (BusinessException e) {
						e.printStackTrace();
					}
				});
				return new SimpleObjectProperty<Button>(deleteBtn);
			});
			table.getItems().addAll(users);
		} catch (BusinessException e1) {
			e1.printStackTrace();
		}

	}

	@FXML
	public void search(KeyEvent event) {

		String text = searchBar.getText();
		List<User> users = new ArrayList<User>();

		try {

			if (choice.getSelectionModel().getSelectedIndex() == 0) {
				if (Pattern.matches("[0-9]+", text)) {
					users.clear();
					users.add(userService.findUserById(Integer.parseInt(text)));
				} else {
					searchBar.setText("");
					users = userService.getAllUsers();
				}

			} else
				users = userService.findUsersByEmailPrefix(text);

			table.getItems().clear();
			table.getItems().addAll(users);

		} catch (BusinessException e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void createUser(MouseEvent event) {

		try {
			dispatcher.renderView("createOrEditUser", null);
		} catch (IOException | BusinessException e) {

			e.printStackTrace();
		}

	}

	@FXML
	public void goBack() {
		dispatcher.goBack();
	}
}
