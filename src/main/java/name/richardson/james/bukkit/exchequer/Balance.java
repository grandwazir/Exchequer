package name.richardson.james.bukkit.exchequer;

import java.math.BigDecimal;

// TODO: Auto-generated Javadoc
/**
 * The Interface Balance.
 */
public interface Balance {
  
  /**
   * Adds the specified amount to the balance.
   * 
   * @param amount the amount to add to the balance.
   * @return the modified balance.
   * @throws IllegalArgumentException if the new balance is less than 0.
   */
  public BigDecimal add(BigDecimal amount);

  /**
   * Subtract the specified amount from the balance.
   * 
   * @param amount the amount to add to the balance.
   * @return the modified balance.
   * @throws IllegalArgumentException if the new balance is less than 0.
   */
  public BigDecimal subtract(BigDecimal amount);

  /**
   * Divide the balance by the specified amount.
   * 
   * @param amount the amount to divide the balance by.
   * @return the modified balance.
   */
  public BigDecimal divide(BigDecimal amount);

  /**
   * Multiply the balance by the specified amount.
   * 
   * @param amount the amount to multiply the balance by.
   * @return the modified balance.
   */
  public BigDecimal multiply(BigDecimal amount);

  /**
   * Check if the balance contains a certain amount of funds.
   * 
   * @param amount the amount to check
   * @return true, if the balance contains equal to, or greater than, the amount
   *         specified.
   */
  public boolean contains(BigDecimal amount);

}
