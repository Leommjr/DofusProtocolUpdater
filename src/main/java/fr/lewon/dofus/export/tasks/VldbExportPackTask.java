package fr.lewon.dofus.export.tasks;

import com.jpexs.decompiler.flash.EventListener;
import com.jpexs.decompiler.flash.RunnableIOExResult;
import com.jpexs.decompiler.flash.abc.ScriptPack;
import com.jpexs.decompiler.flash.exporters.settings.ScriptExportSettings;
import com.jpexs.helpers.Helper;
import fr.lewon.dofus.export.manager.IdByNameManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VldbExportPackTask implements Callable<File> {

    protected ScriptPack pack;
    protected ScriptExportSettings exportSettings;
    protected EventListener eventListener;
    protected String fileName;
    protected IdByNameManager manager;
    protected String arrayName;

    protected long startTime;
    protected long stopTime;

    public VldbExportPackTask(ScriptPack pack, ScriptExportSettings exportSettings, EventListener evl, String fileName, IdByNameManager manager, String arrayName) {
        this.pack = pack;
        this.exportSettings = exportSettings;
        this.eventListener = evl;
        this.fileName = fileName;
        this.manager = manager;
        this.arrayName = arrayName;
    }

    @Override
    public File call() throws IOException, InterruptedException {
        RunnableIOExResult<String> rio = new RunnableIOExResult<>() {
            @Override
            public void run() throws IOException, InterruptedException {
                VldbExportPackTask thisObj = VldbExportPackTask.this;
                thisObj.startTime = System.currentTimeMillis();
                this.result = thisObj.pack.export(thisObj.exportSettings, true);
                thisObj.stopTime = System.currentTimeMillis();
            }
        };

        rio.run();

        this.handleExport(rio);

        return null;
    }

    private void handleExport(RunnableIOExResult<String> rio) {
        if (this.eventListener != null) {
            long time = this.stopTime - this.startTime;
            Pattern p = Pattern.compile(arrayName + "\\[([0-9]+)] = (.*?);");
            Map<Integer, String> messageNamesById = new HashMap<>();
            Arrays.stream(rio.result.split("\n"))
                    .map(p::matcher)
                    .filter(Matcher::find)
                    .forEach(m -> {
                        int messageId = Integer.parseInt(m.group(1));
                        String messageName = m.group(2);
                        messageNamesById.put(messageId, messageName);
                    });

            manager.clearAll();
            messageNamesById.forEach((id, name) -> manager.addPair(name, id));
            this.eventListener.handleExportedEvent(fileName, messageNamesById.size() + ", " + Helper.formatTimeSec(time));
        }
    }
}
