package utkuerem.keycloak.otp.sms.authenticator.plugin.gateway;

import org.jboss.logging.Logger;

import java.util.Map;

public class SmsServiceFactory {

    private static final Logger LOG = Logger.getLogger(SmsServiceFactory.class);

    public static SmsService get(Map<String, String> config) {
        if (Boolean.parseBoolean(config.getOrDefault("DEMO", "false"))) {
            return (phoneNumber, message) ->
                    LOG.warn(String.format("***** DEMO MODE ***** Would send SMS to %s with text: %s", phoneNumber, message));
        } else {
            return new NetgsmSMSService();
        }
    }
}