package club.frozed.frozedsg.utils.command;

import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class BukkitCommand extends Command {
    private Plugin owningPlugin;
    private CommandExecutor executor;
    protected BukkitCompleter completer;

    protected BukkitCommand(String label, CommandExecutor executor, Plugin owner) {
        super(label);
        this.executor = executor;
        this.owningPlugin = owner;
        this.usageMessage = "";
    }

    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        boolean success = false;
        if (!this.owningPlugin.isEnabled()) {
            return false;
        } else if (!this.testPermission(sender)) {
            return true;
        } else {
            try {
                success = this.executor.onCommand(sender, this, commandLabel, args);
            } catch (Throwable var9) {
                throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + this.owningPlugin.getDescription().getFullName(), var9);
            }

            if (!success && this.usageMessage.length() > 0) {
                String[] var5 = this.usageMessage.replace("<command>", commandLabel).split("\n");
                int var6 = var5.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    String line = var5[var7];
                    sender.sendMessage(line);
                }
            }

            return success;
        }
    }

    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws CommandException, IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");
        List<String> completions = null;

        try {
            if (this.completer != null) {
                completions = this.completer.onTabComplete(sender, this, alias, args);
            }

            if (completions == null && this.executor instanceof TabCompleter) {
                completions = ((TabCompleter)this.executor).onTabComplete(sender, this, alias, args);
            }
        } catch (Throwable var11) {
            StringBuilder message = new StringBuilder();
            message.append("Unhandled exception during tab completion for command '/").append(alias).append(' ');
            String[] var7 = args;
            int var8 = args.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                String arg = var7[var9];
                message.append(arg).append(' ');
            }

            message.deleteCharAt(message.length() - 1).append("' in plugin ").append(this.owningPlugin.getDescription().getFullName());
            throw new CommandException(message.toString(), var11);
        }

        return completions == null ? super.tabComplete(sender, alias, args) : completions;
    }
}
