/*
 *  LICENSE
 * AdvancedPlHide
 * -------------
 * Copyright (C) 2021 - 2021 BlueTree242
 * -------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 *  END
 */

package tk.bluetree242.advancedplhide;

import tk.bluetree242.advancedplhide.config.subcompleter.ConfSubCompleterList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompleterModifier {
    private static final List BAD_COMMANDS = Arrays.asList("ver", "version", "plugins", "bukkit:plugins", "bukkit:ver", "bukkit:version", "about", "bukkit:about", "?", "minecraft:?");

    public static void removePluginPrefix(CommandCompleterList list) {
        for (CommandCompleter completer : new ArrayList<>(list)) {
            if (completer.getName().contains(":")) completer.remove();
        }
    }


    public static void handleCompleter(CommandCompleterList list, Group playerGroup, boolean whitelist) {
        if (Platform.get().getConfig().remove_plugin_prefix())
            removePluginPrefix(list);

        if (playerGroup != null) {
            if (!whitelist) applyBlacklist(list, playerGroup.getCompleteCommands());
            else applyWhitelist(list, playerGroup.getCompleteCommands());
        }
    }

    public static void handleSubCompleter(SubCommandCompleterList list, Group playerGroup, boolean whitelist) {
        if (BAD_COMMANDS.contains(list.getName().toLowerCase())) {
            list.removeAll();
        }
        if (playerGroup == null) return;
        ConfSubCompleterList originConfList = playerGroup.getSubCompleters();
        ConfSubCompleterList confList = originConfList.ofCommand(list.getName());
        if (!whitelist) applyBlacklist(list, confList);
        else applyWhitelist(list, confList);
    }

    public static void applyBlacklist(SubCommandCompleterList list, ConfSubCompleterList originConfList) {
        for (SubCommandCompleter completer : new ArrayList<>(list)) {
            ConfSubCompleterList confList = originConfList.ofArgs(list.getArgs(completer));
            if (!confList.isEmpty()) {
                completer.remove();
            }
        }
    }

    public static void applyWhitelist(SubCommandCompleterList list, ConfSubCompleterList originConfList) {
        if (originConfList.isEmpty()) return;
        for (SubCommandCompleter completer : new ArrayList<>(list)) {
            ConfSubCompleterList confList = originConfList.ofArgs(list.getArgs(completer));
            if (confList.isEmpty()) {
                completer.remove();
            }
        }
    }


    public static void applyBlacklist(CommandCompleterList list, List<CommandCompleter> toBlacklist) {
        List<String> commands = new ArrayList<>();
        List<String> plugins = new ArrayList<>();
        for (CommandCompleter completer : toBlacklist) {
            if (!completer.getName().startsWith("from:"))
                commands.add(completer.getName());
            else {
                String name = completer.getName().replaceFirst("from:", "");
                plugins.add(name);
            }
        }
        for (CommandCompleter completer : new ArrayList<>(list)) {
            if (commands.contains(completer.getName())) {
                completer.remove();
            } else if (plugins.contains(Platform.get().getPluginForCommand(completer.getName()))) {
                completer.remove();
            }
        }
    }

    public static void applyWhitelist(CommandCompleterList list, List<CommandCompleter> toWhitelist) {
        List<String> commands = new ArrayList<>();
        List<String> plugins = new ArrayList<>();
        for (CommandCompleter completer : toWhitelist) {
            if (!completer.getName().startsWith("from:"))
                commands.add(completer.getName());
            else {
                String name = completer.getName().replaceFirst("from:", "");
                plugins.add(name);
            }
        }
        for (CommandCompleter completer : new ArrayList<>(list)) {
            if (!commands.contains(completer.getName())) {
                if (!plugins.contains(Platform.get().getPluginForCommand(completer.getName())))
                    completer.remove();
            }
        }
    }
}
