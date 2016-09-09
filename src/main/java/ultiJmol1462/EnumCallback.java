package ultiJmol1462;

/**
 * Created by Fabrice.Moriaud on 09.09.2016.
 */

public enum EnumCallback {

    ANIMFRAME,
    APPLETREADY,
    ATOMMOVED,
    CLICK,
    ECHO,
    ERROR,
    EVAL,
    HOVER,
    LOADSTRUCT,
    MEASURE,
    MESSAGE,
    MINIMIZATION,
    PICK,
    RESIZE,
    SCRIPT,
    SYNC;

    public static EnumCallback getCallback(String name) {

        name = name.toUpperCase();
        name = name.substring(0, Math.max(name.indexOf("CALLBACK"), 0));
        for (EnumCallback item : values())
            if (item.name().equalsIgnoreCase(name))
                return item;
        return null;
    }

    private static String nameList;

    public static synchronized String getNameList() {
        if (nameList == null) {
            StringBuffer names = new StringBuffer();
            for (EnumCallback item : values())
                names.append(item.name().toLowerCase()).append("Callback;");
            nameList = names.toString();
        }
        return nameList;
    }
}
