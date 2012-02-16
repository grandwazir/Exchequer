package name.richardson.james.bukkit.exchequer.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.exchequer.AccountRecord;
import name.richardson.james.bukkit.exchequer.Exchequer;
import name.richardson.james.bukkit.exchequer.ExchequerHandler;
import name.richardson.james.bukkit.util.command.CommandArgumentException;
import name.richardson.james.bukkit.util.command.CommandPermissionException;
import name.richardson.james.bukkit.util.command.CommandUsageException;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class GrantCommand extends PlayerCommand {

  public static final String NAME = "grant";
  public static final String DESCRIPTION = "Grant money to players.";
  public static final String PERMISSION_DESCRIPTION = "Allow users to grant money to players.";
  public static final String USAGE = "<player | #account_number> <amount>";

  public static final Permission PERMISSION = new Permission("exchequer.grant", PERMISSION_DESCRIPTION, PermissionDefault.OP);

  private final ExchequerHandler handler;

  public GrantCommand(Exchequer plugin) {
    super(plugin, NAME, DESCRIPTION, USAGE, PERMISSION_DESCRIPTION, PERMISSION);
    this.handler = plugin.getHandler(GrantCommand.class);
  }

  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    double amount = (Double) arguments.get("amount");
    
    if (amount < 0) throw new CommandArgumentException("Invalid amount specified!", "You may not set a negative balance.");
    
    if (arguments.containsKey("player")) {
      String playerName = (String) arguments.get("player");
      AccountRecord account = handler.getPlayerPersonalAccount(playerName);
      if (account == null) throw new CommandUsageException("That player does not have a bank account!");
      account.add(amount);
      handler.save(account);
      sender.sendMessage(String.format(ChatColor.GREEN + "Credited %s to %s's account.", handler.formatAmount(amount), playerName));
    } else if (arguments.containsKey("account")) {
      int accountId = (Integer) arguments.get("account");
      AccountRecord account = handler.getAccount(accountId);
      if (account == null) throw new CommandUsageException("That account does not exist!");
      account.add(amount);
      handler.save(account);
      sender.sendMessage(String.format(ChatColor.GREEN + "Credited %s to account #%s.", handler.formatAmount(amount), accountId));
    }
    
  }
  
  @Override
  public Map<String, Object> parseArguments(final List<String> arguments) throws CommandArgumentException {
    HashMap<String, Object> map = new HashMap<String, Object>();
    
    if (arguments.isEmpty()) {
      throw new CommandArgumentException("You must specify a valid player or account number!", "You should prefix account numbers with #.");
    } else {
      try {
        String account = arguments.get(0);
        if (account.contains("#")) {
          map.put("account", Integer.parseInt(account.replaceFirst("#", "")));
        } else {
          map.put("player", account);
        }
      } catch (NumberFormatException exception) {
        throw new CommandArgumentException("You must specify a valid account number!", "You should prefix it with #.");
      }
      try {
        map.put("amount", Double.parseDouble((arguments.get(1))));
      } catch (IndexOutOfBoundsException exception) {
        throw new CommandArgumentException("You must specify an amount!", "This is how much you want to set the balance to.");
      } catch (NumberFormatException exception) {
        throw new CommandArgumentException("You must specify an valid amount!", "Only numbers are valid.");    
      }
    }
    return map;
  }
  
}
