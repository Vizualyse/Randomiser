/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smite.god.list;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author mykha
 */
public class SmiteGodList extends Application {
    Elements godList = new Elements();
    ObservableList<String> gods = FXCollections.observableArrayList();
    ArrayList<String> godType = new ArrayList();
    ArrayList<String> physRules = new ArrayList();
    ArrayList<String> magRules = new ArrayList();
    
    BorderPane root = new BorderPane();
    StackPane topStack = new StackPane();
    VBox topBox = new VBox(5);
    ComboBox list;
    Stage modifyRulesStage = new Stage();
    
    private static double xOffset = 0;
    private static double yOffset = 0;
    
    @Override
    public void start(Stage primaryStage) {
        try{
        Image img = new Image(new FileInputStream("Rexsi Logo.png"));
        ImageView imgView = new ImageView(img);
        imgView.setFitWidth(600);
        imgView.setFitHeight(158);
        topStack.getChildren().add(imgView);
        topBox.getChildren().add(topStack);
        }catch(Exception e){}
        
        godsDropdown();
        topBox.setAlignment(Pos.CENTER);
        root.setTop(topBox);
        updateImage();
        updateRules();
        randomise();
        modifyRules();
        
        Button rules = new Button("Modify Rules");
        rules.setFont(Font.font("arial", 16));
        rules.setTextFill(Color.web("#f9e294"));
        rules.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(10), Insets.EMPTY)));
        rules.setOnAction((event)->{
            modifyRulesStage.show();
        });
        topBox.getChildren().add(rules);
        
        Button exit = new Button("X");
        exit.setFont(Font.font("tahoma", 10));
        exit.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        exit.setTextFill(Color.RED);
        exit.setTranslateY(-69);
        exit.setTranslateX(290);
        exit.setOnAction((event)->{primaryStage.close();});
        topStack.getChildren().add(exit);
        
        Scene scene = new Scene(root, 600, 1000);
        
        root.setBackground(new Background(new BackgroundFill(Color.web("#f9e294"), CornerRadii.EMPTY, Insets.EMPTY)));
        primaryStage.setTitle("Rexsi's Randomiser");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        root.setOnMousePressed((event)-> {
            xOffset = primaryStage.getX() - event.getScreenX();
            yOffset = primaryStage.getY() - event.getScreenY();
        });
        
        root.setOnMouseDragged((event)-> {
            primaryStage.setX(event.getScreenX() + xOffset);
            primaryStage.setY(event.getScreenY() + yOffset);
            
        });
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void updateRules(){
        String rules = "";
        try{
            BufferedReader br = new BufferedReader(new FileReader("rules.txt"));
            try {
                String line = br.readLine();
                while (line != null) {
                    if(line.charAt(1) == 'P'){
                        physRules.add(line.split("]")[1]);
                    }else{
                        if(line.charAt(1) == 'M'){
                            magRules.add(line.split("]")[1]);
                        }
                    }
                    line = br.readLine();
                }
            } finally {
                br.close();
            }
        }catch(Exception e){} 
    }
    
    public void modifyRules(){
        ObservableList<String> selectedRules = FXCollections.observableArrayList(physRules);
        
        BorderPane rulePane = new BorderPane();
        Scene ruleScene = new Scene(rulePane, 300, 400);
        
        rulePane.setBackground(new Background(new BackgroundFill(Color.web("#f9e294"), CornerRadii.EMPTY, Insets.EMPTY)));
        modifyRulesStage.initStyle(StageStyle.UNDECORATED);
        rulePane.setOnMousePressed((event)-> {
            xOffset = modifyRulesStage.getX() - event.getScreenX();
            yOffset = modifyRulesStage.getY() - event.getScreenY();
        });
        
        rulePane.setOnMouseDragged((event)-> {
            modifyRulesStage.setX(event.getScreenX() + xOffset);
            modifyRulesStage.setY(event.getScreenY() + yOffset);
            
        });
        
        Button exit = new Button("X");
        exit.setFont(Font.font("tahoma", 10));
        exit.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        exit.setTextFill(Color.RED);
        exit.setTranslateX(280);
        exit.setOnAction((event)->{modifyRulesStage.hide();});
        rulePane.setTop(exit);
        
        VBox rules = new VBox(5);
        rules.setAlignment(Pos.TOP_CENTER);
        ComboBox ruleBox = new ComboBox();
        ruleBox.getItems().addAll("Physical", "Magical");
        ruleBox.getSelectionModel().selectFirst();
        
        ruleBox.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(10), Insets.EMPTY)));
        ruleBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                @Override public ListCell<String> call(ListView<String> param) {
                    final ListCell<String> cell = new ListCell<String>() {
                        {
                            super.setPrefWidth(100);
                        }    
                        @Override public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if(item!=null){
                                    setText(item.toString());
                                    setStyle("-fx-text-fill: #f9e294;-fx-font-family: arial;-fx-font-size: 16px;-fx-background-color: black;");
                                } else {
                                    setStyle("-fx-text-fill: #f9e294;-fx-font-family: arial;-fx-font-size: 16px;-fx-background-color: black;"); 
                                }
                            }
                        };
                return cell;
            }
        });
        ruleBox.setButtonCell(new ListCell(){
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty); 
                if(item!=null){
                    setText(item.toString());
                    setStyle("-fx-text-fill: #f9e294;-fx-font-family: arial;-fx-font-size: 16px;");
                } else {
                    setStyle("-fx-text-fill: #f9e294;-fx-font-family: arial;-fx-font-size: 16px;"); 
                }
            }

        });
        
        
        Label currentRules = new Label();
        currentRules.setStyle("-fx-text-fill: black;-fx-font-family: arial;-fx-font-size: 16px;");
        
        ruleBox.setOnAction((event)->{
            if(ruleBox.getSelectionModel().getSelectedIndex() == 0){
                String currentRulesStr = "";
                for(String s: physRules){
                    currentRulesStr += s + "\n";
                }
                currentRules.setText(currentRulesStr);
                selectedRules.clear();
                selectedRules.addAll(physRules);
            }else{
                String currentRulesStr = "";
                for(String s: magRules){
                    currentRulesStr += s + "\n";
                }
                currentRules.setText(currentRulesStr);
                selectedRules.clear();
                selectedRules.addAll(magRules);
            }
        });
        
        ComboBox removeBox = new ComboBox(selectedRules);
        removeBox.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(10), Insets.EMPTY)));
        removeBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                @Override public ListCell<String> call(ListView<String> param) {
                    final ListCell<String> cell = new ListCell<String>() {
                        {
                            super.setPrefWidth(100);
                        }    
                        @Override public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if(item!=null){
                                    setText(item.toString());
                                    setStyle("-fx-text-fill: #f9e294;-fx-font-family: arial;-fx-font-size: 16px;-fx-background-color: black;");
                                } else {
                                    setStyle("-fx-text-fill: #f9e294;-fx-font-family: arial;-fx-font-size: 16px;-fx-background-color: black;"); 
                                }
                            }
                        };
                return cell;
            }
        });
        removeBox.setButtonCell(new ListCell(){
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty); 
                if(item!=null){
                    setText(item.toString());
                    setStyle("-fx-text-fill: #f9e294;-fx-font-family: arial;-fx-font-size: 16px;");
                } else {
                    setStyle("-fx-text-fill: #f9e294;-fx-font-family: arial;-fx-font-size: 16px;"); 
                }
            }

        });
        
        Button removeButton = new Button("Remove");
        removeButton.setFont(Font.font("arial", 16));
        removeButton.setTextFill(Color.web("#f9e294"));
        removeButton.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(10), Insets.EMPTY)));
        removeButton.setOnAction((event)->{
            if(ruleBox.getSelectionModel().getSelectedIndex() == 0){
                physRules.remove(removeBox.getSelectionModel().getSelectedIndex());
                String currentRulesStr = "";
                for(String s: physRules){
                    currentRulesStr += s + "\n";
                }
                currentRules.setText(currentRulesStr);
                selectedRules.clear();
                selectedRules.addAll(physRules);
            }else{
                magRules.remove(removeBox.getSelectionModel().getSelectedIndex());
                String currentRulesStr = "";
                for(String s: magRules){
                    currentRulesStr += s + "\n";
                }
                currentRules.setText(currentRulesStr);
                selectedRules.clear();
                selectedRules.addAll(magRules);
            }
        });
        
        TextField ruleEntry = new TextField();
        ruleEntry.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(10), Insets.EMPTY)));
        ruleEntry.setStyle("-fx-text-fill: #f9e294;-fx-font-family: arial;-fx-font-size: 16px;");      
        ruleEntry.setOnKeyPressed((event)->{
            String rule = "";
            if(event.getCode().equals(KeyCode.ENTER)){
                if(ruleBox.getSelectionModel().getSelectedIndex() == 0){
                    physRules.add(ruleEntry.getText());
                }else{
                    magRules.add(ruleEntry.getText());
                    
                }
            }
            try {
                BufferedWriter writer;
                writer = new BufferedWriter(new FileWriter("rules.txt"));
                for(String s: physRules){
                    rule += "[Phsyical]" + s + "\n";
                }
                for(String s: magRules){
                    rule += "[Magical]" + s + "\n";
                }
                writer.write(rule);
                    
            writer.close();
            } catch (IOException ex) {
                Logger.getLogger(SmiteGodList.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(ruleBox.getSelectionModel().getSelectedIndex() == 0){
                String currentRulesStr = "";
                for(String s: physRules){
                    currentRulesStr += s + "\n";
                }
                currentRules.setText(currentRulesStr);
                selectedRules.clear();
                selectedRules.addAll(physRules);
            }else{
                String currentRulesStr = "";
                for(String s: magRules){
                    currentRulesStr += s + "\n";
                }
                currentRules.setText(currentRulesStr);
                selectedRules.clear();
                selectedRules.addAll(magRules);
            }
        });

        String currentRulesStr = "";
        for(String s: physRules){
            currentRulesStr += s + "\n";
        }

        currentRules.setText(currentRulesStr);
        
        HBox remove = new HBox(5);
        remove.setAlignment(Pos.CENTER);
        remove.getChildren().addAll(removeBox, removeButton);

        rules.getChildren().addAll(ruleBox, ruleEntry, currentRules, remove);
        rulePane.setCenter(rules);
        modifyRulesStage.setScene(ruleScene);
        
    }
    
    public void randomise(){
        Button btn = new Button("Randomise");
        btn.setFont(Font.font("arial", 16));
        btn.setTextFill(Color.web("#f9e294"));
        btn.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(10), Insets.EMPTY)));
        btn.setOnAction((event)->{
            Random rand = new Random();
            list.getSelectionModel().select(rand.nextInt(godList.size()));
            updateImage();
            Label l = new Label();
            l.setStyle("-fx-text-fill: black;-fx-font-family: arial;-fx-font-size: 32px;");
            if(godType.get(list.getSelectionModel().getSelectedIndex()).equals("Physical")){
                if(physRules.size()>0){
                    l.setText(physRules.get(rand.nextInt(physRules.size())));
                }
            }else{
                if(magRules.size()>0){
                    l.setText(magRules.get(rand.nextInt(magRules.size())));
                }
            }
            TilePane tile = new TilePane();
            tile.setAlignment(Pos.CENTER);
            tile.getChildren().add(l);
            tile.setTranslateY(-20);
            root.setBottom(tile);
        });
        
        topBox.getChildren().add(btn);
    }
    
    public void godsDropdown(){
        updateGodList();
        
        for(Element e: godList){
            gods.add(e.getElementsByClass("category-page__member-link").get(0).ownText());
        }
        list = new ComboBox(gods);
        list.getSelectionModel().selectFirst();
        
        list.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(10), Insets.EMPTY)));
        list.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                @Override public ListCell<String> call(ListView<String> param) {
                    final ListCell<String> cell = new ListCell<String>() {
                        {
                            super.setPrefWidth(100);
                        }    
                        @Override public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if(item!=null){
                                    setText(item.toString());
                                    setStyle("-fx-text-fill: #f9e294;-fx-font-family: arial;-fx-font-size: 16px;-fx-background-color: black;");
                                } else {
                                    setStyle("-fx-text-fill: #f9e294;-fx-font-family: arial;-fx-font-size: 16px;-fx-background-color: black;"); 
                                }
                            }
                        };
                return cell;
            }
        });
        list.setButtonCell(new ListCell(){
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty); 
                if(item!=null){
                    setText(item.toString());
                    setStyle("-fx-text-fill: #f9e294;-fx-font-family: arial;-fx-font-size: 16px;");
                } else {
                    setStyle("-fx-text-fill: #f9e294;-fx-font-family: arial;-fx-font-size: 16px;"); 
                }
            }

        });
        
        list.setOnAction((event) ->{
            updateImage();
        });
        
        
        topBox.getChildren().add(list);
    }
    
    public void updateImage(){
        Element e = godList.get(list.getSelectionModel().getSelectedIndex()).getElementsByClass("category-page__member-left").get(0);
        String img = e.select("img").first().absUrl("data-src");
        String url = img.split("rev")[0];

        Image image = new Image(url);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(600);
        imageView.setFitWidth(450);
        root.setCenter(imageView);
        
    }
    
    public void updateGodList(){
        String url = "https://smite.fandom.com/wiki/Category:Gods";
        try {
            Document doc = Jsoup.connect(url).get();
            for(Element e: doc.getElementsByClass("category-page__member")){
                godList.add(e);
                for(Element f: Jsoup.connect(e.getElementsByClass("category-page__member-link").get(0).absUrl("href")).get().getElementsByClass("pi-data-value pi-font").get(0).getElementsByAttribute("title")){
                    if(f.attr("title").equals("Magical") || f.attr("title").equals("Physical")){
                        godType.add(f.attr("title"));
                    }
                }  
            }     
        } catch (IOException ex) {
            Logger.getLogger(SmiteGodList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
