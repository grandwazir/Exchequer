package name.richardson.james.bukkit.exchequer.general;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.exchequer.AccountRecord;
import name.richardson.james.bukkit.exchequer.Exchequer;
import name.richardson.james.bukkit.exchequer.ExchequerHandler;
import name.richardson.james.bukkit.exchequer.PlayerRecord;
import name.richardson.james.bukkit.utilities.command.CommandArgumentException;
import name.richardson.james.bukkit.utilities.command.CommandPermissionException;
import name.richardson.james.bukkit.utilities.command.CommandUsageException;
import name.richardson.james.bukkit.utilities.command.ConsoleCommand;
import name.richardson.james.bukkit.utilities.command.PluginCommand;

@ConsoleCommand
public class BalanceCommand extends PluginCommand {
  
  private final ExchequerHandler handler;

  private String playerName;

  private int accountNumber;
  
  public BalanceCommand(Exchequer plugin) {
    super(plugin);
    this.handler = plugin.getHandler(BalanceCommand.class);
    this.registerPermissions();
  }
  
  private void registerPermissions() {
    final String prefix = plugin.getDescription().getName().toLowerCase() + ".";
    final String wildcardDescription = String.format(plugin.getMessage("wildcard-permission-description"), this.getName());
    // create the wildcard permission
    Permission wildcard = new Permission(prefix + this.getName() + ".*", wildcardDescription, PermissionDefault.OP);
    wildcard.addParent(plugin.getRootPermission(), true);
    this.addPermission(wildcard);
    // create the base permission
    Permission base = new Permission(prefix + this.getName(), plugin.getMessage("balancecommand-permission-description"), PermissionDefault.TRUE);
    base.addParent(wildcard, true);
    this.addPermission(base);
    // create the ability to view other player's accounts
    Permission others = new Permission(prefix + this.getName() + "." + plugin.getMessage("balancecommand-others-permission-name"), plugin.getMessage("balancecommand-others-permission-description"), PermissionDefault.OP);
    others.addParent(wildcard, true);
    this.addPermission(others);
  }

  public void execute(CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
  
    if (this.playerName != null) {
      // check to see if the player has permission to view other people's accounts
      if (!playerName.equalsIgnoreCase(sender.getName()) && !sender.hasPermission(this.getPermission(2))) {
        throw new CommandPermissionException(this.plugin.getMessage("balancecommand-others-not-allowed"), this.getPermission(2));
      }
      
      // otherwise list all the accounts
      List<AccountRecord> accounts = handler.getPlayerAccounts(playerName);
      // throw exception they have no accounts
      if (accounts.isEmpty()) throw new CommandUsageException(plugin.getSimpleFormattedMessage("balancecommand-player-has-no-accounts", playerName));
      
      // list all the accounts
      sender.sendMessage(this.getFormattedBalanceHeader(accounts.size()));
      BigDecimal total = new BigDecimal(0);
      for (AccountRecord account : accounts) {
        Object[] tokens = {account.getId(), handler.formatAmount(account.getBalance())};
        sender.sendMessage(this.plugin.getSimpleFormattedMessage("balancecommand-account-detail", tokens));
        total = total.add(account.getBalance());
      }
      sender.sendMessage(this.plugin.getSimpleFormattedMessage("balancecommand-total", handler.formatAmount(total)));
    } else if (this.accountNumber != 0) {
      AccountRecord account = handler.getAccount(this.accountNumber);
      if (account == null) throw new CommandUsageException(this.plugin.getMessage("account-does-not-exist"));
      
      // get a list of signatories
      Set<String> signatories = new HashSet<String>();
      for (PlayerRecord record : account.getSignatories()) {
        signatories.add(record.getPlayerName());
      }
      
      // check to see if this person is allowed to view the balance of other people's accounts
      if (!signatories.contains(sender.getName()) && !sender.hasPermission(this.getPermission(2))) {
        throw new CommandPermissionException(this.plugin.getMessage("balancecommand-others-not-allowed"), this.getPermission(2));
      }
      
      sender.sendMessage(this.plugin.getSimpleFormattedMessage("balancecommand-account-balance", account.getId()));
      sender.sendMessage(this.plugin.getSimpleFormattedMessage("balancecommand-account-signatories", buildOwnerList(account)));
      sender.sendMessage(this.plugin.getSimpleFormattedMessage("balancecommand-total", handler.formatAmount(account.getBalance())));
    }
  
  }

  private String getFormattedBalanceHeader(int size) {
    Object[] arguments = {size, this.playerName};
    double[] limits = {0, 1, 2};
    String[] formats = {this.getMessage("no-accounts"), this.getMessage("one-account"), this.getMessage("many-accounts")};
    return this.getChoiceFormattedMessage("total-player-accounts", arguments, formats, limits);
  }

  private String buildOwnerList(AccountRecord account) {
    StringBuilder message = new StringBuilder();
    for (PlayerRecord record : account.getSignatories()) {
      message.append(record.getPlayerName());
      message.append(" ");
      message.append(", ");
    }
    message.delete(message.length() - 2, message.length());
    message.append(".");
    return message.toString();
  }

  
  public void parseArguments(String[] arguments, CommandSender sender) throws CommandArgumentException {
    this.playerName = null;
    this.accountNumber = 0;
    
    if (arguments.length == 0 && sender instanceof ConsoleCommandSender) {
      throw new CommandArgumentException(this.plugin.getMessage("specify-player-or-account"), this.plugin.getMessage("account-number-hint"));
    } else if (arguments.length == 1) {
      if (arguments[0].startsWith("#")) {
        try {
          this.accountNumber = Integer.parseInt(arguments[0].replaceAll("#", ""));
        } catch (NumberFormatException exception) {
          throw new CommandArgumentException(this.plugin.getMessage("setcommand-invalid-amount"), this.plugin.getMessage("account-number-hint"));    
        }
      } else {
        this.playerName = arguments[0];
      }
    } else {
      this.playerName = sender.getName();
    }
    
  }
  
}
