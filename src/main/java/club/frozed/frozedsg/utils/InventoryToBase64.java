package club.frozed.frozedsg.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class InventoryToBase64 {
    public InventoryToBase64() {
    }

    public static String itemToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(items.length);
            ItemStack[] var3 = items;
            int var4 = items.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                ItemStack item = var3[var5];
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception var7) {
            throw new IllegalStateException("Unable to save item stacks.", var7);
        }
    }

    public static ItemStack[] itemFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for(int i = 0; i < items.length; ++i) {
                items[i] = (ItemStack)dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException var5) {
            throw new IOException("Unable to decode class type.", var5);
        }
    }
}
