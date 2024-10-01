package com.project.classroom.constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public final class EnvKey {

    private static Properties properties = new Properties();

    static {
        try {
            URL url = EnvKey.class.getClassLoader().getResource("config.properties");
            if (url != null) {
                try (InputStream inputStream = url.openStream()) {
                    properties.load(inputStream);
                }
            } else {
                throw new RuntimeException("Can not found file application.properties");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String JWT_SECRET =  properties.getProperty("app.jwt.secret");

    public static int JWT_EXPIRATION =  Integer.parseInt(properties.getProperty("app.jwt.expiration"));

    public static String AES_SECRET_KEY =  properties.getProperty("aes.secret.key");

    public static String AES_IVECTOR_KEY =  properties.getProperty("aes.ivector.key");


}
