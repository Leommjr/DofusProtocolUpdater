package fr.lewon.dofus.export;

public enum ExportDofusClass {

    MESSAGE_RECEIVER("MessageReceiver"),
    PROTOCOL_TYPE_MANAGER("ProtocolTypeManager");

    private final String fileName;

    ExportDofusClass(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }
}
