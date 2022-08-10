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
