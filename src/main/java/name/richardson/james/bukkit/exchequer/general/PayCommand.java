package name.richardson.james.bukkit.exchequer.general;

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

  public PayCommand(Exchequer plugin) {
    super(plugin, plugin.getMessage("paycommand-name"), plugin.getMessage("paycommand-description"), plugin.getMessage("paycommand-usage"));
    this.handler = plugin.getHandler(PayCommand.class);
    this.server = plugin.getServer();
    // register permissions
    final String prefix = plugin.getDescription().getName().toLowerCase() + ".";
    Permission base = new Permission(prefix + this.getName(), plugin.getMessage("paycommand-permission-description"), PermissionDefault.TRUE);
    base.addParent(this.plugin.getRootPermission(), true);
    this.addPermission(base);
  }
  
  public void execute(CommandSender sender) throws CommandArgumentException, CommandPermissionException, CommandUsageException {
    String beneficiary = (String) getArguments().get("player");
    String player = sender.getName();
    double amount = (Double) getArguments().get("amount");
    
    if (beneficiary.equalsIgnoreCase(player)) throw new CommandArgumentException(plugin.getMessage("may-not-pay-money-to-self"), plugin.getMessage("transfer-money-to-self-hint"));
    
    AccountRecord playerAccount = handler.getPlayerPersonalAccount(player);
    AccountRecord beneficiaryAccount = handler.getPlayerPersonalAccount(beneficiary);
    
    if (beneficiaryAccount == null) throw new CommandUsageException(String.format(this.plugin.getMessage("player-has-no-personal-account"), beneficiary));
    
    if (!playerAccount.contains(amount)) throw new CommandUsageException(plugin.getMessage("not-enough-funds"));
    
    playerAccount.substract(amount);
    beneficiaryAccount.add(amount);
    handler.save(playerAccount);
    handler.save(beneficiaryAccount);
    
    sender.sendMessage(String.format(ChatColor.GREEN + this.plugin.getMessage("paycommand-success"), handler.formatAmount(amount), beneficiary));
    // notify beneficiary if they are online
    Player beneficiaryPlayer = server.getPlayer(beneficiary);
    if (beneficiaryPlayer != null) {
      beneficiaryPlayer.sendMessage(String.format(ChatColor.GREEN + this.plugin.getMessage("paycommand-notify"), handler.formatAmount(amount), player));
    }
    
  }

  public void parseArguments(final List<String> arguments, CommandSender sender) throws CommandArgumentException {
    HashMap<String, Object> map = new HashMap<String, Object>();
    
    if (arguments.isEmpty()) {
      throw new CommandArgumentException(this.plugin.getMessage("specify-player"), this.plugin.getMessage("specify-player-hint"));
    } else {
      try {
        String playerName = arguments.get(0);
        map.put("player", playerName);
      } catch (NumberFormatException exception) {
        throw new CommandArgumentException(this.plugin.getMessage("specify-player"), this.plugin.getMessage("specify-player-hint"));
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
