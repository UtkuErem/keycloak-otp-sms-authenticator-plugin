package utkuerem.keycloak.otp.sms.authenticator.plugin;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class SmsAuthenticatorFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "sms-authenticator";

    @Override
    public String getDisplayType() {
        return "SMS Code Authenticator";
    }

    @Override
    public String getReferenceCategory() {
        return "otp";
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "SMS ile kullanıcının mobil cihazına gönderilen OTP kodunu doğrular.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return List.of(new ProviderConfigProperty("length", "Kod Uzunluğu", "Gönderilecek olan kodun kaç haneli olacağı bilgisi",
                ProviderConfigProperty.STRING_TYPE, 6),
                new ProviderConfigProperty("ttl", "Kod Yaşam Uzunluğu", "Gönderilecek olan kodun kaç saniye aktif kalacağı bilgisi",
                        ProviderConfigProperty.STRING_TYPE, 300),
                new ProviderConfigProperty("senderId", "senderId", "Kullanıcının telefonuna gidecek olan mesajın kimden geldiği bilgisi",
                        ProviderConfigProperty.STRING_TYPE, "Albert"),
                new ProviderConfigProperty("demo", "Demo mode", "Demo mod açıkken, SMS gitmez, ama server log'larına mesaj yazılır", ProviderConfigProperty.BOOLEAN_TYPE, true)
                );
    }

    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        return new SmsAuthenticator();
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
