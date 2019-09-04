import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client100 extends Application {
    private TextField ipField = new TextField();
    private TextField portField = new TextField();

    private Button connectButton = new Button("Connect");

    private Label status = new Label("Not Connected");
    private Label myScore = new Label("0");
    private Label opponentScore = new Label("0");

    private VBox container = new VBox();

    private HBox scores = new HBox();
    private HBox choices = new HBox();
    private HBox actions = new HBox();

    private Button rock = new Button("Rock");
    private Button paper = new Button("Paper");
    private Button scissor = new Button("Scissor");
    private Button lizard = new Button("Lizard");
    private Button spock = new Button("Spock");
    private Button playAgain = new Button("Play Again");
    private Button quit = new Button("Quit");

    private Client client;
    private Thread clientThread = new Thread(() ->
    {
        try {
            client.startClient();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    );

    class Client {
        private String ip;
        private int port;
        private PrintWriter out;

        Client(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        void onFinish(PrintWriter out, Socket s) {
            Platform.runLater(() -> {
                playAgain.setOnAction(e -> {
                    out.println("action:playagain");
                    actions.getChildren().remove(0, 2);


                });

                quit.setOnAction(e -> {
                    actions.getChildren().remove(0, 2);
                    myScore.setText("0");
                    myScore.setText("0");
                    status.setText("Not Connected");
                    container.getChildren().remove(4, 7);
                    connectButton.setVisible(true);
                    try {
                        s.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });

                actions.setSpacing(30);
                actions.getChildren().addAll(playAgain, quit);
            });
        }

        void startClient() throws IOException {
            Socket socketClient = new Socket(ip, port);

            System.out.println("Client: connection established");

            Platform.runLater(() -> {
                container.getChildren().addAll(
                        scores,
                        choices,
                        actions
                );

                connectButton.setVisible(false);
            });

            Scanner in = new Scanner(socketClient.getInputStream());
            out = new PrintWriter(socketClient.getOutputStream(), true);

            while (in.hasNextLine()) {
                String recv = in.nextLine();
                System.out.println(recv);

                String[] splitMsg = recv.split(":");
                if (splitMsg[0].compareTo("status") == 0) {
                    Platform.runLater(() -> status.setText(splitMsg[1]));
                    if (splitMsg[1].compareTo("You win!") == 0 || splitMsg[1].compareTo("You lose") == 0) {
                        this.onFinish(out, socketClient);
                    }
                }

                if (splitMsg[0].compareTo("mypoints") == 0) {
                    Platform.runLater(() -> myScore.setText(splitMsg[1]));
                }

                if (splitMsg[0].compareTo("oppoints") == 0) {
                    Platform.runLater(() -> opponentScore.setText(splitMsg[1]));
                }
            }
        }

        void sendChoice(String choice) {
            out.println("choice:" + choice);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage s) {
        s.setTitle("RPSLS Client");

        // ip field
        HBox ipBox = new HBox();
        ipBox.setSpacing(20);
        ipBox.getChildren().addAll(new Label("IP"), ipField);

        // port field
        HBox portBox = new HBox();
        portBox.setSpacing(20);
        portBox.getChildren().addAll(new Label("Port"), portField);

        // connect button
        connectButton.setOnAction(e -> {
            client = new Client(ipField.getText(), Integer.parseInt(portField.getText()));
            clientThread.start();
        });

        // status
        HBox statusBox = new HBox();
        statusBox.getChildren().addAll(new Label("Status: "), status);

        // scores
        HBox myScoreBox = new HBox();
        HBox opponentScoreBox = new HBox();
        myScoreBox.getChildren().addAll(new Label("My Score: "), myScore);
        opponentScoreBox.getChildren().addAll(new Label("Opponent Score: "), opponentScore);
        scores.getChildren().addAll(myScoreBox, opponentScoreBox);
        scores.setSpacing(30);

        //pictures on buttons
        Image rockpic = new Image("rock.jpg");
        ImageView rp = new ImageView(rockpic);
        rp.setFitHeight(50);
        rp.setFitWidth(50);
        //rp.setPreserveRatio(true);
        rock.setGraphic(rp);

        Image paperpic = new Image("paper.jpeg");
        ImageView pp = new ImageView(paperpic);
        pp.setFitHeight(50);
        pp.setFitWidth(50);
        //pp.setPreserveRatio(true);
        paper.setGraphic(pp);

        Image scissorpic = new Image("scissor.png");
        ImageView sp = new ImageView(scissorpic);
        sp.setFitHeight(50);
        sp.setFitWidth(50);
        //sp.setPreserveRatio(true);
        scissor.setGraphic(sp);

        Image lizardpic = new Image("lizard.png");
        ImageView lp = new ImageView(lizardpic);
        lp.setFitHeight(50);
        lp.setFitWidth(50);
        //lp.setPreserveRatio(true);
        lizard.setGraphic(lp);

        Image spockpic = new Image("spock.png");
        ImageView spp = new ImageView(spockpic);
        spp.setFitHeight(50);
        spp.setFitWidth(50);
        //spp.setPreserveRatio(true);
        spock.setGraphic(spp);


        // choices
        choices.setSpacing(20);
        choices.getChildren().addAll(rock, paper, scissor, lizard, spock);
        rock.setOnAction(e -> client.sendChoice("rock"));
        paper.setOnAction(e -> client.sendChoice("paper"));
        scissor.setOnAction(e -> client.sendChoice("scissor"));
        lizard.setOnAction(e -> client.sendChoice("lizard"));
        spock.setOnAction(e -> client.sendChoice("spock"));

        // container
        container.setSpacing(20);
        container.setPadding(new Insets(30, 30, 30, 30));
        container.getChildren().addAll(
                ipBox,
                portBox,
                connectButton,
                statusBox
        );

        Scene sc = new Scene(container, 500, 500);
        s.setScene(sc);
        s.show();
    }
}