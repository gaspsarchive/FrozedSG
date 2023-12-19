package club.frozed.frozedsg.utils.pagination;

import club.frozed.frozedsg.utils.pagination.buttons.BackButton;
import club.frozed.frozedsg.utils.pagination.buttons.Button;
import club.frozed.frozedsg.utils.pagination.buttons.JumpToPageButton;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ViewAllPagesMenu extends Menu {
    @NonNull PaginatedMenu menu;

    public String getTitle(Player player) {
        return "Jump to page";
    }

    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap();
        buttons.put(0, new BackButton(this.menu));
        int index = 10;

        for(int i = 1; i <= this.menu.getPages(player); ++i) {
            buttons.put(index++, new JumpToPageButton(i, this.menu, this.menu.getPage() == i));
            if ((index - 8) % 9 == 0) {
                index += 2;
            }
        }

        return buttons;
    }

    public boolean isAutoUpdate() {
        return true;
    }

    public ViewAllPagesMenu(@NonNull PaginatedMenu menu) {
        if (menu == null) {
            throw new NullPointerException("menu is marked non-null but is null");
        } else {
            this.menu = menu;
        }
    }

    public @NonNull PaginatedMenu getMenu() {
        return this.menu;
    }
}
