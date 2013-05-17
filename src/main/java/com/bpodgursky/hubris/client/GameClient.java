package com.bpodgursky.hubris.client;

import com.bpodgursky.hubris.command.*;
import com.bpodgursky.hubris.connection.GameConnection;
import com.bpodgursky.hubris.connection.RemoteConnection;
import com.bpodgursky.hubris.event.GameEvent;
import com.bpodgursky.hubris.event.Message;
import com.bpodgursky.hubris.universe.Comment;
import com.bpodgursky.hubris.universe.GameState;
import com.bpodgursky.hubris.universe.GameStateDelta;
import com.bpodgursky.hubris.universe.TechType;

import javax.xml.transform.TransformerFactoryConfigurationError;
import java.util.List;

public class GameClient {

  private final String userName;
  private final Integer playerNumber;
  private final Long gameNumber;
  private final GameConnection connection;

  public GameClient(String userName, Integer playerNumber, Long gameNumber, GameConnection connection) throws Exception {

    this.userName = userName;
    this.playerNumber = playerNumber;
    this.gameNumber = gameNumber;
    this.connection = connection;

  }

  public void sendTech(TechType tech, Integer to) throws Exception {
    send(new SendTech(playerNumber, userName, gameNumber, tech.getGameId(), to));
  }

  public void sendMessage(String subject, String body, List<Integer> to) throws Exception {
    send(new SendMessage(playerNumber, userName, gameNumber, subject, body, to));
  }

  public void sendMessageComment(String key, String body) throws Exception {
    send(new SendMessageComment(playerNumber, userName, gameNumber, key, body));
  }

  public List<GameEvent> getEvents(Integer offset, Integer number) throws Exception {
    return send(new GetEvents(playerNumber, userName, gameNumber, offset, number));
  }

  public List<Comment> getMessageComments(String messageKey, Integer number, Integer offset) throws Exception {
    return send(new GetMessageComments(playerNumber, userName, gameNumber, messageKey, offset, number));
  }

  public List<Message> getMessages(Integer offset, Integer number) throws Exception {
    return send(new GetMessages(playerNumber, userName, gameNumber, number, offset));
  }

  public GameStateDelta sendCash(Integer destination, Integer amount) throws TransformerFactoryConfigurationError, Exception {
    return send(new SendCash(playerNumber, userName, gameNumber, destination, amount));
  }

  public GameStateDelta transferShips(Integer id1, Integer id2, Integer loc1Final, Integer loc2Final) throws Exception {
    return send(new TransferShips(playerNumber, userName, gameNumber, id1, id2, loc1Final, loc2Final));
  }

  public GameStateDelta createCarrier(Integer star, Integer strength) throws TransformerFactoryConfigurationError, Exception {
    return send(new CreateCarrier(playerNumber, userName, gameNumber, star, strength));
  }

  public GameStateDelta clearAllFleetPaths(Integer fleet) throws Exception {
    return send(new ClearAllFleetPaths(playerNumber, userName, gameNumber, fleet));
  }

  public GameStateDelta clearFleetLastPath(Integer fleet) throws TransformerFactoryConfigurationError, Exception {
    return send(new ClearFleetLastPath(playerNumber, userName, gameNumber, fleet));
  }

  public GameStateDelta setWaypoint(Integer fleet, Integer star) throws TransformerFactoryConfigurationError, Exception {
    return send(new SetWaypoint(playerNumber, userName, gameNumber, fleet, star));
  }

  public GameStateDelta setGarrison(Integer star, Integer size) throws TransformerFactoryConfigurationError, Exception {
    return send(new SetGarrison(playerNumber, userName, gameNumber, star, size));
  }

  public GameStateDelta buyEconomy(Integer star) throws Exception {
    return send(new UpgradeEconomy(playerNumber, userName, gameNumber, star));
  }

  public GameStateDelta buyIndustry(Integer star) throws Exception {
    return send(new UpgradeIndustry(playerNumber, userName, gameNumber, star));
  }

  public GameStateDelta buyScience(Integer star) throws Exception {
    return send(new UpgradeScience(playerNumber, userName, gameNumber, star));
  }

  public GameStateDelta setNextResearch(String researchName) throws TransformerFactoryConfigurationError, Exception {
    return send(new SetNextResearch(playerNumber, userName, gameNumber, researchName));
  }

  public GameStateDelta setResearch(TechType research) throws Exception {
    return send(new SetResearch(playerNumber, userName, gameNumber, research.getGameId()));
  }

  public <R> R send(GameRequest<R> request) throws Exception {
    return connection.sendRequest(request);
  }

  public GameState getState() throws TransformerFactoryConfigurationError, Exception {
    return send(new GetState(playerNumber, userName, gameNumber));
  }

  public static void main(String[] args) throws Exception {

    GameClient connection = new GameClient("rapleaf.np.test@gmail.com", 6, 28326395l, new RemoteConnection("rapleaf.np.test@gmail.com", "rapleaf_np"));

    GameState state = connection.getState();
    System.out.println(state);

    state.writeGnuPlot("current_state");

//		connection.sendTech(TechType.WEAPONS, 4);

//		connection.setResearch(TechType.RANGE);

//		connection.setNextResearch("fleet_speed");

//		connection.buyScience(80);

//		connection.buyIndustry(170);

//		connection.clearAllFleetPaths(276);

//		connection.clearFleetLastPath(276);

//		connection.setWaypoint(276, 168);

//		connection.setGarrison(118, 1);

//		connection.buyEconomy(166);

//		System.out.println(connection.sendCash(7, 1));

//		connection.transferShips(14, 177, 591, 1);

//		connection.createCarrier(180, 1);

//		connection.sendMessageComment("ag1uZXB0dW5lc3ByaWRlchULEgxHYW1lX01lc3NhZ2UY5rLGDQw", "bump");

  }

}
