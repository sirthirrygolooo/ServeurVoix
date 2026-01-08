package org.hehe.sirthirrygolooo.config;

public class AppConfig {
    public static final int LISTEN_PORT = 65432;
//   Pour quand le serv sera relié
    public static final String CENTRAL_HOST = System.getenv("CENTRAL_HOST") != null ? System.getenv("CENTRAL_HOST") : "localhost";
    public static final int CENTRAL_PORT = 9999;
}