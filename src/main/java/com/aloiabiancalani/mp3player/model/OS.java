package com.aloiabiancalani.mp3player.model;

public  class OS {

    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }

    public static boolean isLinux() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("nux") || os.contains("nix");
    }

}
