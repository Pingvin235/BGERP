package org.bgerp.plugin.msg.sms;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

import ru.bgcrm.util.ParameterMap;
import ru.bgerp.util.Log;

public class SenderTele2 extends Sender {
    private static final Log log = Log.getLog();

    private final String url;
    private final String login;
    private final String password;
    private final String shortcode;

    protected SenderTele2(ParameterMap setup) throws InitStopException {
        super(setup);
        url = setup.get("url", "https://newbsms.tele2.ru/api/?operation=send");
        shortcode = setup.get("shortcode", "bgerp.org");
        login = setup.get("login");
        password = setup.get("password");
        initWhen(StringUtils.isNoneBlank(login, password));
    }

    @Override
    public void send(String number, String text) {
        try {
            String base64orig = login + ":" + password;
            String base64encode = Base64.getEncoder().encodeToString(base64orig.getBytes(StandardCharsets.UTF_8));

            var httpclient = HttpClients.createDefault();

            HttpPost request = new HttpPost();
            JSONObject bodyRequest = new JSONObject();

            bodyRequest.put("msisdn", Long.valueOf(number));
            bodyRequest.put("shortcode", shortcode);
            bodyRequest.put("text", text);

            StringEntity entity = new StringEntity(bodyRequest.toString(), "UTF-8");
            request.setEntity(entity);
            request.setURI(URI.create(url));
            request.addHeader(new BasicHeader("Authorization", "Basic " + base64encode));
            request.addHeader(new BasicHeader("Content-Type", "application/json; charset=utf-8"));
            log.debug("Sending: %s", request);

            HttpResponse response = httpclient.execute(request);
            log.debug("Response code: %s", response.getStatusLine().getStatusCode() + ", reason: " + response.getStatusLine().getReasonPhrase());

        } catch (Exception e) {
            log.error(e);
        }
    }
}
