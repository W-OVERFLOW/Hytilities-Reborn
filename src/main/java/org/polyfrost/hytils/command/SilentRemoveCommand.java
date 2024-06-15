/*
 * Hytils Reborn - Hypixel focused Quality of Life mod.
 * Copyright (C) 2020, 2021, 2022, 2023  Polyfrost, Sk1er LLC and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.polyfrost.hytils.command;

import org.polyfrost.universal.ChatColor;
import org.polyfrost.universal.UChat;
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.Command;
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.Description;
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.Main;
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.SubCommand;
import org.polyfrost.hytils.handlers.chat.modules.triggers.SilentRemoval;
import com.mojang.authlib.GameProfile;

import java.util.Set;
import java.util.regex.Pattern;

@Command("silentremove")
public class SilentRemoveCommand {

    protected static final Pattern usernameRegex = Pattern.compile("\\w{1,16}");

    @Main
    public void handle() {
        UChat.chat(ChatColor.RED + "Usage: /silentremove <player>");
    }

    @Main(description = "Adds or removes a player from the silent list.")
    private void main(@Description("Player Name") GameProfile player) {
        String name = player.getName();
        if (!usernameRegex.matcher(name).matches()) {
            UChat.chat(ChatColor.RED + "Invalid username.");
            return;
        }
        final Set<String> silentUsers = SilentRemoval.getSilentUsers();
        if (silentUsers.contains(name)) {
            silentUsers.remove(name);
            UChat.chat("&aRemoved &e" + name + " &afrom the removal queue.");
            return;
        }

        silentUsers.add(name);
        UChat.chat("&aAdded &e" + name + " &ato the removal queue.");
    }

    @SubCommand(description = "Clears the silent removal queue.")
    private void clear() {
        final Set<String> silentUsers = SilentRemoval.getSilentUsers();
        if (silentUsers.isEmpty()) {
            UChat.chat(ChatColor.RED + "Silent removal list is already empty.");
            return;
        }
        silentUsers.clear();
        UChat.chat(ChatColor.GREEN + "Cleared the silent removal list.");
    }
}
