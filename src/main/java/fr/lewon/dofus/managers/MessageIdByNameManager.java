package fr.lewon.dofus.managers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public abstract class MessageIdByNameManager {

    private final File protocolFile;
    private final Map<String, Integer> messageIdByName = new HashMap<>();

    protected MessageIdByNameManager(String outputFilePath) {
        this.protocolFile = new File(outputFilePath);
    }

    public synchronized void save() {
        try (FileOutputStream fos = new FileOutputStream(this.protocolFile, false);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            osw.write(new ObjectMapper().writeValueAsString(this.messageIdByName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addMessage(String messageName, int messageId) {
        this.messageIdByName.put(messageName, messageId);
        this.save();
    }
}
