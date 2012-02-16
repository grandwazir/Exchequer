package name.richardson.james.bukkit.exchequer.management;

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
public class GrantCommand extends PluginCommand {

  private final ExchequerHandler handler;

  public GrantCommand(Exchequer plugin) {
    super(plugin, plugin.getMessage("grantcommand-name"), plugin.getMessage("grantcommand-description"), plugin.getMessage("grantcommand-usage"));
    this.handler = plugin.getHandler(GrantCommand.class);
    // register permissions
    final String prefix = plugin.getDescription().getName().toLowerCase() + ".";
    Permission base = new Permission(prefix + this.getName(), plugin.getMessage("grantcommand-permission-description"), PermissionDefault.OP);
    base.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(base);
  }

  public void execute(CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    double amount = (Double) getArguments().get("amount");
    
    if (amount < 0) throw new CommandArgumentException(this.plugin.getMessage("setcommand-invalid-amount"), this.plugin.getMessage("may-not-set-negative-balance"));
    
    if (getArguments().containsKey("player")) {
      String playerName = (String) getArguments().get("player");
      AccountRecord account = handler.getPlayerPersonalAccount(playerName);
      if (account == null) throw new CommandUsageException(String.format(this.plugin.getMessage("player-has-no-personal-account"), playerName));
      account.add(amount);
      handler.save(account);
      sender.sendMessage(String.format(ChatColor.GREEN + this.plugin.getMessage("grantcommand-personal-account-success"), handler.formatAmount(amount), playerName));
    } else if (getArguments().containsKey("account")) {
      int accountId = (Integer) getArguments().get("account");
      AccountRecord account = handler.getAccount(accountId);
      if (account == null) throw new CommandUsageException(this.plugin.getMessage("account-does-not-exist"));
      account.add(amount);
      handler.save(account);
      sender.sendMessage(String.format(ChatColor.GREEN + this.plugin.getMessage("setcommand-personal-account-success"), handler.formatAmount(amount), accountId));
    }
    
  }
  
  public void parseArguments(final List<String> arguments, CommandSender sender) throws CommandArgumentException {
    HashMap<String, Object> map = new HashMap<String, Object>();
    
    if (arguments.isEmpty()) {
      throw new CommandArgumentException(this.plugin.getMessage("specify-player-or-account"), this.plugin.getMessage("account-number-hint"));
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
      try {
        map.put("amount", Double.parseDouble((arguments.get(1))));
      } catch (IndexOutOfBoundsException exception) {
        throw new CommandArgumentException(this.plugin.getMessage("specify-amount"), this.plugin.getMessage("specify-amount-hint"));
      } catch (NumberFormatException exception) {
        throw new CommandArgumentException(this.plugin.getMessage("setcommand-invalid-amount"), this.plugin.getMessage("only-numbers-valid"));    
      }
    }
    
    this.setArguments(map);
  }

  
}
