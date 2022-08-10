
# Keycloak OTP SMS Authenticator Plugin

The purpose of this plugin is to enable verification with OTP SMS code on systems using Keycloak.

The plugin uses NetGSM as the sms resource provider. Before installing the plugin, do not forget to update its configurations.

To be able to use the 2fa-sms-authenticator after you deployed the JAR profile to the the /deployments directory, you'll have to create and configure a Keycloak authentication flow in your realm and use it in a binding.



## Authors and Thanks

- [@dasniko](https://github.com/dasniko) for design and development.

  
## Kullanılan Teknolojiler

**Client:** Keycloak

**Server:** Java

  