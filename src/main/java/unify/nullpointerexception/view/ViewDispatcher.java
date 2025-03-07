package unify.nullpointerexception.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import unify.nullpointerexception.business.BusinessException;
import unify.nullpointerexception.controller.DataInitializable;
import unify.nullpointerexception.controller.MusicBarController;
import unify.nullpointerexception.domain.Song;
import unify.nullpointerexception.domain.User;


public class ViewDispatcher {

	private static final String FXML_SUFFIX = ".fxml";
	private static final String RESOURCE_BASE = "/viste/";
	private static final String LOGO_PATH = "src/main/resources/img/logo.png";
	private static final List<String> MAIN_MENU_VIEWS = new ArrayList<String>();

	private static ViewDispatcher instance = new ViewDispatcher();

	private Stage stage = new Stage();

	private Stack<Pair<String, Object>> viewStack = new Stack<>();

	private BorderPane layout;
	private MusicBarController musicBarController;

	private ViewDispatcher() {
		
		MAIN_MENU_VIEWS.addAll(List.of("home","playlists", "artists", "searchAlbum", "adminPanel", "generi"));
	}

	// restituisce l'istanza del dispatcher
	public static ViewDispatcher getInstance() {
		return instance;
	}

	// metodo per caricare la vista del login
	public void loginView(Stage stage) throws ViewException, FileNotFoundException {

		Image icon = new Image(new FileInputStream(LOGO_PATH));

		this.stage = stage;

		Parent loginView = loadView("login").getView();

		Scene scene = new Scene(loginView);

		stage.getIcons().add(icon);
		stage.setTitle("Unify nullPointerException");
		stage.setMinWidth(800);
		stage.setMinHeight(500);
		stage.setScene(scene);

		stage.show();
	}

	private void renderError(ViewException e) {
		e.printStackTrace();
		System.exit(1);
	}

	// metodo che carica il layout e la schermata home dopo il login avvenuto
	// correttamente
	public void loggedIn(User utente) throws IOException, BusinessException {
		try {
			View<User> layoutView = loadView("layout");

			// Inizializza l'utente nel controllore.
			DataInitializable<User> layoutController = layoutView.getController();
			layoutController.initializeData(utente);

			layout = (BorderPane) layoutView.getView();

			// si passa l'utente perché le voci nel menu variano a seconda del ruolo
			renderView("home", utente);
			renderMusicBar();
			Scene scene = new Scene(layout);

			stage.setScene(scene);
		} catch (ViewException e) {
			renderError(e);
		}
	}

	// metodo per caricare le voci del menu
	public <T> void renderView(String viewName, T data) throws IOException, BusinessException {
		try {
			// se entro in una delle pagine principali del menu tramite sidebar pulisco lo
			// stack
			if (MAIN_MENU_VIEWS.contains(viewName))
				viewStack.clear();
			viewStack.push(new Pair<String, Object>(viewName, data));
			View<T> view = loadView(viewName);
			DataInitializable<T> controller = view.getController();
			controller.initializeData(data);
			layout.setCenter(view.getView());
		} catch (ViewException e) {
			renderError(e);
		}
	}

	// metodo per tornare indietro
	public void goBack() {
		try {
			viewStack.pop();

			View<Object> view = loadView(viewStack.peek().getKey());
			DataInitializable<Object> controller = view.getController();
			controller.initializeData(viewStack.peek().getValue());
			layout.setCenter(view.getView());
		} catch (ViewException e) {
			renderError(e);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	// metodo che carica la barra in basso con i controlli dell'audio
	public void renderMusicBar() {
		try {

			View<Song> view = loadView("musicBar");
			layout.setBottom(view.getView());
			musicBarController = (MusicBarController)view.getController();

		} catch (ViewException e) {
			renderError(e);
		}
	}

	// metodo per il logout
	public void logout() {
		try {
			musicBarController.stop();
			Parent loginView = loadView("login").getView();
			Scene scene = new Scene(loginView);
			stage.setScene(scene);
		} catch (ViewException e) {
			renderError(e);
		}
	}

	// metodo che carica le viste
	public <T> View<T> loadView(String viewName) throws ViewException {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(RESOURCE_BASE + viewName + FXML_SUFFIX));
			Parent parent = loader.load();

			return new View<>(parent, loader.getController());
		} catch (IOException ex) {
			throw new ViewException(ex);
		}
	}

	// metodo per aprire la finestra per esplorare e caricare i file
	public File uploadFile(FileChooser fileChooser) {
		return fileChooser.showOpenDialog(new Stage());
	}

	public Boolean confirmAlert(String message) {

		Alert alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.CANCEL);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.YES)
			return true;
		else
			return false;
	}

	public void alertMissingFields (String titoloAlert, String headerAlert, String contentAlert){
		Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(titoloAlert);
        alert.setHeaderText(headerAlert);
        alert.setContentText(contentAlert);
        alert.showAndWait();

	}

	public void loadSong(Song song, float volume){
		musicBarController.loadSong(song, volume);
	}

	public void loadQueue(Song song, float volume){
		musicBarController.loadQueue(song, volume);
	}
	public void loadQueue(List<Song> queue, float volume){
		musicBarController.loadQueue(queue, volume);
	}

	public void addToQueue(Song song){
		musicBarController.addToQueue(song);
	}
	public void addToQueue(List<Song> queue){
		musicBarController.addToQueue(queue);
	}

	public void clearQueue(){
		musicBarController.clearQueue();
	}

}
