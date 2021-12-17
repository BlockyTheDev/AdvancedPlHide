package tk.bluetree242.advancedplhide.bukkit.impl;

import tk.bluetree242.advancedplhide.CommandCompleter;

public class StringCommandCompleter implements CommandCompleter {
    private final String name;
    private final StringCommandCompleterList list;

    public StringCommandCompleter(String name, StringCommandCompleterList list) {
        this.list = list;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void remove() {
        list.remove(this);
    }
}