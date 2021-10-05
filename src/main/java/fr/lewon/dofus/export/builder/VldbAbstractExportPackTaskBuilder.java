package fr.lewon.dofus.export.builder;

import com.jpexs.decompiler.flash.EventListener;
import com.jpexs.decompiler.flash.abc.ScriptPack;
import com.jpexs.decompiler.flash.exporters.settings.ScriptExportSettings;
import fr.lewon.dofus.export.tasks.VldbExportPackTask;

public abstract class VldbAbstractExportPackTaskBuilder {

    private final String fileName;

    public VldbAbstractExportPackTaskBuilder(String fileName) {
        this.fileName = fileName;
    }

    public VldbExportPackTask build(ScriptPack pack, ScriptExportSettings exportSettings, EventListener evl) {
        return new VldbExportPackTask(pack, exportSettings, evl, getFileName(), this::treatFileContent);
    }

    public String getFileName() {
        return fileName;
    }

    public abstract void treatFileContent(String fileContent);

}
