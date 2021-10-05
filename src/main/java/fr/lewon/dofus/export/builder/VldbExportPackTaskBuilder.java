package fr.lewon.dofus.export.builder;

import com.jpexs.decompiler.flash.EventListener;
import com.jpexs.decompiler.flash.abc.ScriptPack;
import com.jpexs.decompiler.flash.exporters.settings.ScriptExportSettings;
import fr.lewon.dofus.export.manager.IdByNameManager;
import fr.lewon.dofus.export.tasks.VldbExportPackTask;

public class VldbExportPackTaskBuilder {

    private final String fileName;
    private final IdByNameManager manager;
    private final String arrayName;

    public VldbExportPackTaskBuilder(String fileName, IdByNameManager manager, String arrayName) {
        this.fileName = fileName;
        this.manager = manager;
        this.arrayName = arrayName;
    }

    public VldbExportPackTask build(ScriptPack pack, ScriptExportSettings exportSettings, EventListener evl) {
        return new VldbExportPackTask(pack, exportSettings, evl, this.fileName, this.manager, this.arrayName);
    }

    public String getFileName() {
        return this.fileName;
    }

    public IdByNameManager getManager() {
        return this.manager;
    }

}
