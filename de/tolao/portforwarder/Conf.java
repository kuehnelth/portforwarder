package de.tolao.portforwarder;

import java.util.Properties;

public class Conf
{
    private Properties p;

    public Conf(java.io.InputStream inStream) throws java.io.IOException
    {
        p = new Properties();
        p.load(inStream);
    }

    public Conf() throws java.io.IOException {
        java.io.InputStream inStream = Conf.class.getResourceAsStream("portforwarder.properties");
        p = new Properties();
        p.load(inStream);
        inStream.close();
    }

    public String get(String key) {
        return p.getProperty(key);
    }
}
