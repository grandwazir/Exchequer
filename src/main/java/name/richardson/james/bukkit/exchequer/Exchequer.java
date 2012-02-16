package name.richardson.james.bukkit.exchequer;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import name.richardson.james.bukkit.exchequer.DatabaseHandler;
import name.richardson.james.bukkit.exchequer.general.PayCommand;
import name.richardson.james.bukkit.exchequer.management.GrantCommand;
import name.richardson.james.bukkit.exchequer.management.SetCommand;
import name.richardson.james.bukkit.util.Plugin;
import name.richardson.james.bukkit.util.command.CommandManager;


// TODO: Auto-generated Javadoc
/**
 * The Class Exchequer.
 */
public class Exchequer extends Plugin {

  /** The database. */
  private DatabaseHandler database;
  
  /** The registered players. */
  private Set<String> registeredPlayers = new HashSet<String>();

  private CommandManager commandManager;
  
  /* (non-Javadoc)
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

  /* (non-Javadoc)
   * @see org.bukkit.plugin.Plugin#onDisable()
   */
  public void onDisable() {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see org.bukkit.plugin.Plugin#onEnable()
   */
  public void onEnable() {
    try {
      this.logger.setPrefix("[Exchequer] ");
      this.setupDatabase();
      this.refreshRegisteredPlayers();
      this.registerListeners();
      this.setPermission();
      this.registerCommands();
    } catch (SQLException e) {
      this.logger.severe("Error initalising database!");
      e.printStackTrace();
      this.setEnabled(false);
    } finally {
      if (!this.getServer().getPluginManager().isPluginEnabled(this)) {
        return;
      }
    }
    this.logger.info(String.format("%s is enabled.", this.getDescription().getFullName()));

  }
  
  /**
   * Register listeners.
   */
  private void registerListeners() {
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
  private void setupDatabase() throws SQLException {
    try {
      this.getDatabase().find(AccountRecord.class).findRowCount();
    } catch (final PersistenceException ex) {
      this.logger.warning("No database schema found. Generating a new one.");
      this.installDDL();
    }
    this.database = new DatabaseHandler(this.getDatabase());
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
  
  private void registerCommands() {
    commandManager = new CommandManager(this.getDescription());
    this.getCommand("ex").setExecutor(commandManager);
    commandManager.registerCommand("grant", new GrantCommand(this));
    commandManager.registerCommand("pay", new PayCommand(this));
    commandManager.registerCommand("set", new SetCommand(this));
  }
    

}
