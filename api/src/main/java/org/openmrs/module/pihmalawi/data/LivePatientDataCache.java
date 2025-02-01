/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.pihmalawi.data;

import org.openmrs.Location;
import org.openmrs.module.pihmalawi.common.JsonObject;
import org.openmrs.module.reporting.common.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Provides a construct for maintaining patient data
 */
public class LivePatientDataCache {

    //***** PROPERTIES *****

    private Map<String, Long> lastAccessTimes = new HashMap<String, Long>();
    private Map<String, Map<Integer, JsonObject>> cachesByKey;

    //***** CONSTRUCTORS *****

    public LivePatientDataCache() {}

    //***** METHODS *****

    public boolean hasCache(Date endDate, Location location) {
        return cachesByKey != null && cachesByKey.containsKey(getCacheKey(endDate, location));
    }

    public Map<Integer, JsonObject> getDataCache(Date endDate, Location location) {
        return getDataCache(getCacheKey(endDate, location));
    }

    public void updateCache(Map<Integer, JsonObject> dataToCache, Date endDate, Location location) {
        getDataCache(endDate, location).putAll(dataToCache);
    }

    public void updateCache(Integer patientId, JsonObject patientData, Date endDate, Location location) {
        getDataCache(endDate, location).put(patientId, patientData);
    }

    public void clearCache(Date endDate, Location location) {
        getDataCache(endDate, location).clear();
    }

    public void clearAllCaches() {
        cachesByKey = new HashMap<String, Map<Integer, JsonObject>>();
        lastAccessTimes = new HashMap<String, Long>();
    }

    public void clearCaches(int minutesSinceLastAccess) {
        long now = System.currentTimeMillis();
        long lastAccessTimeBoundary = now - minutesSinceLastAccess*60*1000;
        for (Iterator<String> cacheKeyIter = lastAccessTimes.keySet().iterator(); cacheKeyIter.hasNext();) {
            String cacheKey = cacheKeyIter.next();
            Long lastAccessTime = lastAccessTimes.get(cacheKey);
            if (lastAccessTime < lastAccessTimeBoundary) {
                getDataCache(cacheKey).clear();
                cacheKeyIter.remove();
            }
        }
    }

    //***** PRIVATE METHODS *****

    private Map<Integer, JsonObject> getDataCache(String cacheKey) {
        lastAccessTimes.put(cacheKey, System.currentTimeMillis());
        if (cachesByKey == null) {
            cachesByKey = new HashMap<String, Map<Integer, JsonObject>>();
        }
        Map<Integer, JsonObject> dataCache = cachesByKey.get(cacheKey);
        if (dataCache == null) {
            dataCache = new HashMap<Integer, JsonObject>();
            cachesByKey.put(cacheKey, dataCache);
        }
        return dataCache;
    }

    private String getCacheKey(Date endDate, Location location) {
        StringBuilder sb = new StringBuilder();
        if (location != null) {
            sb.append(location.getUuid());
        }
//        sb.append("|");
//        sb.append(DateUtil.formatDate(endDate, "yyyy-MM-dd"));
        return sb.toString();
    }
}
