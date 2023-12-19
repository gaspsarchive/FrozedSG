package club.frozed.frozedsg.utils.command;

import club.frozed.frozedsg.PotSG;

public abstract class BaseCommand {
    public PotSG main = PotSG.getInstance();

    public BaseCommand() {
        this.main.getFramework().registerCommands(this);
    }

    public abstract void onCommand(CommandArgs var1);
}

