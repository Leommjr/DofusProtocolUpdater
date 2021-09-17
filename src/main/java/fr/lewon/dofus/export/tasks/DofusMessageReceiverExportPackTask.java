package fr.lewon.dofus.export.tasks;

import com.jpexs.decompiler.flash.EventListener;
import com.jpexs.decompiler.flash.abc.ClassPath;
import com.jpexs.decompiler.flash.abc.ScriptPack;
import com.jpexs.decompiler.flash.exporters.settings.ScriptExportSettings;
import fr.lewon.dofus.managers.DTBMessageReceiverManager;
import fr.lewon.dofus.managers.MessageIdByNameManager;

public class DofusMessageReceiverExportPackTask extends DTBExportPackTask {

    public DofusMessageReceiverExportPackTask(ClassPath path, ScriptPack pack, String fileName, ScriptExportSettings exportSettings, boolean parallel, EventListener evl) {
        super(path, pack, fileName, exportSettings, parallel, evl);
    }

    @Override
    protected MessageIdByNameManager getManager() {
        return new DTBMessageReceiverManager();
    }

    @Override
    protected String getArrayName() {
        return "_messagesTypes";
    }

    @Override
    protected String getEventType() {
        return "MESSAGE";
    }
}
