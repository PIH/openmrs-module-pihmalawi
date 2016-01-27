package org.openmrs.module.pihmalawi.page.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.pihmalawi.PihMalawiConstants;
import org.openmrs.module.pihmalawi.PihMalawiWebConstants;
import org.openmrs.module.pihmalawi.metadata.CommonMetadata;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.web.WebConstants;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public class LoginPageController {
    private static final String GET_LOCATIONS = "Get Locations";

    protected final Log log = LogFactory.getLog(getClass());

    /**
     * @should redirect the user to the home page if they are already authenticated
     * @should show the user the login page if they are not authenticated
     * @should set redirectUrl in the page model if any was specified in the request
     * @should set the referer as the redirectUrl in the page model if no redirect param exists
     * @should set redirectUrl in the page model if any was specified in the session
     */
    public String get(PageModel model,
                      UiUtils ui,
                      PageRequest pageRequest,
                      @CookieValue(value = PihMalawiWebConstants.COOKIE_NAME_LAST_SESSION_LOCATION, required = false) String lastSessionLocationId,
                      @SpringBean("locationService") LocationService locationService,
                      @SpringBean("appFrameworkService") AppFrameworkService appFrameworkService) {

        if (Context.isAuthenticated()) {
            return "redirect:findPatient.htm";
        }

        String redirectUrl = getStringSessionAttribute(PihMalawiWebConstants.SESSION_ATTRIBUTE_REDIRECT_URL, pageRequest.getRequest());
        if (StringUtils.isBlank(redirectUrl))
            redirectUrl = pageRequest.getRequest().getParameter(PihMalawiWebConstants.REQUEST_PARAMETER_NAME_REDIRECT_URL);

        if (StringUtils.isBlank(redirectUrl))
            redirectUrl = pageRequest.getRequest().getHeader("Referer");

        if (redirectUrl == null)
            redirectUrl = "";

        model.addAttribute(PihMalawiWebConstants.REQUEST_PARAMETER_NAME_REDIRECT_URL, redirectUrl);
        Location lastSessionLocation = null;
        List<Location> systemLocations = null;
        try {
            Context.addProxyPrivilege(PrivilegeConstants.VIEW_LOCATIONS);
            Context.addProxyPrivilege(GET_LOCATIONS);
            //model.addAttribute("locations", appFrameworkService.getLoginLocations());
            CommonMetadata commonMetadata = new CommonMetadata();
            systemLocations = commonMetadata.getSystemLocations();
            lastSessionLocation = locationService.getLocation(Integer.valueOf(lastSessionLocationId));
        }
        catch (Exception ex) {
            // pass
        }
        finally {
            Context.removeProxyPrivilege(PrivilegeConstants.VIEW_LOCATIONS);
            Context.removeProxyPrivilege(GET_LOCATIONS);
        }

        model.addAttribute("lastSessionLocation", lastSessionLocation);
        model.addAttribute("locations", systemLocations);
        return null;
    }

    /**
     * Processes requests to authenticate a user
     *
     * @param username
     * @param password
     * @param sessionLocationId
     * @param locationService
     * @param ui {@link UiUtils} object
     * @param pageRequest {@link PageRequest} object
     * @return
     * @should redirect the user back to the redirectUrl if any
     * @should redirect the user to the home page if the redirectUrl is the login page
     * @should send the user back to the login page if an invalid location is selected
     * @should send the user back to the login page when authentication fails
     */
    public String post(@RequestParam(value = "username", required = false) String username,
                       @RequestParam(value = "password", required = false) String password,
                       @RequestParam(value = "sessionLocation", required = false) Integer sessionLocationId,
                       @SpringBean("locationService") LocationService locationService, UiUtils ui,
                       PageRequest pageRequest
                       ) {

        String redirectUrl = pageRequest.getRequest().getParameter(PihMalawiWebConstants.REQUEST_PARAMETER_NAME_REDIRECT_URL);
        redirectUrl = getRelativeUrl(redirectUrl, pageRequest);
        HttpSession httpSession = pageRequest.getRequest().getSession();
        Location sessionLocation = null;
        if (sessionLocationId != null && (sessionLocationId != null && sessionLocationId.intValue() > 0)) {
            try {
                // TODO as above, grant this privilege to Anonymous instead of using a proxy privilege
                Context.addProxyPrivilege(PrivilegeConstants.VIEW_LOCATIONS);
                Context.addProxyPrivilege(GET_LOCATIONS);
                sessionLocation = locationService.getLocation(sessionLocationId);
            }
            finally {
                Context.removeProxyPrivilege(PrivilegeConstants.VIEW_LOCATIONS);
                Context.removeProxyPrivilege(GET_LOCATIONS);
            }
        }

        if (sessionLocationId != null) {
            // Set a cookie, so next time someone logs in on this machine, we can default to that same location
            pageRequest.setCookieValue(PihMalawiWebConstants.COOKIE_NAME_LAST_SESSION_LOCATION, sessionLocationId.toString());
        }

        try {
            Context.authenticate(username, password);

            if (Context.isAuthenticated()) {
                pageRequest.getRequest().getSession().removeAttribute(WebConstants.OPENMRS_MSG_ATTR);
                if (log.isDebugEnabled()) {
                    log.debug("User has successfully authenticated");
                }

                if (sessionLocation != null && httpSession!=null) {
                    httpSession.setAttribute(PihMalawiWebConstants.SESSION_LOCATION_ID, sessionLocation.getId());
                }

                if (StringUtils.isNotBlank(redirectUrl)) {
                    //don't redirect back to the login page on success nor an external url
                    if (!redirectUrl.contains("login.")) {
                        if (log.isDebugEnabled())
                            log.debug("Redirecting user to " + redirectUrl);

                        return "redirect:" + redirectUrl;
                    } else {
                        if (log.isDebugEnabled())
                            log.debug("Redirect contains 'login.', redirecting to home page");
                    }
                }

                return "redirect:/openmrs/findPatient.htm";
            }
        }
        catch (ContextAuthenticationException ex) {
            if (log.isDebugEnabled())
                log.debug("Failed to authenticate user");

            pageRequest.getSession().setAttribute(PihMalawiWebConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
                    ui.message(PihMalawiConstants.MODULE_ID + ".error.login.fail"));
        }



        if (log.isDebugEnabled())
            log.debug("Sending user back to login page");

        pageRequest.getSession().setAttribute(PihMalawiWebConstants.SESSION_ATTRIBUTE_REDIRECT_URL, redirectUrl);

        return "redirect:" + ui.pageLink(PihMalawiConstants.MODULE_ID, "login");
    }

    private String getStringSessionAttribute(String attributeName, HttpServletRequest request) {
        String attributeValue = (String) request.getSession().getAttribute(attributeName);
        request.getSession().removeAttribute(attributeName);
        return attributeValue;
    }

    public String getRelativeUrl(String url, PageRequest pageRequest) {
        if (url == null)
            return null;

        if (url.startsWith("/") || (!url.startsWith("http://") && !url.startsWith("https://"))) {
            return url;
        }

        //This is an absolute url, discard the protocal, domain name/host and port section
        int indexOfContextPath = url.indexOf(pageRequest.getRequest().getContextPath());
        if (indexOfContextPath >= 0) {
            url = url.substring(indexOfContextPath);
            log.debug("Relative redirect:" + url);

            return url;
        }

        return null;
    }

}
