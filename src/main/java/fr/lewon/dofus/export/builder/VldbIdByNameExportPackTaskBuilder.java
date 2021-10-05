package fr.lewon.dofus.export.builder;

import fr.lewon.dofus.export.manager.IdByNameManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VldbIdByNameExportPackTaskBuilder extends VldbAbstractExportPackTaskBuilder {

    private final IdByNameManager manager;
    private final String arrayName;

    public VldbIdByNameExportPackTaskBuilder(String fileName, IdByNameManager manager, String arrayName) {
        super(fileName);
        this.manager = manager;
        this.arrayName = arrayName;
    }

    @Override
    public void treatFileContent(String fileContent) {
        Pattern p = Pattern.compile(arrayName + "\\[([0-9]+)] = (.*?);");
        Map<Integer, String> messageNamesById = new HashMap<>();
        Arrays.stream(fileContent.split("\n"))
                .map(p::matcher)
                .filter(Matcher::find)
                .forEach(m -> {
                    int messageId = Integer.parseInt(m.group(1));
                    String messageName = m.group(2);
                    messageNamesById.put(messageId, messageName);
                });

        manager.clearAll();
        messageNamesById.forEach((id, name) -> manager.addPair(name, id));
    }
}
