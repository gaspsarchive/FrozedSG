package club.frozed.frozedsg.utils.configurations;

import club.frozed.frozedsg.PotSG;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ConfigFile {
    public static ConfigFile instance;
    private YamlConfiguration configuration;
    private String name;
    private File file;

    public ConfigFile(String name) {
        this.name = name;
        this.file = new File(PotSG.getInstance().getDataFolder(), name + ".yml");
        if (!this.file.exists()) {
            PotSG.getInstance().saveResource(name + ".yml", false);
        }

        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        instance = this;
    }

    public boolean getBoolean(String path) {
        return this.configuration.contains(path) && this.configuration.getBoolean(path);
    }

    public double getDouble(String path) {
        return this.configuration.contains(path) ? this.configuration.getDouble(path) : 0.0;
    }

    public int getInt(String path) {
        return this.configuration.contains(path) ? this.configuration.getInt(path) : 0;
    }

    public String getString(String path) {
        return this.configuration.contains(path) ? ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path)) : "String at path: " + path + " not found!";
    }

    public String getUnColoredString(String path) {
        return this.configuration.contains(path) ? this.configuration.getString(path) : "String at path: " + path + " not found!";
    }

    public List<String> getStringList(String path) {
        if (!this.configuration.contains(path)) {
            return Arrays.asList("String List at path: " + path + " not found!");
        } else {
            ArrayList<String> strings = new ArrayList();
            Iterator var3 = this.configuration.getStringList(path).iterator();

            while(var3.hasNext()) {
                String string = (String)var3.next();
                strings.add(ChatColor.translateAlternateColorCodes('&', string));
            }

            return strings;
        }
    }

    public void load() {
        this.file = new File(PotSG.getInstance().getDataFolder(), this.name + ".yml");
        if (!this.file.exists()) {
            PotSG.getInstance().saveResource(this.name + ".yml", false);
        }

        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public void save() {
        try {
            this.configuration.save(this.file);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public YamlConfiguration getConfiguration() {
        return this.configuration;
    }

    public String getName() {
        return this.name;
    }

    public File getFile() {
        return this.file;
    }

    public void setConfiguration(YamlConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ConfigFile)) {
            return false;
        } else {
            ConfigFile other = (ConfigFile)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label47: {
                    Object this$configuration = this.getConfiguration();
                    Object other$configuration = other.getConfiguration();
                    if (this$configuration == null) {
                        if (other$configuration == null) {
                            break label47;
                        }
                    } else if (this$configuration.equals(other$configuration)) {
                        break label47;
                    }

                    return false;
                }

                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name != null) {
                        return false;
                    }
                } else if (!this$name.equals(other$name)) {
                    return false;
                }

                Object this$file = this.getFile();
                Object other$file = other.getFile();
                if (this$file == null) {
                    if (other$file != null) {
                        return false;
                    }
                } else if (!this$file.equals(other$file)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ConfigFile;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $configuration = this.getConfiguration();
        result = result * 59 + ($configuration == null ? 43 : $configuration.hashCode());
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $file = this.getFile();
        result = result * 59 + ($file == null ? 43 : $file.hashCode());
        return result;
    }

    public String toString() {
        return "ConfigFile(configuration=" + this.getConfiguration() + ", name=" + this.getName() + ", file=" + this.getFile() + ")";
    }

    public static ConfigFile getInstance() {
        return instance;
    }
}
