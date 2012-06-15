package name.richardson.james.bukkit.exchequer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import name.richardson.james.bukkit.utilities.internals.Handler;
import name.richardson.james.bukkit.utilities.internals.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class ExchequerHandler.
 */
public class ExchequerHandler extends Handler implements ExchequerAPI {

  private static final Logger logger = new Logger(ExchequerHandler.class);
  
  /** The initial funds provided to newly registered players. */
  private final BigDecimal initalFunds = new BigDecimal("30");

  private Exchequer plugin;
  
  /**
   * Instantiates a new exchequer handler.
   * 
   * @param plugin a reference to the Exchequer plugin
   * @param parentClass the class of the class instantiating the new handler.
   */
  public ExchequerHandler(Exchequer plugin, Class<?> parentClass) {
    super(parentClass);
    this.plugin = plugin;
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.exchequer.ExchequerAPI#getPlayerAccounts(java
   * .lang.String)
   */
  public List<AccountRecord> getPlayerAccounts(String playerName) {
    logger.info("Getting player accounts for " + playerName);
    PlayerRecord record = PlayerRecord.findByPlayerName(plugin.getDatabaseHandler(), playerName);
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
    logger.info("Getting personal account for " + playerName);
    PlayerRecord record = PlayerRecord.findByPlayerName(plugin.getDatabaseHandler(), playerName);
    if (record == null) return null;
    return record.getPersonalAccount();
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.exchequer.ExchequerAPI#isPlayerRegistered(
   * java.lang.String)
   */
  public boolean isPlayerRegistered(String playerName) {
    logger.info("Checking if player is registered: " + playerName);
    return PlayerRecord.isPlayerKnown(plugin.getDatabaseHandler(), playerName);
  }

  /*
   * (non-Javadoc)
   * @see
   * name.richardson.james.bukkit.exchequer.ExchequerAPI#registerPlayer(java
   * .lang.String)
   */
  public PlayerRecord registerPlayer(String playerName) {
    if (PlayerRecord.isPlayerKnown(plugin.getDatabaseHandler(), playerName)) {
      return PlayerRecord.findByPlayerName(plugin.getDatabaseHandler(), playerName);
    } else {
      DatabaseHandler database = plugin.getDatabaseHandler();
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
    logger.info("Saving record.");
    plugin.getDatabaseHandler().save(record);
    return true;
  }

  public AccountRecord getAccount(int accountId) {
    return AccountRecord.findAccountByID(plugin.getDatabaseHandler(), accountId);
  }

  public String formatAmount(BigDecimal amount) {
    StringBuilder message = new StringBuilder();
    // format the major units
    DecimalFormat majorFormatter = new DecimalFormat("##,###;-##.###");
    majorFormatter.setRoundingMode(RoundingMode.DOWN);
    String major = majorFormatter.format(amount);
    message.append(major);
    int unitAmount = amount.intValue();
    if (unitAmount != 1 && unitAmount != -1) {
      message.append(" arms");
    } else {
      message.append(" arm");
    }
    // format the minor units
    DecimalFormat minorFormatter = new DecimalFormat(".##;-.##");
    minorFormatter.setMaximumIntegerDigits(0);
    minorFormatter.setMinimumIntegerDigits(0);
    String minor = minorFormatter.format(amount);
    minor = minor.replaceFirst(".", "");
    unitAmount = Integer.parseInt(minor);
    if (unitAmount != 0) {
      message.append(", ");
      message.append(unitAmount);
      if (unitAmount != 1 && unitAmount != -1) {
        message.append(" legs");
      } else {
        message.append(" leg");
      }
    }
    return message.toString();
  }

}
