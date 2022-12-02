package org.openmrs.module.pihmalawi.activator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.authentication.AuthenticationConfig;
import org.openmrs.module.authenticationui.AuthenticationUiModuleConfig;
import org.openmrs.module.pihmalawi.PihMalawiConstants;
import org.openmrs.module.pihmalawi.metadata.CommonMetadata;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import static org.openmrs.module.authentication.AuthenticationConfig.SCHEME;
import static org.openmrs.module.authentication.AuthenticationConfig.SCHEME_CONFIG_PREFIX_TEMPLATE;
import static org.openmrs.module.authentication.AuthenticationConfig.SCHEME_ID;
import static org.openmrs.module.authentication.AuthenticationConfig.SCHEME_TYPE_TEMPLATE;
import static org.openmrs.module.authentication.AuthenticationConfig.WHITE_LIST;

public class AuthenticationInitializer implements Initializer {

    protected static Log log = LogFactory.getLog(AuthenticationInitializer.class);

    public static final String BASIC = "basic";
    public static final String SECRET = "secret";
    public static final String TOTP = "totp";
    public static final String TWO_FACTOR = "2fa";

    @Override
    public void started() {

        // Add this classloader
        AuthenticationConfig.registerClassLoader(AuthenticationInitializer.class.getClassLoader());

        // If no authentication scheme is explicitly configured, default to basic
        AuthenticationConfig.setProperty(SCHEME, "basic");

        // We set up white list as everything needed for the basic login page and any additional scheme login page
        // Add in any additional white list pages that are included in the config

        Set<String> whitelist = new HashSet<>();
        whitelist.add("/login.htm");
        whitelist.add("/authenticationui/login/login.page");
        whitelist.add("/appui/session/getLoginLocations.action");
        whitelist.add("/csrfguard");
        whitelist.add("*.js");
        whitelist.add("*.css");
        whitelist.add("*.gif");
        whitelist.add("*.jpg");
        whitelist.add("*.png");
        whitelist.add("*.ico");
        whitelist.add("*.ttf");
        whitelist.add("*.woff");

        // Set up all the supported authentication schemes with default values.
        // Allow overriding with values from the config

        // Basic Authentication Scheme.  This provides basic auth + session location selection
        {
            String className = "org.openmrs.module.authentication.web.BasicWithLocationAuthenticationScheme";
            Properties p = new Properties();
            p.put("loginPage", "/authenticationui/login/login.page");
            p.put("usernameParam", "username");
            p.put("passwordParam", "password");
            p.put("locationParamName", "sessionLocation");
            p.put("locationRequired", "false");
            p.put("onlyLocationsWithTag", "Login Location");
            p.put("locationSessionAttributeName", PihMalawiConstants.SESSION_LOCATION_ID);
            p.put("lastLocationCookieName", PihMalawiConstants.COOKIE_NAME_LAST_SESSION_LOCATION);
            addScheme(BASIC, className, p, whitelist);
        }

        // Secret Question Authentication Scheme.  This is an available 2nd factor
        {
            String className = "org.openmrs.module.authentication.web.SecretQuestionAuthenticationScheme";
            Properties p = new Properties();
            p.put("loginPage", "/authenticationui/login/loginSecret.page");
            p.put("configurationPage", "/authenticationui/account/changeSecurityQuestion.page?schemeId={schemeId}&userId={userId}");
            addScheme(SECRET, className, p, whitelist);
        }

        // Totp Authentication Scheme.  This is an available 2nd factor
        {
            String className = "org.openmrs.module.authentication.web.TotpAuthenticationScheme";
            Properties p = new Properties();
            p.put("qrCodeIssuer", "PIHEMR");
            p.put("loginPage", "/authenticationui/login/loginTotp.page");
            p.put("configurationPage", "/authenticationui/account/configureTotp.page?schemeId={schemeId}&userId={userId}");
            addScheme(TOTP, className, p, whitelist);
        }

        // Two-Factor Authentication Scheme.
        {
            String className = "org.openmrs.module.authentication.web.TwoFactorAuthenticationScheme";
            Properties p = new Properties();
            p.put("primaryOptions", BASIC);
            p.put("secondaryOptions", SECRET + "," + TOTP);
            addScheme(TWO_FACTOR, className, p, whitelist);
        }

        AuthenticationConfig.setProperty(WHITE_LIST, String.join(",", whitelist));

        log.info("Authentication Schemes Configured");
        Properties p = AuthenticationConfig.getConfig();
        Set<String> sortedKeys = new TreeSet<>(p.stringPropertyNames());
        for (String key : sortedKeys) {
            log.info(key + " = " + p.getProperty(key));
        }

        AuthenticationUiModuleConfig.setHeaderLogoUrl("pihmalawi:images/openMrsLogo.png");
        AuthenticationUiModuleConfig.setHomePageUrl("/index.htm");
        AuthenticationUiModuleConfig.setRequireLoginLocation(false);
        AuthenticationUiModuleConfig.setLastLocationCookieName(PihMalawiConstants.COOKIE_NAME_LAST_SESSION_LOCATION);
        AuthenticationUiModuleConfig.setLoginWelcomeMessage(null);
        AuthenticationUiModuleConfig.setLoginWarningIfNotChrome(null);
        AuthenticationUiModuleConfig.setAllowPasswordReset(false);
        AuthenticationUiModuleConfig.setAccountAdminPrivilege("App: coreapps.systemAdministration");
        AuthenticationUiModuleConfig.setPhoneNumberPersonAttributeType(CommonMetadata.TELEPHONE_NUMBER_ATTRIBUTE_TYPE);
        AuthenticationUiModuleConfig.setDefaultLocationUserProperty(null);
    }

    @Override
    public void stopped() {

    }

    /**
     * Add configuration for a scheme with the given schemeId, if a scheme with this schemeId is not already configured
     */
    protected void addScheme(String schemeId, String className, Properties config, Set<String> whitelist) {
        String schemeTypeProperty = SCHEME_TYPE_TEMPLATE.replace(SCHEME_ID, schemeId);
        if (StringUtils.isBlank(AuthenticationConfig.getProperty(schemeTypeProperty))) {
            AuthenticationConfig.setProperty(schemeTypeProperty, className);
            if (config != null) {
                for (String propertyName : config.stringPropertyNames()) {
                    String key = SCHEME_CONFIG_PREFIX_TEMPLATE.replace(SCHEME_ID, schemeId) + propertyName;
                    String value = config.getProperty(propertyName);
                    AuthenticationConfig.setProperty(key, value);
                    if (propertyName.equalsIgnoreCase("loginPage")) {
                        whitelist.add(value);
                    }
                }
            }
        }
    }
}
