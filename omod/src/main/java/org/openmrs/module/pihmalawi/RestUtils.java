package org.openmrs.module.pihmalawi;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.sync.server.RemoteServer;
import org.openmrs.util.ConfigUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestUtils {

    private static final String MAC_ADDRESS_PATTERN = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

    public static HashMap<String, Object> convertServerObject(RemoteServer server) {
        HashMap<String, Object> objectMap = new HashMap<>();
        if (server != null) {
            objectMap.put("serverId", server.getServerId());
            objectMap.put("uuid", server.getUuid());
            objectMap.put("serverType", server.getServerType());
            objectMap.put("childUsername", server.getChildUsername());
            objectMap.put("classesNotSent", server.getClassesNotSent());
            objectMap.put("classesNotReceived", server.getClassesNotReceived());
            objectMap.put("nickname", server.getNickname());
            objectMap.put("username", server.getUsername());
            objectMap.put("password", server.getPassword());
        }
        return objectMap;
    }

    public static ResponseEntity<Map<String, Object>> errorResponse(Throwable t) {
        Map<String, Object> data = new LinkedHashMap<>();
        List<String> errorMessages = new ArrayList<>();
        while (t != null && !errorMessages.contains(t.getMessage())) {
            errorMessages.add(t.getMessage());
            t = t.getCause();
        }
        data.put("errorMessages",errorMessages);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(data);
    }

    public static boolean isValidMacAddress(String macAddress) {
        if ( StringUtils.isNotBlank(macAddress) ) {
            Pattern pattern = Pattern.compile(MAC_ADDRESS_PATTERN);
            Matcher matcher = pattern.matcher(macAddress);
            return matcher.matches();
        } else {
            return false;
        }
    }
    public static boolean anyMacAddressesAllowed(List<String> macAddresses) {
        boolean allowed = false;
        if (macAddresses != null && StringUtils.isNotBlank(macAddresses.get(0)) && isValidMacAddress(macAddresses.get(0))) {
            String addresses = ConfigUtil.getGlobalProperty(PihMalawiConstants.SYNC_ALLOW_MAC_ADDRESSES_GP_NAME);
            allowed = StringUtils.isNotBlank(addresses) && addresses.toLowerCase().contains(macAddresses.get(0).toLowerCase());
        }
        return allowed;
    }
}
