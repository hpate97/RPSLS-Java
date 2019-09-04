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

import java.util.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

class State {
    public int roundNumber = 1;
    public String p1Play = null;
    public String p2Play = null;
    public int p1Points = 0;
    public int p2Points = 0;
    public Map<String, String> choiceList = new HashMap<String, String>();

    public int checkWin() {
        if (p1Points == 3) {
            return 1;
        } else if (p2Points == 3) {
            return 2;
        }
        return 0;
    }

    public void checkRoundWinner()
    {

        /* Rock Cases */
        if (p1Play.compareTo("rock") == 0 && p2Play.compareTo("paper") == 0) {
            p2Points++;
        } else if (p1Play.compareTo("rock") == 0 && p2Play.compareTo("lizard") == 0) {
            p1Points++;
        } else if (p1Play.compareTo("rock") == 0 && p2Play.compareTo("spock") == 0) {
            p2Points++;
        } else if (p1Play.compareTo("rock") == 0 && p2Play.compareTo("scissor") == 0) {
            p1Points++;
        }

        /* Paper Cases */

        else if (p1Play.compareTo("paper") == 0 && p2Play.compareTo("lizard") == 0) {
            p2Points++;
        } else if (p1Play.compareTo("paper") == 0 && p2Play.compareTo("spock") == 0) {
            p1Points++;
        } else if (p1Play.compareTo("paper") == 0 && p2Play.compareTo("scissor") == 0) {
            p2Points++;
        } else if (p1Play.compareTo("paper") == 0 && p2Play.compareTo("rock") == 0) {
            p1Points++;
        }

        /* Paper Cases */

        else if (p1Play.compareTo("scissor") == 0 && p2Play.compareTo("lizard") == 0) {
            p1Points++;
        } else if (p1Play.compareTo("scissor") == 0 && p2Play.compareTo("spock") == 0) {
            p2Points++;
        } else if (p1Play.compareTo("scissor") == 0 && p2Play.compareTo("paper") == 0) {
            p1Points++;
        } else if (p1Play.compareTo("scissor") == 0 && p2Play.compareTo("rock") == 0) {
            p2Points++;
        }

        /* Paper Cases */

        else if (p1Play.compareTo("lizard") == 0 && p2Play.compareTo("scissor") == 0) {
            p2Points++;
        } else if (p1Play.compareTo("lizard") == 0 && p2Play.compareTo("spock") == 0) {
            p1Points++;
        } else if (p1Play.compareTo("lizard") == 0 && p2Play.compareTo("paper") == 0) {
            p1Points++;
        } else if (p1Play.compareTo("lizard") == 0 && p2Play.compareTo("rock") == 0) {
            p2Points++;
        }

        /* Paper Cases */

        else if (p1Play.compareTo("spock") == 0 && p2Play.compareTo("scissor") == 0) {
            p1Points++;
        } else if (p1Play.compareTo("spock") == 0 && p2Play.compareTo("lizard") == 0) {
            p2Points++;
        } else if (p1Play.compareTo("spock") == 0 && p2Play.compareTo("paper") == 0) {
            p2Points++;
        } else if (p1Play.compareTo("spock") == 0 && p2Play.compareTo("rock") == 0) {
            p1Points++;
        }


        roundNumber++;
    }

    public void reset() {
        this.roundNumber = 1;
        this.p1Play = null;
        this.p2Play = null;
        this.p1Points = 0;
        this.p2Points = 0;
    }
}

public class Server100 extends Application {
    private int numClients = 0;
    private TextField portField = new TextField();
    private Button onButton = new Button("Turn on");
    private Button offButton = new Button("Turn off");
    private Label numClientsLabel = new Label("0");
    private State state = new State();
    private HBox clients = new HBox();

    public Label p1Label = new Label("");
    public HBox p1playdis = new HBox();

    public Label p2Label = new Label("");
    public HBox p2playdis = new HBox();

    private Thread server = new Thread(() ->

    {
        try {
            this.startServer();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    );
    private Thread p1Thread;
    private Thread p2Thread;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage s) {
        s.setTitle("RPSLS Server");

        // port field
        HBox portBox = new HBox();
        portBox.setSpacing(20);
        portBox.getChildren().addAll(new Label("Port"), portField);

        // on off buttons
        HBox buttons = new HBox();
        onButton.setOnAction(e -> {
            server.start();
            offButton.setVisible(true);
            clients.setVisible(true);
            onButton.setVisible(false);
        });

        offButton.setOnAction(e -> {
            onButton.setVisible(true);
            clients.setVisible(false);

            // todo: stop server
            server.interrupt();
            offButton.setVisible(false);
        });
        offButton.setVisible(false);
        buttons.getChildren().addAll(onButton, offButton);
        buttons.setSpacing(20);

        // clients connected
        clients.getChildren().addAll(new Label("Clients Connected: "), numClientsLabel);
        clients.setSpacing(20);
        clients.setVisible(false);

        p1playdis.getChildren().addAll(new Label("Player1 Played: "), p1Label);
        p1playdis.setSpacing(40);
        p1playdis.setVisible(false);

//        p2playdis.getChildren().addAll(new Label("Player2 Played: "), p2Label);
//        p2playdis.setSpacing(20);
//        p2Label.setVisible(false);

        // container
        VBox vbox = new VBox();
        vbox.setSpacing(20);
        vbox.setPadding(new Insets(30, 30, 30, 30));
        vbox.getChildren().addAll(portBox, buttons, clients, p1playdis);

        Scene sc = new Scene(vbox, 500, 500);
        s.setScene(sc);
        s.show();
    }

    private void startServer() throws IOException {
        int userPort = Integer.parseInt(portField.getText());
        ServerSocket mysocket = new ServerSocket(userPort);

        while (numClients < 2) {
            Socket s = mysocket.accept();
            if (numClients == 0) {
                this.p1Thread = new Thread(new ServerThread(s, state, ++numClients));
                this.p1Thread.start();
            } else {
                this.p2Thread = new Thread(new ServerThread(s, state, ++numClients));
                this.p2Thread.start();
            }
            Platform.runLater(() -> this.numClientsLabel.setText(Integer.toString(numClients)));
        }
    }
}

class ServerThread implements Runnable {
    private Socket connection;
    private final State state;
    private int playerNumber;

    ServerThread(Socket s, State state, int playerNumber) {
        this.connection = s;
        this.state = state;
        this.playerNumber = playerNumber;
    }

    @Override
    public void run() {
        try {
            Scanner in = new Scanner(connection.getInputStream());
            PrintWriter out = new PrintWriter(connection.getOutputStream(), true);

            synchronized (state) {
                if (playerNumber == 1) {
                    out.println("status:You are the only one connected");
                    try {
                        state.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    state.notify();
                }
            }

            out.println("status:Game Started");

            while (in.hasNextLine()) {
                String line = in.nextLine();

                String[] splitMsg = line.split(":");
                if (splitMsg[0].compareTo("choice") == 0) {
                    synchronized (state) {
                        if (state.checkWin() > 0) {
                            break;
                        }


                        if (playerNumber == 1) {
                            state.p1Play = splitMsg[1];

                            //Platform.runLater(() -> .setText(state.p1Play));


                            String player1choice = "Player1:" + state.p1Play;
                            state.choiceList.put("Player1", new String(player1choice));
                            //System.out.println(state.choiceList.get("Player1"));

                            if (state.p2Play == null) {
                                out.println("status:Waiting for opponent to play");
                                try {
                                    state.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                state.notify();
                                this.checkWinner();
                            }
                        } else if (playerNumber == 2) {
                            state.p2Play = splitMsg[1];

                            String player2choice = "Player2:" + state.p2Play;
                            state.choiceList.put("Player2", new String(player2choice));
                            //System.out.println(state.choiceList.get("Player2"));


                            if (state.p1Play == null) {
                                out.println("status:Waiting for opponent to play");
                                try {
                                    state.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                state.notify();
                                this.checkWinner();
                            }
                        }

                        int winner = state.checkWin();

                        if (playerNumber == 1) {
                            out.println("mypoints:" + Integer.toString(state.p1Points));
                            out.println("oppoints:" + Integer.toString(state.p2Points));
                            System.out.println(state.choiceList.get("Player1"));
                            if (winner == 1) {
                                out.println("status:" + "You win!");
                            } else if (winner == 2) {
                                out.println("status:" + "You lose");
                            }
                        } else {
                            out.println("mypoints:" + Integer.toString(state.p2Points));
                            out.println("oppoints:" + Integer.toString(state.p1Points));
                            System.out.println(state.choiceList.get("Player2"));
                            if (winner == 2) {
                                out.println("status:" + "You win!");
                            } else if (winner == 1) {
                                out.println("status:" + "You lose");
                            }
                        }

                        if (winner == 0) {
                            out.println("status:Play a choice");
                        }

                    }
                }
            }
        } catch (IOException e) {

        }
    }

    public void checkWinner()
    {
        state.checkRoundWinner();

        state.p1Play = null;
        state.p2Play = null;
    }
}