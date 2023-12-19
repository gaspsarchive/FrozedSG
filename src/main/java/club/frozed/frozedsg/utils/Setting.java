package club.frozed.frozedsg.utils;

import org.bukkit.Material;

import java.util.Arrays;

public class Setting {
    private boolean enabled;
    private Material material;
    private String name;
    private String[] description;
    private int data;
    private int requiredPoints;

    public Setting(String name, Material material, int requiredPoints, String... description) {
        this.material = material;
        this.name = name;
        this.description = description;
        this.requiredPoints = requiredPoints;
    }

    public Setting(String name, Material material, boolean enabled, int requiredPoints, String... description) {
        this.material = material;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.requiredPoints = requiredPoints;
    }

    public Setting(String name, Material material, boolean enabled, int data, int requiredPoints, String... description) {
        this.material = material;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.data = data;
        this.requiredPoints = requiredPoints;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Material getMaterial() {
        return this.material;
    }

    public String getName() {
        return this.name;
    }

    public String[] getDescription() {
        return this.description;
    }

    public int getData() {
        return this.data;
    }

    public int getRequiredPoints() {
        return this.requiredPoints;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

    public void setData(int data) {
        this.data = data;
    }

    public void setRequiredPoints(int requiredPoints) {
        this.requiredPoints = requiredPoints;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Setting)) {
            return false;
        } else {
            Setting other = (Setting)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.isEnabled() != other.isEnabled()) {
                return false;
            } else {
                label49: {
                    Object this$material = this.getMaterial();
                    Object other$material = other.getMaterial();
                    if (this$material == null) {
                        if (other$material == null) {
                            break label49;
                        }
                    } else if (this$material.equals(other$material)) {
                        break label49;
                    }

                    return false;
                }

                label42: {
                    Object this$name = this.getName();
                    Object other$name = other.getName();
                    if (this$name == null) {
                        if (other$name == null) {
                            break label42;
                        }
                    } else if (this$name.equals(other$name)) {
                        break label42;
                    }

                    return false;
                }

                if (!Arrays.deepEquals(this.getDescription(), other.getDescription())) {
                    return false;
                } else if (this.getData() != other.getData()) {
                    return false;
                } else {
                    return this.getRequiredPoints() == other.getRequiredPoints();
                }
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Setting;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        result = result * 59 + (this.isEnabled() ? 79 : 97);
        Object $material = this.getMaterial();
        result = result * 59 + ($material == null ? 43 : $material.hashCode());
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        result = result * 59 + Arrays.deepHashCode(this.getDescription());
        result = result * 59 + this.getData();
        result = result * 59 + this.getRequiredPoints();
        return result;
    }

    public String toString() {
        return "Setting(enabled=" + this.isEnabled() + ", material=" + this.getMaterial() + ", name=" + this.getName() + ", description=" + Arrays.deepToString(this.getDescription()) + ", data=" + this.getData() + ", requiredPoints=" + this.getRequiredPoints() + ")";
    }
}
