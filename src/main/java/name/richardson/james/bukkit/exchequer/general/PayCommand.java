package name.richardson.james.bukkit.exchequer.general;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.exchequer.AccountRecord;
import name.richardson.james.bukkit.exchequer.Exchequer;
import name.richardson.james.bukkit.exchequer.ExchequerHandler;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.PluginCommand;

public class PayCommand extends PluginCommand {

  private final ExchequerHandler handler;
  private final Server server;
  
  /* Name of the player who is making a payment */
  private String payee;
  
  /* Name of the player who is will receive the money */
  private String beneficiary;
  
  /* Amount being transfered */
  private BigDecimal amount;

  public PayCommand(Exchequer plugin) {
    super(plugin);
    this.handler = plugin.getHandler(PayCommand.class);
    this.server = plugin.getServer();
    // register permissions
    final String prefix = plugin.getDescription().getName().toLowerCase() + ".";
    Permission base = new Permission(prefix + this.getName(), plugin.getMessage("paycommand-permission-description"), PermissionDefault.TRUE);
    base.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(base);
  }
  
  public void execute(CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    if (beneficiary.equalsIgnoreCase(sender.getName())) throw new CommandArgumentException(plugin.getMessage("may-not-pay-money-to-self"), plugin.getMessage("transfer-money-to-self-hint"));
    
    AccountRecord playerAccount = handler.getPlayerPersonalAccount(payee);
    AccountRecord beneficiaryAccount = handler.getPlayerPersonalAccount(beneficiary);
    
    if (beneficiaryAccount == null) throw new CommandUsageException(this.plugin.getSimpleFormattedMessage("player-has-no-personal-account", beneficiary));
    
    if (!playerAccount.contains(amount)) throw new CommandUsageException(plugin.getMessage("not-enough-funds"));
    
    playerAccount.subtract(amount);
    beneficiaryAccount.add(amount);
    handler.save(playerAccount);
    handler.save(beneficiaryAccount);
    
    Object[] atokens = {beneficiary, handler.formatAmount(amount)};
    sender.sendMessage(this.plugin.getSimpleFormattedMessage("paycommand-success", atokens));
    // notify beneficiary if they are online
    Player beneficiaryPlayer = server.getPlayer(beneficiary);
    if (beneficiaryPlayer != null) {
      Object[] btokens = {payee, handler.formatAmount(amount)};
      beneficiaryPlayer.sendMessage(this.plugin.getSimpleFormattedMessage("paycommand-notify", btokens));
    }
    
  }

  public void parseArguments(String[] arguments, CommandSender sender) throws CommandArgumentException {
    this.beneficiary = null;
    this.payee = sender.getName();
    
    if (arguments.length == 0) {
      throw new CommandArgumentException(this.plugin.getMessage("specify-player"), this.plugin.getMessage("specify-player-hint"));
    } else {
      // get the beneficiary
      beneficiary = arguments[0];
      try {
        amount = new BigDecimal(arguments[1]);
      } catch (IndexOutOfBoundsException exception) {
        throw new CommandArgumentException(this.plugin.getMessage("specify-amount"), this.plugin.getMessage("specify-amount-hint"));
      } catch (NumberFormatException exception) {
        throw new CommandArgumentException(this.plugin.getMessage("setcommand-invalid-amount"), this.plugin.getMessage("only-numbers-valid"));    
      }
      
      if (amount.doubleValue() < 0) throw new CommandArgumentException(this.plugin.getMessage("setcommand-invalid-amount"), this.plugin.getMessage("only-numbers-valid"));
      
    }
    
  }
  
}
