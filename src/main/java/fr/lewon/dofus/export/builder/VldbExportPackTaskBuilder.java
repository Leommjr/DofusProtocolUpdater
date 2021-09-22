package fr.lewon.dofus.export.builder;

import com.jpexs.decompiler.flash.EventListener;
import com.jpexs.decompiler.flash.abc.ScriptPack;
import com.jpexs.decompiler.flash.exporters.settings.ScriptExportSettings;
import fr.lewon.dofus.export.tasks.VldbExportPackTask;
import fr.lewon.dofus.export.manager.IdByNameManager;

public class VldbExportPackTaskBuilder {

    private final String fileName;
    private final IdByNameManager manager;
    private final String arrayName;
    private final String eventType;

    public VldbExportPackTaskBuilder(String fileName, IdByNameManager manager, String arrayName, String eventType) {
        this.fileName = fileName;
        this.manager = manager;
        this.arrayName = arrayName;
        this.eventType = eventType;
    }

    public VldbExportPackTask build(ScriptPack pack, ScriptExportSettings exportSettings, EventListener evl) {
        return new VldbExportPackTask(pack, exportSettings, evl, fileName, manager, arrayName, eventType);
    }

    public String getFileName() {
        return fileName;
    }

    public IdByNameManager getManager() {
        return manager;
    }

    public String getArrayName() {
        return arrayName;
    }

    public String getEventType() {
        return eventType;
    }
}
