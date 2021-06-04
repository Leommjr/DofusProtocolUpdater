package com.jpexs.decompiler.flash.exporters.script;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DTBProtocolManager {

    private static final File protocolFile = new File("config/protocol");
    private static final Map<String, String> headerByMessageName = new HashMap<>();

    private synchronized static void save() {
        try (FileOutputStream fos = new FileOutputStream(protocolFile, false);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            osw.write(new ObjectMapper().writeValueAsString(headerByMessageName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void addProtocol(String messageName, String hexHeader) {
        headerByMessageName.put(messageName, hexHeader);
        save();
    }

}
