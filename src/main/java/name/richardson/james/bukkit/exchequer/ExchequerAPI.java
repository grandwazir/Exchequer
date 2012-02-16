package name.richardson.james.bukkit.exchequer;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Interface ExchequerAPI.
 */
public interface ExchequerAPI {

  /**
   * Gets a list of all the accounts a player has access to.
   * 
   * This list will include hidden and personal accounts.
   * 
   * @param playerName the name of the player.
   * @return the accounts to which the player is a signatory.
   */
  public List<AccountRecord> getPlayerAccounts(String playerName);

  /**
   * Gets the personal account of a player.
   * 
   * @param playerName the name of the player
   * @return the player's personal account or null if the player is not
   *         registered.
   */
  public AccountRecord getPlayerPersonalAccount(String playerName);

  /**
   * Checks if the player is registered with Exchequer.
   * 
   * @param playerName the name of the player
   * @return true, if is player is known to Exchequer
   */
  public boolean isPlayerRegistered(String playerName);

  /**
   * Register a player with Exchequer.
   * 
   * This method will create a new PlayerRecord and an associated personal
   * AccountRecord set to the initial default balance specified in the
   * configuration of Exchequer.
   * 
   * Calling this method on a player who is already registered is safe and will
   * make no changes.
   * 
   * @param playerName the name of the player
   * @return the PlayerRecord associated with this name
   */
  public PlayerRecord registerPlayer(String playerName);

  /**
   * Save an object to the Exchequer database.
   * 
   * This will need to be used in order to save your changes to an AccountRecord
   * or a PlayerRecord after modifying them directly.
   * 
   * @param record the object to save.
   * @return true, if successful
   */
  public boolean save(Object record);

}
