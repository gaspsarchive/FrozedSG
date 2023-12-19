package club.frozed.frozedsg.utils.command;

import club.frozed.frozedsg.PotSG;
import club.frozed.frozedsg.utils.chat.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.IndexHelpTopic;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CommandFramework implements CommandExecutor {
    private Map<String, Map.Entry<Method, Object>> commandMap = new HashMap();
    private CommandMap map;
    private PotSG plugin;

    public CommandFramework(PotSG plugin) {
        this.plugin = plugin;
        if (plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager manager = (SimplePluginManager)plugin.getServer().getPluginManager();

            try {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                this.map = (CommandMap)field.get(manager);
            } catch (SecurityException | NoSuchFieldException | IllegalAccessException | IllegalArgumentException var4) {
                var4.printStackTrace();
            }
        }

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return this.handleCommand(sender, cmd, label, args);
    }

    public boolean handleCommand(CommandSender sender, Command cmd, String label, String[] args) {
        for(int i = args.length; i >= 0; --i) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(label.toLowerCase());

            for(int x = 0; x < i; ++x) {
                buffer.append("." + args[x].toLowerCase());
            }

            String cmdLabel = buffer.toString();
            if (this.commandMap.containsKey(cmdLabel)) {
                Method method = (Method)((Map.Entry)this.commandMap.get(cmdLabel)).getKey();
                Object methodObject = ((Map.Entry)this.commandMap.get(cmdLabel)).getValue();
                club.frozed.frozedsg.utils.command.Command command = (club.frozed.frozedsg.utils.command.Command)method.getAnnotation(club.frozed.frozedsg.utils.command.Command.class);
                if (command.isAdminOnly() && !sender.hasPermission("frozedsg.admin")) {
                    sender.sendMessage(ChatColor.RED + "Only ops may execute this command.");
                    return true;
                }

                if (!command.permission().equals("") && !sender.hasPermission(command.permission())) {
                    sender.sendMessage(Color.translate("&cIt seems you don't have enough perms to perform this command."));
                    return true;
                }

                if (command.inGameOnly() && !(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "This command is only performable in game.");
                    return true;
                }

                try {
                    method.invoke(methodObject, new CommandArgs(sender, cmd, label, args, cmdLabel.split("\\.").length - 1));
                } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException var12) {
                    var12.printStackTrace();
                }

                return true;
            }
        }

        this.defaultCommand(new CommandArgs(sender, cmd, label, args, 0));
        return true;
    }

    public void registerCommands(Object obj) {
        Method[] var2 = obj.getClass().getMethods();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Method m = var2[var4];
            String[] var7;
            int var8;
            int var9;
            String alias;
            if (m.getAnnotation(club.frozed.frozedsg.utils.command.Command.class) != null) {
                club.frozed.frozedsg.utils.command.Command command = (club.frozed.frozedsg.utils.command.Command)m.getAnnotation(club.frozed.frozedsg.utils.command.Command.class);
                if (m.getParameterTypes().length <= 1 && m.getParameterTypes()[0] == CommandArgs.class) {
                    this.registerCommand(command, command.name(), m, obj);
                    var7 = command.aliases();
                    var8 = var7.length;

                    for(var9 = 0; var9 < var8; ++var9) {
                        alias = var7[var9];
                        this.registerCommand(command, alias, m, obj);
                    }
                } else {
                    System.out.println("Unable to register command " + m.getName() + ". Unexpected method arguments");
                }
            } else if (m.getAnnotation(Completer.class) != null) {
                Completer comp = (Completer)m.getAnnotation(Completer.class);
                if (m.getParameterTypes().length <= 1 && m.getParameterTypes().length != 0 && m.getParameterTypes()[0] == CommandArgs.class) {
                    if (m.getReturnType() != List.class) {
                        System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected return type");
                    } else {
                        this.registerCompleter(comp.name(), m, obj);
                        var7 = comp.aliases();
                        var8 = var7.length;

                        for(var9 = 0; var9 < var8; ++var9) {
                            alias = var7[var9];
                            this.registerCompleter(alias, m, obj);
                        }
                    }
                } else {
                    System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected method arguments");
                }
            }
        }

    }

    public void registerHelp() {
        Set<HelpTopic> help = new TreeSet(HelpTopicComparator.helpTopicComparatorInstance());
        Iterator var2 = this.commandMap.keySet().iterator();

        while(var2.hasNext()) {
            String s = (String)var2.next();
            if (!s.contains(".")) {
                Command cmd = this.map.getCommand(s);
                HelpTopic topic = new GenericCommandHelpTopic(cmd);
                help.add(topic);
            }
        }

        IndexHelpTopic topic = new IndexHelpTopic(this.plugin.getName(), "All commands for " + this.plugin.getName(), (String)null, help, "Below is a list of all " + this.plugin.getName() + " commands:");
        Bukkit.getServer().getHelpMap().addTopic(topic);
    }

    public void unregisterCommands(Object obj) {
        Method[] var2 = obj.getClass().getMethods();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Method m = var2[var4];
            if (m.getAnnotation(club.frozed.frozedsg.utils.command.Command.class) != null) {
                club.frozed.frozedsg.utils.command.Command command = (club.frozed.frozedsg.utils.command.Command)m.getAnnotation(club.frozed.frozedsg.utils.command.Command.class);
                this.commandMap.remove(command.name().toLowerCase());
                this.commandMap.remove(this.plugin.getName() + ":" + command.name().toLowerCase());
                this.map.getCommand(command.name().toLowerCase()).unregister(this.map);
            }
        }

    }

    public void registerCommand(club.frozed.frozedsg.utils.command.Command command, String label, Method m, Object obj) {
        this.commandMap.put(label.toLowerCase(), new AbstractMap.SimpleEntry(m, obj));
        this.commandMap.put(this.plugin.getName() + ':' + label.toLowerCase(), new AbstractMap.SimpleEntry(m, obj));
        String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase();
        if (this.map.getCommand(cmdLabel) == null) {
            Command cmd = new BukkitCommand(cmdLabel, this, this.plugin);
            this.map.register(this.plugin.getName(), cmd);
        }

        if (!command.description().equalsIgnoreCase("") && cmdLabel == label) {
            this.map.getCommand(cmdLabel).setDescription(command.description());
        }

        if (!command.usage().equalsIgnoreCase("") && cmdLabel == label) {
            this.map.getCommand(cmdLabel).setUsage(command.usage());
        }

    }

    public void registerCompleter(final String label, final Method m, final Object obj) {
        String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase();
        BukkitCommand command;
        if (this.map.getCommand(cmdLabel) == null) {
            command = new BukkitCommand(cmdLabel, this, this.plugin);
            this.map.register(this.plugin.getName(), command);
        }

        if (this.map.getCommand(cmdLabel) instanceof BukkitCommand) {
            command = (BukkitCommand)this.map.getCommand(cmdLabel);
            if (command.completer == null) {
                command.completer = new BukkitCompleter();
            }

            command.completer.addCompleter(label, m, obj);
        } else if (this.map.getCommand(cmdLabel) instanceof PluginCommand) {
            try {
                final Object command3 = this.map.getCommand(cmdLabel);
                final Field field = command3.getClass().getDeclaredField("completer");
                field.setAccessible(true);
                if (field.get(command3) == null) {
                    final BukkitCompleter completer = new BukkitCompleter();
                    completer.addCompleter(label, m, obj);
                    field.set(command3, completer);
                }
                else if (field.get(command3) instanceof BukkitCompleter) {
                    final BukkitCompleter completer = (BukkitCompleter)field.get(command3);
                    completer.addCompleter(label, m, obj);
                }
                else {
                    System.out.println("Unable to register tab completer " + m.getName() + ". A tab completer is already registered for that command!");
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void defaultCommand(final CommandArgs args) {
        args.getSender().sendMessage(args.getLabel() + " is not handled! Oh noes!");
    }
}
