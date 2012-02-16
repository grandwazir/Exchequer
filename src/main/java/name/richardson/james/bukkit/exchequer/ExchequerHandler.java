package name.richardson.james.bukkit.exchequer;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import name.richardson.james.bukkit.utilities.internals.Handler;

// TODO: Auto-generated Javadoc
/**
 * The Class ExchequerHandler.
 */
public class ExchequerHandler extends Handler implements ExchequerAPI {

  /** The database. */
  private DatabaseHandler database;

  /** The inital funds provided to newly registered players. */
  private double initalFunds = 30;

  /**
   * Instantiates a new exchequer handler.
   * 
   * @param plugin a reference to the Exchequer plugin
   * @param parentClass the class of the class instantiating the new handler.
   */
  public ExchequerHandler(Exchequer plugin, Class<?> parentClass) {
    super(parentClass);
    this.database = plugin.getDatabaseHandler();
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.exchequer.ExchequerAPI#getPlayerAccounts(java
   * .lang.String)
   */
  public List<AccountRecord> getPlayerAccounts(String playerName) {
    PlayerRecord record = PlayerRecord.findByPlayerName(database, playerName);
    if (record == null)
      return Collections.emptyList();
    return record.getAccounts();
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.exchequer.ExchequerAPI#getPlayerPersonalAccount
   * (java.lang.String)
   */
  public AccountRecord getPlayerPersonalAccount(String playerName) {
    PlayerRecord record = PlayerRecord.findByPlayerName(database, playerName);
    if (record == null)
      return null;
    return record.getPersonalAccount();
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.exchequer.ExchequerAPI#isPlayerRegistered(
   * java.lang.String)
   */
  public boolean isPlayerRegistered(String playerName) {
    return PlayerRecord.isPlayerKnown(database, playerName);
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.exchequer.ExchequerAPI#registerPlayer(java
   * .lang.String)
   */
  public PlayerRecord registerPlayer(String playerName) {
    if (PlayerRecord.isPlayerKnown(database, playerName)) {
      return PlayerRecord.findByPlayerName(database, playerName);
    } else {
      PlayerRecord record = new PlayerRecord();
      record.setPlayerName(playerName);
      database.save(record);
      // create personal account
      AccountRecord account = new AccountRecord();
      account.setPersonal(true);
      account.setHidden(false);
      account.setBalance(initalFunds);
      database.save(account);
      // create the relationship
      record = PlayerRecord.findByPlayerName(database, playerName);
      account = AccountRecord.findLastCreatedAccount(database);
      record.getAccounts().add(account);
      database.save(record);
      database.save(account);
      return record;
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.exchequer.ExchequerAPI#save(java.lang.Object)
   */
  public boolean save(Object record) {
    database.save(record);
    return true;
  }

  public AccountRecord getAccount(int accountId) {
    return AccountRecord.findAccountByID(database, accountId);
  }

  public String formatAmount(double amount) {
    DecimalFormat formatter = new DecimalFormat("##,###");
    return formatter.format(amount) + " arms";
  }

}
