package name.richardson.james.bukkit.exchequer;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import name.richardson.james.bukkit.exchequer.general.BalanceCommand;
import name.richardson.james.bukkit.exchequer.general.PayCommand;
import name.richardson.james.bukkit.exchequer.management.GrantCommand;
import name.richardson.james.bukkit.exchequer.management.SetCommand;
import name.richardson.james.bukkit.utilities.command.CommandManager;
import name.richardson.james.bukkit.utilities.plugin.SkeletonPlugin;

// TODO: Auto-generated Javadoc
/**
 * The Class Exchequer.
 */
public class Exchequer extends SkeletonPlugin {

  /** The database. */
  private DatabaseHandler database;

  /** The registered players. */
  private Set<String> registeredPlayers = new HashSet<String>();

  private CommandManager commandManager;

  /*
   * (non-Javadoc)
   * @see org.bukkit.plugin.java.JavaPlugin#getDatabaseClasses()
   */
  @Override
  public List<Class<?>> getDatabaseClasses() {
    return DatabaseHandler.getDatabaseClasses();
  }

  /**
   * Gets the database handler.
   * 
   * @return the database handler
   */
  public DatabaseHandler getDatabaseHandler() {
    return this.database;
  }

  /*
   * (non-Javadoc)
   * @see org.bukkit.plugin.Plugin#onDisable()
   */

  /**
   * Register listeners.
   */
  protected void registerListeners() {
    this.getServer().getPluginManager().registerEvents(new LoginListener(this), this);
  }

  /**
   * Gets the registered players.
   * 
   * @return the registered players
   */
  public Set<String> getRegisteredPlayers() {
    return registeredPlayers;
  }

  /**
   * Refresh registered players.
   */
  private void refreshRegisteredPlayers() {
    registeredPlayers.clear();
    for (Object object : database.list(PlayerRecord.class)) {
      PlayerRecord record = (PlayerRecord) object;
      registeredPlayers.add(record.getPlayerName());
    }
  }

  /**
   * Setup database.
   * 
   * @throws SQLException the sQL exception
   */
  protected void setupPersistence() throws SQLException {
    try {
      this.getDatabase().find(AccountRecord.class).findRowCount();
    } catch (final PersistenceException ex) {
      this.logger.warning(this.getMessage("no-database"));
      this.installDDL();
    }
    this.database = new DatabaseHandler(this.getDatabase());
    this.refreshRegisteredPlayers();
  }

  /**
   * Gets the handler.
   * 
   * @param owner the owner
   * @return the handler
   */
  public ExchequerHandler getHandler(Class<?> owner) {
    return new ExchequerHandler(this, owner);
  }

  protected void registerCommands() {
    commandManager = new CommandManager(this);
    this.getCommand("ex").setExecutor(commandManager);
    commandManager.addCommand(new BalanceCommand(this));
    commandManager.addCommand(new GrantCommand(this));
    commandManager.addCommand(new PayCommand(this));
    commandManager.addCommand(new SetCommand(this));
  }

  
  public String getArtifactID() {
    return "exchequer";
  }

  public String getGroupID() {
    return "name.richardson.james.bukkit";
  }

}
