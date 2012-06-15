package name.richardson.james.bukkit.exchequer;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

import name.richardson.james.bukkit.utilities.database.Database;
import name.richardson.james.bukkit.utilities.internals.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class PlayerRecord.
 */
@Entity
@Table(name = "exchequer_players")
public class PlayerRecord {

  /** The logger for this class. */
  private static Logger logger = new Logger(PlayerRecord.class);

  /** The id (primary key) of this PlayerRecord. */
  @Id
  private int id;

  /** The name of the player. */
  @NotNull
  private String playerName;

  /** The accounts that the player is a signatory to. */
  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "exchequer_players_accounts")
  private List<AccountRecord> accounts;

  /**
   * Gets the accounts that the player is a signatory to.
   * 
   * @return available accounts
   */
  public List<AccountRecord> getAccounts() {
    return accounts;
  }

  /**
   * Sets the accounts that the player is a signatory to.
   * 
   * @param accounts accounts
   */
  public void setAccounts(List<AccountRecord> accounts) {
    this.accounts = accounts;
  }

  /**
   * Check if the player has a certain amount of funds.
   * 
   * This check will use the balance of all available accounts.
   * 
   * @param amount the amount
   * @return true, if successful
   */
  public boolean contains(BigDecimal amount) {
    if (this.getBalance().compareTo(amount) != -1) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Gets the personal account of this player.
   * 
   * @return the player's personal account
   */
  public AccountRecord getPersonalAccount() {
    for (AccountRecord account : accounts) {
      if (account.isPersonal())
        return account;
    }
    return null;
  }

  /**
   * Gets the total balance of all accounts associated with this player.
   * 
   * @return the balance
   */
  public BigDecimal getBalance() {
    BigDecimal balance = new BigDecimal("0");
    for (AccountRecord account : accounts) {
      balance = balance.add(account.getBalance());
    }
    return balance;
  }

  /**
   * Gets the player name.
   * 
   * @return the player name
   */
  public String getPlayerName() {
    return playerName;
  }

  /**
   * Sets the player name.
   * 
   * @param playerName the new player name
   */
  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  /**
   * Gets the id (primary key).
   * 
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the id.
   * 
   * @param id the new id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Find a PlayerRecord by player name.
   * 
   * @param database the database to use
   * @param playerName the player name to search for
   * @return the PlayerRecord found or null if no record was found.
   */
  public static PlayerRecord findByPlayerName(final Database database, String playerName) {
    logger.debug(String.format("Attempting to return PlayerRecord matching the name %s.", playerName));
    final PlayerRecord record = database.getEbeanServer().find(PlayerRecord.class).where().ieq("playerName", playerName).findUnique();
    return record;
  }

  /**
   * Checks if is player known to Exchequer.
   * 
   * @param database the database to use
   * @param playerName the player name to search for
   * @return true, if is player known to Exchequer.
   */
  public static boolean isPlayerKnown(final Database database, final String playerName) {
    final PlayerRecord record = database.getEbeanServer().find(PlayerRecord.class).where().ieq("playerName", playerName).findUnique();
    if (record != null) {
      return true;
    } else {
      return false;
    }
  }

}
