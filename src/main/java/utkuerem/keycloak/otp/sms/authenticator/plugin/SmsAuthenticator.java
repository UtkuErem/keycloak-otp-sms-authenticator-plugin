package utkuerem.keycloak.otp.sms.authenticator.plugin;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.common.util.SecretGenerator;
import org.keycloak.models.*;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.theme.Theme;
import utkuerem.keycloak.otp.sms.authenticator.plugin.gateway.SmsServiceFactory;

import javax.ws.rs.core.Response;
import java.util.Locale;

public class SmsAuthenticator implements Authenticator {

    private static final String TPL_CODE = "login-sms.ftl";

    @Override
    public void authenticate(AuthenticationFlowContext authenticationFlowContext) {
        try {
            /* get authConfig, session and userModel */

            AuthenticatorConfigModel authenticatorConfig = authenticationFlowContext.getAuthenticatorConfig();
            KeycloakSession session = authenticationFlowContext.getSession();
            UserModel user = authenticationFlowContext.getUser();

            String phoneNumber = user.getFirstAttribute("phone_number");
            int length = Integer.parseInt(authenticatorConfig.getConfig().get("length"));
            int ttl = Integer.parseInt(authenticatorConfig.getConfig().get("ttl"));

            /* generate code and format ttl and set authNotes */

            String code = SecretGenerator.getInstance().randomString(length, SecretGenerator.DIGITS);
            AuthenticationSessionModel authenticationSession = authenticationFlowContext.getAuthenticationSession();
            authenticationSession.setAuthNote("code", code);
            authenticationSession.setAuthNote("ttl", Long.toString(System.currentTimeMillis() + (ttl * 1000L)));

            /* prepare window and SMS text */

            Theme loginTheme = session.theme().getTheme(Theme.Type.LOGIN);
            Locale locale = session.getContext().resolveLocale(user);

            String smsAuthText = loginTheme.getMessages(locale).getProperty("smsAuthText");
            String smsText = String.format(smsAuthText, code, Math.floorDiv(ttl, 60));

            SmsServiceFactory.get(authenticatorConfig.getConfig()).send(phoneNumber, smsText);

            authenticationFlowContext.challenge(authenticationFlowContext.form().setAttribute
                    ("realm", authenticationFlowContext.getRealm()).createForm(TPL_CODE));
        } catch (Exception e) {
            authenticationFlowContext.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR,
                    authenticationFlowContext.form().createErrorPage(Response.Status.INTERNAL_SERVER_ERROR));
        }
    }

    @Override
    public void action(AuthenticationFlowContext authenticationFlowContext) {
        try {
            String enteredCode = authenticationFlowContext.getHttpRequest().getDecodedFormParameters().getFirst("code");
            AuthenticationSessionModel authenticationSession = authenticationFlowContext.getAuthenticationSession();

            String code = authenticationSession.getAuthNote("code");
            String ttl = authenticationSession.getAuthNote("ttl");

            boolean isValid = enteredCode.equals(code);

            if (isValid) {
                if (Long.parseLong(ttl) < System.currentTimeMillis()) {
                    // expired
                    authenticationFlowContext.failureChallenge(AuthenticationFlowError.EXPIRED_CODE,
                            authenticationFlowContext.form().setError("smsAuthCodeExpired").createErrorPage(Response.Status.BAD_REQUEST));
                } else {
                    /* Done! */
                    authenticationFlowContext.success();
                }
            } else {
                /* wrong code */
                AuthenticationExecutionModel execution = authenticationFlowContext.getExecution();
                if (execution.isRequired()) {
                    authenticationFlowContext.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS,
                            authenticationFlowContext.form().setAttribute("realm", authenticationFlowContext.getRealm())
                                    .setError("smsAuthCodeInvalid").createForm(TPL_CODE));
                } else if (execution.isConditional() || execution.isAlternative()) {
                    authenticationFlowContext.attempted();
                }
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return user.getFirstAttribute("mobile_number") != null;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public void close() {
    }
}
