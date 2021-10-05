package fr.lewon.dofus.export;

import com.jpexs.decompiler.flash.EventListener;
import com.jpexs.decompiler.flash.SWF;
import com.jpexs.decompiler.flash.abc.ScriptPack;
import com.jpexs.decompiler.flash.configuration.Configuration;
import com.jpexs.decompiler.flash.exporters.settings.ScriptExportSettings;
import com.jpexs.helpers.Helper;
import fr.lewon.dofus.export.builder.VldbAbstractExportPackTaskBuilder;
import fr.lewon.dofus.export.tasks.VldbExportPackTask;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class VldbAS3ScriptExporter {

    private static final Logger logger = Logger.getLogger(VldbAS3ScriptExporter.class.getName());

    public List<File> exportDofusScript(SWF swf, List<ScriptPack> as3scripts, ScriptExportSettings exportSettings, EventListener evl, List<VldbAbstractExportPackTaskBuilder> taskBuilders) {
        final List<File> ret = new ArrayList<>();
        List<ScriptPack> packs = as3scripts != null ? as3scripts : swf.getAS3Packs();

        Map<String, VldbAbstractExportPackTaskBuilder> buildersByFileName = new HashMap<>();
        taskBuilders.forEach(b -> buildersByFileName.put(b.getFileName(), b));

        Set<String> toExportNames = buildersByFileName.keySet();
        packs = packs.stream()
                .filter(s -> toExportNames.contains(s.getName()))
                .collect(Collectors.toList());

        List<String> ignoredClasses = new ArrayList<>();
        List<String> ignoredNss = new ArrayList<>();

        String flexClass = swf.getFlexMainClass(ignoredClasses, ignoredNss);

        List<VldbExportPackTask> tasks = new ArrayList<>();
        for (ScriptPack item : packs) {
            if (!item.isSimple && Configuration.ignoreCLikePackages.get()) {
                continue;
            }
            if (ignoredClasses.contains(item.getClassPath().toRawString())) {
                continue;
            }
            if (flexClass != null && item.getClassPath().toRawString().equals(flexClass)) {
                continue;
            }

            VldbAbstractExportPackTaskBuilder taskBuilder = buildersByFileName.get(item.getName());
            if (taskBuilder == null) {
                continue;
            }

            tasks.add(taskBuilder.build(item, exportSettings, evl));
        }

        ExecutorService executor = Executors.newFixedThreadPool(Configuration.getParallelThreadCount());
        List<Future<File>> futureResults = new ArrayList<>();
        for (VldbExportPackTask task : tasks) {
            Future<File> future = executor.submit(task);
            futureResults.add(future);
        }

        try {
            executor.shutdown();
            if (!executor.awaitTermination(Configuration.exportTimeout.get(), TimeUnit.SECONDS)) {
                logger.log(Level.SEVERE, "{0} ActionScript export limit reached", Helper.formatTimeToText(Configuration.exportTimeout.get()));
            }
        } catch (InterruptedException ex) {
        } finally {
            executor.shutdownNow();
        }

        for (int f = 0; f < futureResults.size(); f++) {
            try {
                if (futureResults.get(f).isDone()) {
                    ret.add(futureResults.get(f).get());
                }
            } catch (InterruptedException ex) {
            } catch (ExecutionException ex) {
                logger.log(Level.SEVERE, "Error during ABC export", ex);
            }
        }

        return ret;
    }
}
