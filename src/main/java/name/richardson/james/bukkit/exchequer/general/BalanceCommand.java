package name.richardson.james.bukkit.exchequer.general;

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
import name.richardson.james.bukkit.exchequer.management.SetCommand;
import name.richardson.james.bukkit.util.command.CommandArgumentException;
import name.richardson.james.bukkit.util.command.CommandPermissionException;
import name.richardson.james.bukkit.util.command.CommandUsageException;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class BalanceCommand extends PlayerCommand {
  
  public static final String NAME = "balance";
  public static final String DESCRIPTION = "Check the balance of an account";
  public static final String PERMISSION_DESCRIPTION = "Allow users to check the balance of their account.";
  public static final String USAGE = "[player | #account_number]";

  public static final Permission PERMISSION = new Permission("exchequer.balance", PERMISSION_DESCRIPTION, PermissionDefault.OP);

  private final ExchequerHandler handler;

  public BalanceCommand(Exchequer plugin) {
    super(plugin, NAME, DESCRIPTION, USAGE, PERMISSION_DESCRIPTION, PERMISSION);
    this.handler = plugin.getHandler(SetCommand.class);
  }
  
  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
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
    }
    return map;
  }
  
  
}
