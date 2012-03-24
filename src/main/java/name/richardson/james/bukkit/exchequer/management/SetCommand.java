package name.richardson.james.bukkit.exchequer.management;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.exchequer.AccountRecord;
import name.richardson.james.bukkit.exchequer.Exchequer;
import name.richardson.james.bukkit.exchequer.ExchequerHandler;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.command.PluginCommand;

@ConsoleCommand
public class SetCommand extends PluginCommand {
  
  /* The account to target  */
  private AccountRecord account;
  
  /* The id of the account to target  */
  private int accountNumber;
  
  /* The name of the player to target */
  private String playerName;
  
  /* The amount of money to set the balance to */
  private BigDecimal amount;
  
  private final ExchequerHandler handler;

  public SetCommand(Exchequer plugin) {
    super(plugin);
    this.handler = plugin.getHandler(SetCommand.class);
    // register permissions
    final String prefix = plugin.getDescription().getName().toLowerCase() + ".";
    Permission base = new Permission(prefix + this.getName(), plugin.getMessage("setcommand-permission-description"), PermissionDefault.OP);
    base.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(base);
  }

  public void execute(CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {

    if (playerName != null) {
      account = handler.getPlayerPersonalAccount(playerName);
      if (account == null) throw new CommandUsageException(this.plugin.getSimpleFormattedMessage("player-has-no-personal-account", playerName));
      this.alterBalance();
      Object[] tokens = {playerName, handler.formatAmount(account.getBalance())};
      sender.sendMessage(plugin.getSimpleFormattedMessage("setcommand-personal-account-success", tokens));
    } else {
      account = handler.getAccount(accountNumber);
      if (account == null) throw new CommandUsageException(this.plugin.getMessage("account-does-not-exist"));
      this.alterBalance();
      Object[] tokens = {accountNumber, handler.formatAmount(account.getBalance())};
      sender.sendMessage(plugin.getSimpleFormattedMessage("setcommand-bank-account-success", tokens));
    }
    
  }
  
  private void alterBalance() throws CommandUsageException {
    try {
      account.setBalance(amount);
    } catch (IllegalArgumentException exception) {
      throw new CommandUsageException(plugin.getMessage(exception.getMessage()));
    }
    handler.save(account);
  }
  
  public void parseArguments(final String[] args, CommandSender sender) throws CommandArgumentException {
    this.playerName = null;
    this.accountNumber = 0;
    this.amount = null;
    
    if (args.length == 0) throw new CommandArgumentException(this.plugin.getMessage("specify-player-or-account"), this.plugin.getMessage("specify-player-hint"));
    
    // get the target account
    if (args[0].startsWith("#")) {
      try {
        this.accountNumber = Integer.parseInt(args[0].replaceAll("#", ""));
      } catch (NumberFormatException exception) {
        throw new CommandArgumentException(this.plugin.getMessage("specify-player-or-account"), this.plugin.getMessage("only-numbers-valid"));    
      }
    } else {
      this.playerName = args[0];
    }
    
    // get the amount
    if (args.length == 2) {
      try {
        this.amount = new BigDecimal(args[1], AccountRecord.MATH_CONTEXT);
      } catch (NumberFormatException exception) {
        throw new CommandArgumentException(this.plugin.getMessage("specify-amount"), this.plugin.getMessage("only-numbers-valid"));    
      }
    } else {
      throw new CommandArgumentException(this.plugin.getMessage("specify-amount"), this.plugin.getMessage("specify-amount-hint"));
    }
    
  }
  
  
}
