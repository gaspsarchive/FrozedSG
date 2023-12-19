package club.frozed.frozedsg.utils;

import club.frozed.frozedsg.utils.chat.Color;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
public class Clickable
{
    private List<TextComponent> components;

    public Clickable(final String msg) {
        this.components = new ArrayList<TextComponent>();
        final TextComponent message = new TextComponent(Color.translate(msg));
        this.components.add(message);
    }

    public Clickable(final String msg, final String hoverMsg, final String clickString) {
        this.components = new ArrayList<TextComponent>();
        this.add(msg, hoverMsg, clickString);
    }

    public TextComponent add(final String msg, final String hoverMsg, final String clickString) {
        final TextComponent message = new TextComponent(Color.translate(msg));
        if (hoverMsg != null) {
            message.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder(Color.translate(hoverMsg)).create()));
        }
        if (clickString != null) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickString));
        }
        this.components.add(message);
        return message;
    }

    public void add(final String message) {
        this.components.add(new TextComponent(message));
    }

    public void sendToPlayer(final Player player) {
        player.spigot().sendMessage((BaseComponent[])this.asComponents());
    }

    public TextComponent[] asComponents() {
        return this.components.toArray(new TextComponent[0]);
    }

    public Clickable() {
        this.components = new ArrayList<TextComponent>();
    }
}
