package name.richardson.james.bukkit.exchequer;

import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginListener implements Listener {

  /** The handler used to access Exchequer API. */
  private ExchequerHandler handler;
  
  /** The names of the players currently registered. */
  private Set<String> players;
  
  /**
   * Instantiates a new login listener.
   *
   * @param plugin a reference to the Exchequer plugin
   */
  public LoginListener(Exchequer plugin) {
    this.handler = plugin.getHandler(LoginListener.class);
    this.players = plugin.getRegisteredPlayers();
  }
  
  /**
   * On player join.
   * 
   * When a player joins the server, check to see if they are currently registered with Exchequer. If they are not create a new PlayerRecord and AccountRecord for this player.
   *
   * @param event the event
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(final PlayerJoinEvent event) {
    final String playerName = event.getPlayer().getName();
    if (!players.contains(event.getPlayer().getName().toLowerCase())) {
      handler.registerPlayer(playerName);
      this.players.add(playerName.toLowerCase());
    }
  }
  
}
