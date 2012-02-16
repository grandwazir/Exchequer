package name.richardson.james.bukkit.exchequer;

import java.io.IOException;
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
import name.richardson.james.bukkit.utilities.internals.Logger;
import name.richardson.james.bukkit.utilities.plugin.SimplePlugin;

// TODO: Auto-generated Javadoc
/**
 * The Class Exchequer.
 */
public class Exchequer extends SimplePlugin {

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
  public void onDisable() {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * @see org.bukkit.plugin.Plugin#onEnable()
   */
  public void onEnable() {
    try {
      Logger.setDebugging(this, true);
      this.logger.setPrefix("[Exchequer] ");
      this.setResourceBundle();
      this.setupDatabase();
      this.refreshRegisteredPlayers();
      this.registerListeners();
      this.setRootPermission();
      this.registerCommands();
    } catch (SQLException e) {
      this.logger.severe(this.getMessage("unable-to-use-database"));
      this.setEnabled(false);
    } catch (IOException e) {
      this.logger.severe("Unable to close file stream!");
      this.setEnabled(false);
    } finally {
      if (!this.getServer().getPluginManager().isPluginEnabled(this)) {
        this.logger.severe(this.getMessage("panic"));
        return;
      }
    }
    this.logger.info(String.format(this.getMessage("plugin-enabled"), this.getDescription().getFullName()));

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
      this.logger.warning(this.getMessage("no-database"));
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
    commandManager = new CommandManager(this);
    this.getCommand("ex").setExecutor(commandManager);
    commandManager.addCommand(new BalanceCommand(this));
    commandManager.addCommand(new GrantCommand(this));
    commandManager.addCommand(new PayCommand(this));
    commandManager.addCommand(new SetCommand(this));
  }

}
