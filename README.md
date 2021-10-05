# DofusProtocolUpdater

Dofus protocol updater is a [jpexs-decompiler](https://github.com/jindrapetrik/jpexs-decompiler) fork. Its use is to
decompile Dofus and get variable information. For example, the network messages IDs sometimes changing on tuesday
updates.

### How to use

To use this updater,
call `VldbProtocolUpdater.updateManagers(File swfFile, List<VldbExportPackTaskBuilder> taskBuilders)`.

swfFile should be the DofusInvoker.swf file object. <br>
taskBuilders define the files to export, and how (at the moment, the only exportable files are "array builders", let me
explain).

### VldbExportPackTaskBuilder

As I said, the only exportable files are array builders (feel free to implement any other export type yourself if
needed !). What I call an array builder is a class with the sole purpose of managing an array, for example :

![MessageReceiver](demo/message_receiver.png)

`MessageReceiver` holds an array called `_messagesTypes`. They represent the network messages by their id. You could
then export this with a `VldbExportPackTaskBuilder(String fileName, IdByNameManager manager, String arrayName)` with
parameters :
<br>- fileName = MessageReceiver
<br>- manager = an instance of IdByNameManager which is a sort of bidirectional Map
<br>- arrayName = _messagesTypes

When calling `VldbProtocolUpdater.updateManagers` with this `VldbExportPackTaskBuilder`, the manager will first be
cleared then filled with the data found in the MessageReceiver class. cleared and will hold 