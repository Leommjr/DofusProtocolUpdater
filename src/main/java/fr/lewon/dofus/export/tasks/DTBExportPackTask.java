package fr.lewon.dofus.export.tasks;

import com.jpexs.decompiler.flash.EventListener;
import com.jpexs.decompiler.flash.RunnableIOExResult;
import com.jpexs.decompiler.flash.abc.ClassPath;
import com.jpexs.decompiler.flash.abc.ScriptPack;
import com.jpexs.decompiler.flash.exporters.settings.ScriptExportSettings;
import com.jpexs.helpers.Helper;
import fr.lewon.dofus.managers.MessageIdByNameManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DTBExportPackTask implements Callable<File> {

    protected ScriptPack pack;
    protected String fileName;
    protected ScriptExportSettings exportSettings;
    protected ClassPath path;
    protected boolean parallel;
    protected long startTime;
    protected long stopTime;
    protected EventListener eventListener;

    public DTBExportPackTask(ClassPath path, ScriptPack pack, String fileName, ScriptExportSettings exportSettings, boolean parallel, EventListener evl) {
        this.pack = pack;
        this.fileName = fileName;
        this.exportSettings = exportSettings;
        this.path = path;
        this.parallel = parallel;
        this.eventListener = evl;
    }

    @Override
    public File call() throws IOException, InterruptedException {
        RunnableIOExResult<String> rio = new RunnableIOExResult<>() {
            @Override
            public void run() throws IOException, InterruptedException {
                DTBExportPackTask thisObj = DTBExportPackTask.this;
                thisObj.startTime = System.currentTimeMillis();
                this.result = thisObj.pack.export(thisObj.exportSettings, thisObj.parallel);
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
            String arrayName = this.getArrayName();
            String eventType = this.getEventType();
            MessageIdByNameManager manager = this.getManager();
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

            int cpt = 0;
            int count = messageNamesById.size();
            for (Map.Entry<Integer, String> e : messageNamesById.entrySet()) {
                int messageId = e.getKey();
                String messageName = e.getValue();
                manager.addMessage(messageName, messageId);
                this.eventListener.handleExportedEvent(eventType, ++cpt, count, messageName + " : " + messageId + ", " + Helper.formatTimeSec(time));
            }
            manager.save();
        }
    }

    protected abstract MessageIdByNameManager getManager();

    protected abstract String getArrayName();

    protected abstract String getEventType();
}
