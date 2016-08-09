package application;
import audio.AudioPlayer;
import gameComponenets.GameBoardModel;
import gameComponenets.Variables;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class StartScreenController extends Application {
	private Stage window;
	private Scene scene;
	private Parent root;
	public void start(Stage primaryStage) throws Exception {
		root = FXMLLoader.load(getClass().getResource("/fxmlDocs/StartScreenView.fxml"));
		scene = new Scene(root, Variables.getWIDTH(), Variables.getHEIGHT());
		window = primaryStage;
		window.setTitle("Breakout");
		window.setResizable(false);
		window.setScene(scene);
		window.show();

		//Play Background Music
		//AudioPlayer.playSoundEffectIndefinitely(AudioPlayer.gameMusicFile);
		//Play Background Music end
	}
	public void play(ActionEvent event) {
		GameController tc = new GameController();
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		try {
			tc.start(stage);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		stage.setScene(tc.getScene());
		stage.show();
	}
	public static void main(String[] args) {
		launch(args);

	}
}
