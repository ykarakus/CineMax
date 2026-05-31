package cinemax.Helpers;

public final class ConsoleColor {

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";

    private ConsoleColor() {
    }

    private static String colorize(String color, String text) {
        return color + text + RESET;
    }

    public static String success(String text) {
        return colorize(GREEN, text);
    }

    public static String warning(String text) {
        return colorize(YELLOW, text);
    }

    public static String error(String text) {
        return colorize(RED, text);
    }
    
    public static String welcome(String text) {
        return colorize(GREEN, text);
    }
}