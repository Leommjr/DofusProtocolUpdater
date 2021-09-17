package fr.lewon.dofus.export.tasks;

import com.jpexs.decompiler.flash.EventListener;
import com.jpexs.decompiler.flash.abc.ClassPath;
import com.jpexs.decompiler.flash.abc.ScriptPack;
import com.jpexs.decompiler.flash.exporters.settings.ScriptExportSettings;
import fr.lewon.dofus.managers.DTBProtocolTypeManager;
import fr.lewon.dofus.managers.MessageIdByNameManager;

public class DofusProtocolTypeExportPackTask extends DTBExportPackTask {

    public DofusProtocolTypeExportPackTask(ClassPath path, ScriptPack pack, String fileName, ScriptExportSettings exportSettings, boolean parallel, EventListener evl) {
        super(path, pack, fileName, exportSettings, parallel, evl);
    }

    @Override
    protected MessageIdByNameManager getManager() {
        return new DTBProtocolTypeManager();
    }

    @Override
    protected String getArrayName() {
        return "_typesTypes";
    }

    @Override
    protected String getEventType() {
        return "SCRIPT";
    }
}
