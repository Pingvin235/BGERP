package ru.bgcrm.util;

import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

public class MailConfig {
    private final ParameterMap configMap;
    private final String host;
    private final String email;
    private final String from;
    private final String store;
    private final int port;
    private final String login;
    private final String pswd;

    public static final FetchProfile FETCH_PROFILE = new FetchProfile();
    static {
        FETCH_PROFILE.add(FetchProfile.Item.ENVELOPE);
        FETCH_PROFILE.add("To");
        FETCH_PROFILE.add("CC");
        FETCH_PROFILE.add("Message-ID");
        FETCH_PROFILE.add("Received");
    }

    public MailConfig(ParameterMap config) {
        configMap = config;
        host = config.get("host");
        email = config.get("email");
        from = config.get("from", email);
        store = config.get("store", "imap");
        port = config.getInt("port", 0);
        login = config.get("login");
        pswd = config.get("pswd");
    }

    public String getEmail() {
        return email;
    }

    public String getFrom() {
        return from;
    }

    public boolean check() {
        return Utils.notBlankString(email) && Utils.notBlankString(login) && Utils.notBlankString(pswd);
    }

    public Session getImapSession() throws Exception {
        Properties props = new Properties();
        props.setProperty("mail.imap.timeout", "7000");
        props.setProperty("mail.imap.partialfetch", "false");
        props.setProperty("mail.imaps.timeout", "7000");
        props.setProperty("mail.imaps.partialfetch", "false");

        // IMAP SSL
        if ("imaps".equals(store)) {
            props.setProperty("mail.imap.ssl.enable", "true");
            //props.setProperty("mail.imap.ssl.checkserveridentity", "false");
            props.setProperty("mail.imaps.ssl.trust", "*");
        }

        props.setProperty("mail.debug", String.valueOf(configMap.getBoolean("debug", false)));

        return Session.getInstance(props, null);
    }

    public Store getImapStore() throws Exception {
        Store store = getImapSession().getStore(this.store);
        if (port > 0) {
            store.connect(host, port, login, pswd);
        } else {
            store.connect(host, login, pswd);
        }

        return store;
    }

    public Session getSmtpSession(ParameterMap defaultParamMap) {
        Session session = null;

        String user = getOptionFromConfigs(configMap, defaultParamMap, "mail.smtp.user", null);
        String pswd = getOptionFromConfigs(configMap, defaultParamMap, "mail.smtp.pswd", null);

        //TODO: Проверка user, pswd на заполненность.

        final String proto = configMap.get("mail.transport.protocol", "smtp");

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", proto);
        props.setProperty("mail." + proto + ".host", getOptionFromConfigs(configMap, defaultParamMap, "mail.smtp.host", ""));
        props.setProperty("mail." + proto + ".port", getOptionFromConfigs(configMap, defaultParamMap, "mail.smtp.port", ""));
        props.setProperty("mail." + proto + ".localhost", getOptionFromConfigs(configMap, defaultParamMap, "mail.smtp.localhost", ""));

        props.setProperty("mail.debug", String.valueOf(configMap.getBoolean("mail.debug", false)));

        props.put("mail." + proto + ".timeout", "10000");
        props.put("mail." + proto + ".connectiontimeout", "10000");

        // параметры для SSL
        //props.putAll( paramMap.sub( "mail.properties." ) );

        if ("smtps".equals(proto)) {
            props.put("mail.smtps.ssl.trust", "*");
        }

        Authenticator authenticator = null;
        if (Utils.notBlankString(user) && Utils.notBlankString(pswd)) {
            authenticator = new Authenticator(user, pswd);
            props.setProperty("mail." + proto + ".auth", "true");
            props.setProperty("mail." + proto + ".submitter", authenticator.getPasswordAuthentication().getUserName());
        }

        session = Session.getInstance(props, authenticator);

        return session;
    }

    private static final String getOptionFromConfigs(ParameterMap paramMap, ParameterMap defaultParamMap, String paramName, String defaultValue) {
        return paramMap.get(paramName, defaultParamMap != null ? defaultParamMap.get(paramName, defaultValue) : defaultValue);
    }

    private static class Authenticator extends javax.mail.Authenticator {
        private PasswordAuthentication authentication;

        public Authenticator(String user, String password) {
            authentication = new PasswordAuthentication(user, password);
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return authentication;
        }
    }
}