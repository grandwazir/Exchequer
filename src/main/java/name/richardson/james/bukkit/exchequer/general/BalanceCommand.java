package name.richardson.james.bukkit.exchequer.general;

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

  public BalanceCommand(Exchequer plugin) {
    super(plugin, plugin.getMessage("balancecommand-name"), plugin.getMessage("balancecommand-description"), plugin.getMessage("balancecommand-usage"));
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
  
    if (getArguments().containsKey("player")) {
      String playerName = (String) getArguments().get("player");
      // check to see if the player has permission to view other people's accounts
      if (!playerName.equalsIgnoreCase(sender.getName()) && !sender.hasPermission(this.getPermission(2))) {
        throw new CommandPermissionException(this.plugin.getMessage("balancecommand-others-not-allowed"), this.getPermission(2));
      }
      // otherwise list all the accounts
      List<AccountRecord> accounts = handler.getPlayerAccounts(playerName);
      if (accounts.isEmpty()) throw new CommandUsageException(String.format(this.plugin.getMessage("balancecommand-player-has-no-accounts"), playerName));
      sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + this.plugin.getMessage("balancecommand-header"), playerName, accounts.size()));
      double total = 0;
      for (AccountRecord account : accounts) {
        total = total + account.getBalance();
        sender.sendMessage(String.format(ChatColor.YELLOW + this.plugin.getMessage("balancecommand-account-detail"), account.getId(), handler.formatAmount(account.getBalance())));
      }
      sender.sendMessage(String.format(ChatColor.GREEN + this.plugin.getMessage("balancecommand-total"), handler.formatAmount(total)));
    } else if (getArguments().containsKey("account")) {
      int accountId = (Integer) getArguments().get("account");
      AccountRecord account = handler.getAccount(accountId);
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
      sender.sendMessage(String.format(ChatColor.LIGHT_PURPLE + this.plugin.getMessage("balancecommand-account-balance"), account.getId()));
      sender.sendMessage(String.format(ChatColor.YELLOW + this.plugin.getMessage("balancecommand-account-signatories"), buildOwnerList(account)));
      sender.sendMessage(String.format(ChatColor.GREEN + this.plugin.getMessage("balancecommand-total"), handler.formatAmount(account.getBalance())));
    }
  
  }

  public void parseArguments(final List<String> arguments, final CommandSender sender) throws CommandArgumentException {
    HashMap<String, Object> map = new HashMap<String, Object>();
    
    if (arguments.isEmpty()) {
      if (sender instanceof ConsoleCommandSender) throw new CommandArgumentException(this.plugin.getMessage("specify-player-or-account"), this.plugin.getMessage("account-number-hint"));
      map.put("player", sender.getName());
    } else {
      try {
        String account = arguments.get(0);
        if (account.contains("#")) {
          map.put("account", Integer.parseInt(account.replaceFirst("#", "")));
        } else {
          map.put("player", account);
        }
      } catch (NumberFormatException exception) {
        throw new CommandArgumentException(this.plugin.getMessage("specify-account-number"), this.plugin.getMessage("account-number-hint"));
      }
    }
    this.setArguments(map);
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
  
}
