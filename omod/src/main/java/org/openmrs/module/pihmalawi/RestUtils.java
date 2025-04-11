package org.openmrs.module.pihmalawi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openmrs.module.sync.server.RemoteServer;

public class RestUtils {

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
}
