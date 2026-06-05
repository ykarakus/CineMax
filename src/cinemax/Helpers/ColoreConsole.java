package cinemax.Helpers;

public final class ColoreConsole {

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";

    private ColoreConsole() {
    }

    private static String colora(String color, String text) {
        return color + text + RESET;
    }

    public static String successo(String text) {
        return colora(GREEN, text);
    }

    public static String avvertimento(String text) {
        return colora(YELLOW, text);
    }

    public static String errore(String text) {
        return colora(RED, text);
    }
    
    public static String benvenuto(String text) {
        return colora(GREEN, text);
    }
    public static String header(String text) {
        return colora(CYAN, text);
    }
    
    public static String headerRisultati(String text) {
        return colora(BLUE, text);
    }
    public static String titoloFilm(String text) {
        return colora(PURPLE, text);
    }
}