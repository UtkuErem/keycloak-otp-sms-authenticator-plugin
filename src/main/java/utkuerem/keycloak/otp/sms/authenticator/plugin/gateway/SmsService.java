package utkuerem.keycloak.otp.sms.authenticator.plugin.gateway;

public interface SmsService {
    void send(String phoneNumber, String message);
}