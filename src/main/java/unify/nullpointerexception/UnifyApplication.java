package unify.nullpointerexception;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import unify.nullpointerexception.view.ViewDispatcher;

public class UnifyApplication extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		ViewDispatcher dispatcher = ViewDispatcher.getInstance();

		dispatcher.loginView(stage);

		// kill dei processi una volta chiusa la finestra
		kill(stage);
	}

	public static void kill(Stage stage) {
        stage.setOnHiding(event ->{

			Platform.runLater(new Runnable() {
				@Override
				public void run(){
					System.exit(0);
				}
			});
			
		});
	}
}
