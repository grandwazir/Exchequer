package name.richardson.james.bukkit.exchequer.general;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import name.richardson.james.bukkit.exchequer.AccountRecord;
import name.richardson.james.bukkit.exchequer.Exchequer;
import name.richardson.james.bukkit.exchequer.ExchequerHandler;
import name.richardson.james.bukkit.exchequer.management.SetCommand;
import name.richardson.james.bukkit.util.command.CommandArgumentException;
import name.richardson.james.bukkit.util.command.CommandPermissionException;
import name.richardson.james.bukkit.util.command.CommandUsageException;
import name.richardson.james.bukkit.util.command.PlayerCommand;

public class PayCommand extends PlayerCommand {

  public static final String NAME = "pay";
  public static final String DESCRIPTION = "Pay another player.";
  public static final String PERMISSION_DESCRIPTION = "Allow users to pay other players.";
  public static final String USAGE = "<player>";

  public static final Permission PERMISSION = new Permission("exchequer.pay", PERMISSION_DESCRIPTION, PermissionDefault.OP);

  private final ExchequerHandler handler;
  private final Server server;

  public PayCommand(Exchequer plugin) {
    super(plugin, NAME, DESCRIPTION, USAGE, PERMISSION_DESCRIPTION, PERMISSION);
    this.server = plugin.getServer();
    this.handler = plugin.getHandler(SetCommand.class);
  }
  
  @Override
  public void execute(CommandSender sender, Map<String, Object> arguments) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    String beneficiary = (String) arguments.get("player");
    String player = sender.getName();
    double amount = (Double) arguments.get("amount");
    
    if (sender instanceof ConsoleCommandSender) throw new CommandUsageException("You may not use this command from the console.");
    
    if (beneficiary.equalsIgnoreCase(player)) throw new CommandArgumentException("You may not pay money to yourself!", "Use the transfer command to move money between accounts.");
    
    AccountRecord playerAccount = handler.getPlayerPersonalAccount(player);
    AccountRecord beneficiaryAccount = handler.getPlayerPersonalAccount(beneficiary);
    
    if (beneficiaryAccount == null) throw new CommandUsageException("That player does not have a bank account!");
    
    if (!playerAccount.contains(amount)) throw new CommandUsageException("You do not have enough money to do that!");
    
    playerAccount.substract(amount);
    beneficiaryAccount.add(amount);
    handler.save(playerAccount);
    handler.save(beneficiaryAccount);
    
    sender.sendMessage(String.format(ChatColor.GREEN + "You have sent %s %s.", beneficiary, handler.formatAmount(amount)));
    // notify beneficiary if they are online
    Player beneficiaryPlayer = server.getPlayer(beneficiary);
    if (beneficiaryPlayer != null) {
      beneficiaryPlayer.sendMessage(String.format("You have recieved %s from %s.", handler.formatAmount(amount), player));
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
