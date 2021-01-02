package ru.bgcrm.plugin;

/**
 * Plugin's supported language.
 * @author Shamil Vakhitov
 */
public enum Lang {
    RU("ru", "Русский"), 
    EN("en", "English"),
    DE("de", "Deutsch");

    private final String id;
    private final String title;

    private Lang(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Lang id: " + id + "; title: " + title;
    }
}