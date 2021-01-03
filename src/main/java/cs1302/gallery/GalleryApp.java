package cs1302.gallery;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Random;

import javax.imageio.ImageIO;

import java.io.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A class that created a GUI interface. It searches band images of a particular genre.
 * @author Danielle Mawson
 * @version 1.0
 */
public class GalleryApp extends Application {
	
	//Setup instance variables
	boolean printDebugMsgs = false;  // used to control printing of debug statements
	boolean searching = false;
	
	final int stageMaxWidth = 500;
	final int stageMaxHeight = 515;
	
	Image[] images = new Image[50]; //allocate the 50 possible images to show
	int imageCount = 0;  // holds the count of how many images were loaded
	int prevImageCount = 0;
	
	boolean playMode = true;  // start in play mode
	int nextImageIndex = -1; // next image index for play mode (0 relative)
	
	// following definitions are for items used in different methods:
	Scene scene; //holds panes
	VBox vPane; //holds MenuBar, ToolBar, GridPane, and HBox
	GridPane gridPane; //holds images
	ProgressBar progBar;
	Timeline timeLine;
	Label credits = new Label(""); //empty
	Label searchResults = new Label(""); //empty
	String lastDisplayedSearch = new String("folk");
	String lastSearch =  new String("folk");
	String defaultSearch = new String("folk");
	TextField search = new TextField(defaultSearch);
	
	String defaultCredit = new String("      Images provided courtesy of Itunes :)");
	HBox lastRow; //holds Progress Bar and number or results
	 
	Random randomNumber = new Random();
    String styleName = new String("default.css");
	
	/**
	 * This method starts up the GUI display.
	 * @param stage			provides space for the scene to display
	 * @Override
	 */
	public void start(Stage stage) {
		vPane = new VBox();
		//Top Display
		MenuBar menuBar = makeMenuBar();
		menuBar.setPrefWidth(stageMaxWidth);
		ToolBar toolBar = makeToolBar();	
        
		//Middle Display
		makeGridPane();	
		setGridPane();//init to default search which just run
		
		//Bottom Display
		lastRow = makeLastRow();
		//Put it all together and what does that spell?
		vPane.getChildren().addAll(menuBar, toolBar,gridPane,lastRow); // add all the vertical components	
		
		//Scene Setup
		scene = new Scene(vPane);
		scene.getStylesheets().add(styleName); //currently default
		
		//Set Stage
		stage.setMaxWidth(stageMaxWidth);
		stage.setMaxHeight(stageMaxHeight);
		stage.setMaximized(true);
		stage.setTitle("Gallery!");
		stage.setScene(scene);
		stage.show();	
		
		performSearch(defaultSearch);  // will run on separate thread
		createPlayModetimeLine();
	}//start

	/**
	 * Creates a ToolBar, which contains pause, text, search, and update.
	 * @return toolBar			completed ToolBar with all necessary components
	 */
	private ToolBar makeToolBar() {
		//Pause & Play
		
		//start in play mode, so button option is pause
		Button pause = new Button("Pause");    
		pause.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				//toggle mode only when search is complete:
				if (!searching){
					if (pause.getText().equals("Play")){
						pause.setText("Pause");
						playMode = true;
						timeLine.play();
					}
					else{
						pause.setText("Play");
						playMode = false;
						timeLine.pause();
					}
				}
			}
		});
		//Text Field
		Label text = new Label("Search Query:");
		//Search Field
		//defined at higher level since it is used by other methods
		
		//Update Images
		Button update = new Button("Update Image");
		update.setOnAction(new EventHandler<ActionEvent>() {
		
			public void handle(ActionEvent t) {		
				// stop play mode
				timeLine.stop();
				// save previous search info in case new search does not lead to enough items
				 
	             prevImageCount = imageCount; 	
				resetProgressBar();             // restart the progress bar
				searchResults.setText("");
				credits.setText("");   
				performSearch(search.getText());	// will run on separate thread
			}
		});
	 
		ToolBar toolBar = new ToolBar(pause, text, search, update);
		return toolBar;
	}//makeToolBar

	/**
	 * Creates a MenuBar, which contains file, theme, and help.
	 * @return menuBar			completed MenuBar with all necessary components
	 */
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
		MenuItem greyDay = new MenuItem("Default");
		greyDay.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t){
				styleName = "default.css";
				scene.getStylesheets().clear();
				scene.getStylesheets().add(styleName);				
			}
		});
		
		
		MenuItem ocean = new MenuItem("Ocean");
		ocean.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t){
				styleName = "ocean.css";
				scene.getStylesheets().clear();
				scene.getStylesheets().add(styleName);
			}

		});
		
		MenuItem sunset = new MenuItem("Sunset");
		sunset.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t){
				styleName = "sunset.css";
				scene.getStylesheets().clear();
				scene.getStylesheets().add(styleName);
			}
		});
		
		Menu help = new Menu("Help");
		MenuItem aboutMe = new MenuItem("About Me");
		aboutMe.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t){
				Stage aboutMeStage= new Stage();
				Label email = new Label("Email: dem07541@uga.edu");
				Label version = new Label("Version: 0.0");
				ImageView showMe = new ImageView();
				Image me = new Image("https://i.pinimg.com/736x/99/cc/af/"
						+ "99ccafa9e8a815e70b90ca788c623b9f--spongebob-funny-pictures-spongebob-memes.jpg");
				showMe.setImage(me);
				VBox vBoxMe = new VBox();
				vBoxMe.getChildren().addAll(showMe, email, version);
				Scene sceneMe = new Scene(vBoxMe);
				aboutMeStage.setScene(sceneMe);
				aboutMeStage.setMaxWidth(stageMaxWidth);
				aboutMeStage.setMaxHeight(stageMaxHeight);
				aboutMeStage.setTitle("About Danielle Mawson!");
				aboutMeStage.setMaximized(true);
				aboutMeStage.show();
			}

		});
		file.getItems().add(exit);
		theme.getItems().addAll(greyDay,ocean,sunset);
		help.getItems().add(aboutMe);
		menuBar.getMenus().addAll(file, theme, help);
		return menuBar;
	}//makeMenuBar
	
	/**
	 * Creates an empty GridPane with the correct amount of space for future images
	 */
	public void makeGridPane(){
		gridPane = new GridPane();
		gridPane.getColumnConstraints().add(new ColumnConstraints(100)); // column 0 is 100 wide
		gridPane.getColumnConstraints().add(new ColumnConstraints(100)); // column 1 is 100 wide
		gridPane.getColumnConstraints().add(new ColumnConstraints(100)); // column 2 is 100 wide
		gridPane.getColumnConstraints().add(new ColumnConstraints(100)); // column 3 is 100 wide
		gridPane.getColumnConstraints().add(new ColumnConstraints(100)); // column 4 is 100 wide
		
		gridPane.getRowConstraints().add(new RowConstraints(100)); // row 0 is 100 tall
		gridPane.getRowConstraints().add(new RowConstraints(100)); // row 1 is 100 tall
		gridPane.getRowConstraints().add(new RowConstraints(100)); // row 2 is 100 tall
		gridPane.getRowConstraints().add(new RowConstraints(100)); // row 3 is 100 tall	
	}
	
	/**
	 * Sets up a GridPane with the iTunes images corresponding to the search
	 */
	private void setGridPane(){		
			//clears previous search
			gridPane.getChildren().clear();
			for (int i = 0; i < 4; i++){
				for (int j = 0; j < 5; j++){					
					int imageIndex = (i*5)+j;
					
					//more spots than found images
					if (imageIndex > imageCount-1) {
						Label temp = new Label(" "); //empty label
						temp.setAlignment(Pos.CENTER);
						gridPane.add(temp,j,i);	//set to fill extra spaces
					}
					else{
						//debugPrint("adding image at "+i+","+j +" or at index "+imageIndex);
						gridPane.add(new ImageView(images[imageIndex]),j,i);
					}
				}
			}
			
			searchResults.setText("    "+imageCount+" items found"); //At the bottom near progress bar
			if (imageCount > 0)
				credits.setText(defaultCredit);  
		
		
	}
	
	/**
	 * Creates an HBox which is the last row of the GUI. It contains the progress bar, search results, and credits.
	 * @return lastRow			HBox containing all necessary components
	 */
	public   HBox makeLastRow(){
		HBox lastRow = new HBox();
		lastRow.setAlignment(Pos.BOTTOM_LEFT);
		progBar = new ProgressBar(0);	
		// Listen for Slider value changes
		
		lastRow.getChildren().addAll(progBar,searchResults,credits);
		return lastRow;
	}
	
	/**
	 * Sets the ProgressBar back to default value
	 */
	public void resetProgressBar(){
		debugPrint("+++++++++++++++++++++++++RESET PROGRESS HERE ++++++++++++++++++++++++++");
		vPane.getChildren().remove(lastRow);
		lastRow.getChildren().removeAll(progBar,searchResults,credits);
		progBar = new ProgressBar();	
		lastRow.getChildren().addAll(progBar,searchResults,credits);
		vPane.getChildren().add(lastRow);
		 
	}
	
	/**
	 * Updates the ProgressBar to its most recent checkpoint
	 * @param barValue		A ratio representation of the progress made
	 */
    public void updateProgressBar(double barValue){
    	Platform.runLater(new Runnable(){
			@Override
			public void run() {
				progBar.setProgress(barValue);
			}	});
    	
    	if (barValue == 1.0){
        	Platform.runLater(new Runnable(){
				@Override
				public void run() {
					if (imageCount<20){
						Alert alert = new Alert(AlertType.CONFIRMATION);
						DialogPane dialogPane = alert.getDialogPane();
						dialogPane.getStylesheets().add(styleName);
						
						alert.setTitle("Confirmation Action");
						if (imageCount == 0)
							alert.setHeaderText("No titles found");
						else
							alert.setHeaderText("Found "+imageCount+" titles");
						alert.setContentText("Would you like to load new results?");

						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK){
							setGridPane();
							lastDisplayedSearch = lastSearch;  // save last search in case we need to cancel next search
							System.out.println("in setgrid pane saved "+lastDisplayedSearch);
						} else {
						    //restore original search values
							
							search.setText(lastDisplayedSearch);
							imageCount = prevImageCount;
							searchResults.setText("    "+imageCount+" items found");
							if (imageCount >0)
								credits.setText(defaultCredit);  
						}
					}
					else {
						setGridPane();
						lastDisplayedSearch = lastSearch;  // save last search in case we need to cancel next search
						
					}	
					if (playMode)
						timeLine.play();
				}	
			});
    	}
    }
    
    /**
	 * Searches album covers that match String parameter
	 * @param searchParameter			The name of the genre of music you want to search
	 */
	private void performSearch(String searchParameter) {
        lastSearch = searchParameter;
		Runnable r = () -> {
			searching = true;
 
			updateProgressBar((double) 0.0);
			String[] values = searchParameter.split(" ");
			String formattedSearchParameter = values[0];
			if (values.length > 1){
				for (int i = 0; i < values.length; i++){
					formattedSearchParameter = formattedSearchParameter+"+"+values[i];
				}	
			}
			imageCount = 0;
	        for (int i = 0; i < 50; i++){
	        	images[i] = null;  // clear out previous values
	        }
			try{
				URL url = new URL("https://itunes.apple.com/search?term="+formattedSearchParameter);
				InputStreamReader reader = new InputStreamReader(url.openStream());

				JsonParser jp = new JsonParser();
				JsonElement je = jp.parse(reader);
				
				JsonObject root = je.getAsJsonObject();                      // root of response
				JsonArray results = root.getAsJsonArray("results");          // "results" array
				int numResults = results.size();  // "results" array size
				updateProgressBar((double) 0.1);
				 
				for (int i = 0; i < numResults; i++) {                       
				    JsonObject result = results.get(i).getAsJsonObject();    // object i in array
				    JsonElement artworkUrl100 = result.get("artworkUrl100"); // artworkUrl100 member
				    if (artworkUrl100 != null) {   // member might not exist
				    	 imageCount++;
				         String artUrl = artworkUrl100.getAsString();        // get member as string
				         debugPrint(artUrl);   // print the string
				         InputStream imageStream = getImage(artUrl);
				 		 images[i] = new Image(imageStream);
				 		 // update slider every 6 reads...
				 		 if (i % 6 == 0){
				 			updateProgressBar((double)(0.1 + (double)i / 60));
				 		 }
				 		debugPrint("for i = "+i+" progress set to "+ (1 + (double)i / 60));
				    } // if
				} // for
				reader.close();
				if (imageCount > 20) 
					nextImageIndex = 20;  // next is 21...
				else 
					nextImageIndex = -1;  // not enough images to have a next
				updateProgressBar((double)1.0);
				searching = false;
				
				
			}
			catch (Exception e){
				System.out.println("exception "+e);
				System.out.println("error accessing url "+ "https://itunes.apple.com/search?term="+formattedSearchParameter);
			}
			
		}; //end run
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.start();

	}


	/**
	 * Copies an image from the stream 
	 * @param imageUrl 			The URL of the desired image
	 * @return image 			The image from the URL parameter
	 */
	private InputStream getImage(String imageUrl){
		InputStream image = null; //empty previous stream
		try {
			image = new URL(imageUrl).openStream();
			//Files.copy(image, Paths.get("image.jpg"));

		}
		catch (IOException e) {
		}
		return image;
	}

	/**
	 * Lets swapImages() run indefinitely
	 */
    public void createPlayModetimeLine(){
    	// set up the timeline to run every 2 seconds
    	EventHandler<ActionEvent> handler = event -> swapImages();
    	KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), handler);
    	timeLine = new Timeline();
    	timeLine.setCycleCount(Timeline.INDEFINITE);
    	timeLine.getKeyFrames().add(keyFrame);
    }
    
    /**
	 * Swaps search related images provided by iTunes
	 */
    public void swapImages(){
    	debugPrint("current time "+LocalTime.now());
		// find another image to show
		if (nextImageIndex != -1 && !searching){
			//yes there are images available
			// now pick a random location from the current 0 to 19
			// n = rand.nextInt(20) + 1; // 1-20 inclusive. 
			int index = randomNumber.nextInt(20);  // 0 to 19
			
			// now swap values
			debugPrint("swaping "+index+" and "+nextImageIndex);
			Image temp = images[index]; // image to be replaced
			images[index] = images[nextImageIndex];
            images[nextImageIndex] = temp; //this image can be seen later on if randomly selected
            
            // set up next index
            nextImageIndex++;
            if (nextImageIndex  == imageCount) nextImageIndex = 20;
            setGridPane();
		}
    }

    /**
	 * Prints out custom messages during debugging
	 * @param msg			The String message you want the debugger to output
	 */
    public void debugPrint(String msg){
    	if (printDebugMsgs) System.out.println(msg);
    }
    
    /**
	 * The main method of the GUI display that holds Application.launch()
	 * @param args			A string array of arguments launched by the Application class
	 */
	public static void main(String[] args) {	try {
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

