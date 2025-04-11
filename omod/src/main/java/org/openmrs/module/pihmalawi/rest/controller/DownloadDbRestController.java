package org.openmrs.module.pihmalawi.rest.controller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.PublicKeyProvider;
import org.openmrs.module.pihmalawi.RestUtils;
import org.openmrs.module.sync.api.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
public class DownloadDbRestController {
    protected Log log = LogFactory.getLog(getClass());

    private static String MANAGE_SYNC_PRIVILEGE = "Manage Synchronization";

    @Autowired
    private PublicKeyProvider publicKeyProvider;
    @GetMapping(value = "/rest/v1/pihmalawi/downloadDb")
    public ResponseEntity<byte[]> getDbBackup(@RequestHeader HttpHeaders headers) throws IOException {
        List<String> signature = headers.get("X-Signature");
        List<String> signatureInput = headers.get("Signature-Input");
        try {
            if (signatureInput != null && signature != null && publicKeyProvider.verifySignature(signatureInput.get(0), signature.get(0))) {
                if (Context.hasPrivilege(MANAGE_SYNC_PRIVILEGE)) {
                    try {
                        File outputFile = Context.getService(SyncService.class).generateDataFile();
                        InputStream in = new FileInputStream(outputFile);
                        MediaType mediaType = MediaType.parseMediaType("application/octet-stream");
                        return ResponseEntity.ok().contentType(mediaType).body(IOUtils.toByteArray(in));
                    } catch (Exception e) {
                        log.warn("Error while generating backup file", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(HttpStatus.UNAUTHORIZED.getReasonPhrase().getBytes());
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(HttpStatus.UNAUTHORIZED.getReasonPhrase().getBytes());
            }
        } catch (Exception e ) {
            log.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
        }
    }
}
