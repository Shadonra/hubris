package com.bpodgursky.hubris;

import com.bpodgursky.hubris.account.GameMeta;
import com.bpodgursky.hubris.client.ClientSettings;
import com.bpodgursky.hubris.client.CommandFactory;
import com.bpodgursky.hubris.client.GameManager;
import com.bpodgursky.hubris.command.GetState;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.connection.RemoteConnection;
import com.bpodgursky.hubris.event.StateProcessor;
import com.bpodgursky.hubris.listeners.PrintNewCash;
import com.bpodgursky.hubris.listeners.PrintResearchChange;
import com.bpodgursky.hubris.listeners.PrintUpgrade;
import com.bpodgursky.hubris.listeners.SpendOnIncomeListener;
import com.bpodgursky.hubris.transfer.NpHttpClient;
import com.bpodgursky.hubris.universe.GameState;
import jline.console.ConsoleReader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

public class HubrisProcessLoop {

  public static void main(String[] args) throws Exception {

    if (args.length != 1) {
      System.err.println("Syntax is: GameManager <settings_file.yml>");
      System.exit(1);
    }

    ClientSettings settings = ClientSettings.loadFromYaml(args[0]);
    String cookies = settings.getCookies();
    NpHttpClient client = new NpHttpClient(cookies);
    GameManager manager = new GameManager(client);
    ConsoleReader reader = new ConsoleReader();

    System.out.println(" ---- ACTIVE GAMES ---- ");
    List<GameMeta> games = manager.getActiveGames();
    for (int i = 0; i < games.size(); i++) {
      GameMeta game = games.get(i);
      System.out.printf("%2d. %s\n", i, game.getName());
    }
    int gameNumber = Integer.parseInt(reader.readLine("Enter game: "));
    GameMeta game = games.get(gameNumber);
    long gameId = game.getId();
    String npUsername = settings.getNpUsername();

    GameConnection connection = new RemoteConnection(cookies);
    int player = HubrisUtil.getPlayerNumber(connection, npUsername, gameId);

    CommandFactory factory = new CommandFactory(npUsername, gameId, player);
    StateProcessor processsor = new StateProcessor(connection, factory);

    processsor.addEventListener(new PrintNewCash());
    processsor.addEventListener(new PrintUpgrade());
    processsor.addEventListener(new PrintResearchChange());
    processsor.addEventListener(new SpendOnIncomeListener(150, 1.0, 1.0, .5));

    GameState currentState = null;

    while(true){
      currentState = connection.getState(currentState, new GetState(player, npUsername, gameId));
      processsor.update(currentState);
      File stateFile = new File(game.getId() + ".js");
      FileUtils.writeStringToFile(stateFile, "var data = " + currentState.toString() + ";");

      Thread.sleep(5000);

      connection.submit(factory.sendCash(3, 1));

      Thread.sleep(1000*60*5);
    }
  }
}
