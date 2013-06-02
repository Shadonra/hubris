package com.bpodgursky.hubris.client;

import com.bpodgursky.hubris.HubrisUtil;
import com.bpodgursky.hubris.account.GameMeta;
import com.bpodgursky.hubris.command.GetState;
import com.bpodgursky.hubris.common.HubrisConstants;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.connection.RemoteConnection;
import com.bpodgursky.hubris.helpers.ExploreHelper;
import com.bpodgursky.hubris.helpers.FleetHelper;
import com.bpodgursky.hubris.plan.Order;
import com.bpodgursky.hubris.plan.Plan;
import com.bpodgursky.hubris.plan.orders.BalanceFleets;
import com.bpodgursky.hubris.plan.orders.FleetDistPlan;
import com.bpodgursky.hubris.transfer.NpHttpClient;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.Star;
import jline.console.ConsoleReader;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameManager {

  static {
    HubrisUtil.startLogging();
  }

  private final NpHttpClient client;

  private static final Pattern GAME_PATTERN
      = Pattern.compile("href='/game[?]game=([^']+)'>([^<]+)<");

  public GameManager(NpHttpClient client) {
    this.client = client;
  }

  /**
   * Requests a list of games this user is currently a member of.
   *
   * @return
   */
  public List<GameMeta> getActiveGames() {
    String source = client.get(HubrisConstants.accountHomeUrl);
    Matcher matcher = GAME_PATTERN.matcher(source);

    // TODO: make sure we're getting back the page we expect.
    if (!matcher.find()) {
      return Collections.emptyList();
    }

    List<GameMeta> games = new ArrayList<GameMeta>();

    do {
      String name = matcher.group(2);
      Integer id;
      try {
        id = Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException e) {
        throw new RuntimeException("Expected game ID (" + matcher.group(1) + ") to be an integer!");
      }

      games.add(new GameMeta(name, id));
    } while (matcher.find());

    return games;
  }

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

    while (true) {
      System.out.println(" ---- ACTIVE GAMES ---- ");
      List<GameMeta> games = manager.getActiveGames();
      for (int i = 0; i < games.size(); i++) {
        GameMeta game = games.get(i);
        System.out.printf("%2d. %s\n", i, game.getName());
      }
      GameMeta game = games.get(Integer.parseInt(reader.readLine("Enter game: ")));

      GameConnection connection = new RemoteConnection(cookies);
      String npUsername = settings.getNpUsername();
      long id = game.getId();
      int player = HubrisUtil.getPlayerNumber(connection, npUsername, id);

      CommandFactory factory = new CommandFactory(npUsername, id, player);
      GameState state = connection.getState(null, factory.getState());

      Plan plan = new Plan(factory, connection);

      Collection<Order> orders = ExploreHelper.planExplore(FleetHelper.getIdleFleets(state), state, 5.0);

      plan.schedule(orders);

      for (Order order : orders) {
        System.out.println();
        System.out.println("------ ");
        System.out.println();

        Order head = order;
        while (head != null) {
          System.out.println(head);
          System.out.println();

          Iterator<Order> iterator = head.getPrereqs().iterator();
          if (iterator.hasNext()) {
            head = head.getPrereqs().iterator().next();
          } else {
            head = null;
          }
        }
      }


      GameState currentState = null;

      while (true) {
        try {
          currentState = connection.getState(currentState, new GetState(player, npUsername, id));
          plan.tick(currentState);
        } catch (Exception e) {
          e.printStackTrace();
        }

        Thread.sleep(20000);
      }


    }
  }
}
