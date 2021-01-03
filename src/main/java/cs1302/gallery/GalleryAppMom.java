package cs1302.gallery;

import javafx.application.Application;
import javafx.event.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.*;


public class GalleryAppMom extends Application {
	final int stageMaxWidth = 640;
	final int stageMaxHeight = 480;
	@Override
	public void start(Stage stage) {
		HBox pane = new HBox();
		VBox menus = new VBox();
		MenuBar menuBar = makeMenuBar();
		menuBar.setPrefWidth(stageMaxWidth);
		ToolBar toolBar = makeToolBar();
		menus.getChildren().addAll(menuBar, toolBar);
		pane.getChildren().add(menus);
		//Scene
		Scene scene = new Scene(pane, stageMaxWidth, stageMaxHeight);
		//Set Stage
		stage.setMaxWidth(stageMaxWidth);
		stage.setMaxHeight(stageMaxHeight);
		stage.setTitle("Gallery!");
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
		
		performSearch("rock");
		
		//looking at the printed data, found "artworkUrl00" tag which contains the jpeg file in 100X100 size  (there is also a smaller version but your instructions on how to use
		// use that one)
		// eventually each line of the search data can be parsed, it is in json format. See project instructions about this
		// your homework suggest using 
		
		// once u have the image url you can show the imsge like this:
		InputStream imageStream = getImage("http://is1.mzstatic.com/image/thumb/Music/v4/80/81/20/808120a5-3b1c-c97e-773c-da793a521bc5/source/100x100bb.jpg");
		GridPane gridPane = new GridPane();
		Image image = new Image(imageStream);
	
        // followiing is just for show, u will have to get a different image for each location
		// the returned search data is an array of records and u can get the array size to know how many you have
		gridPane.add(new ImageView(image),0,0);
		gridPane.add(new ImageView(image),0,1);
		gridPane.add(new ImageView(image),1,0);
		gridPane.add(new ImageView(image),1,1);
		gridPane.add(new ImageView(image),1,2);
		menus.getChildren().add(gridPane);
		
		stage.show();
		
	}//start
	
	private ToolBar makeToolBar() {
		//Pause & Play
		Button pause = new Button("Pause");
		pause.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				//do stuff
			}
		});
		//Text Field
		Label text = new Label("Search Query:");
		//Search Field
		TextField search = new TextField();
		//Update Images
		Button update = new Button("Update Image");
		update.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				//do stuff
			}
		});
		ToolBar toolBar = new ToolBar(pause, text, search, update);
		return toolBar;
	}//makeToolBar

	public MenuBar makeMenuBar(){
		//MenuBar
		MenuBar menuBar = new MenuBar();
		Menu file = new Menu("File");
		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t){
				System.exit(0);
			}
		});

		Menu theme = new Menu("Theme");
		Menu help = new Menu("Help");
		MenuItem aboutMe = new MenuItem("About Me");
		aboutMe.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t){
				//add code
			}

		});
		file.getItems().add(exit);
		help.getItems().add(aboutMe);
		menuBar.getMenus().addAll(file, theme, help);
		return menuBar;
	}//makeMenuBar


	private void performSearch(String searchParameter) {
	        String[] values = searchParameter.split(" ");
	        String formattedSearchParameter = values[0];
	        if (values.length>1){
	        	for (int i=0;i<values.length;i++){
	        		formattedSearchParameter = formattedSearchParameter+"+"+values[i];
	        	}	
	        }
	        try{
	        	URL url = new URL("https://itunes.apple.com/search?term="+formattedSearchParameter);
	        	URLConnection connection = url.openConnection();
	        	BufferedReader in = new BufferedReader(new InputStreamReader(
	                                    connection.getInputStream()));
	        	String inputLine;
	        	while ((inputLine = in.readLine()) != null) 
	        		System.out.println(inputLine);
	        	in.close();
	        	}
	        catch (Exception e){
	        	System.out.println("error accessing url "+ "https://itunes.apple.com/search?term="+formattedSearchParameter);
	        }
	        
	}
	
	private InputStream getImage(String imageUrl){
		InputStream image = null;
		try {
			image = new URL(imageUrl).openStream();
			Files.copy(image, Paths.get("image.jpg"));

		}
		catch (IOException e) {
		}
		return image;
	}
		
		
	public static void main(String[] args) {
		try {
			Application.launch(args);
		} catch (UnsupportedOperationException e) {
			System.out.println(e);
			System.err.println("If this is a DISPLAY problem, then your X server connection");
			System.err.println("has likely timed out. This can generally be fixed by logging");
			System.err.println("out and logging back in.");
			System.exit(1);
		} // try
	} // main


} 
// GalleryApp

