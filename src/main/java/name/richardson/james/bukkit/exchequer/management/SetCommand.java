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
public class SetCommand extends PluginCommand {

  private final ExchequerHandler handler;

  public SetCommand(Exchequer plugin) {
    super(plugin, plugin.getMessage("setcommand-name"), plugin.getMessage("setcommand-description"), plugin.getMessage("setcommand-usage"));
    this.handler = plugin.getHandler(SetCommand.class);
    // register permissions
    final String prefix = plugin.getDescription().getName().toLowerCase() + ".";
    Permission base = new Permission(prefix + this.getName(), plugin.getMessage("setcommand-permission-description"), PermissionDefault.OP);
    base.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(base);
  }

  public void execute(CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    double amount = (Double) this.getArguments().get("amount");
    
    if (amount < 0) throw new CommandArgumentException(this.plugin.getMessage("setcommand-invalid-amount"), this.plugin.getMessage("may-not-set-negative-balance"));
    
    if (this.getArguments().containsKey("player")) {
      String playerName = (String) this.getArguments().get("player");
      AccountRecord account = handler.getPlayerPersonalAccount(playerName);
      if (account == null) throw new CommandUsageException(String.format(this.plugin.getMessage("player-has-no-personal-account"), playerName));
      account.setBalance(amount);
      handler.save(account);
      sender.sendMessage(String.format(ChatColor.GREEN + this.plugin.getMessage("setcommand-personal-account-success"), playerName, handler.formatAmount(amount)));
    } else if (this.getArguments().containsKey("account")) {
      int accountId = (Integer) this.getArguments().get("account");
      AccountRecord account = handler.getAccount(accountId);
      if (account == null) throw new CommandUsageException(this.plugin.getMessage("account-does-not-exist"));
      account.setBalance(amount);
      handler.save(account);
      sender.sendMessage(String.format(ChatColor.GREEN + this.plugin.getMessage("setcommand-bank-account-success"), accountId, handler.formatAmount(amount)));
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
