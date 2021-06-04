/*
 *  Copyright (C) 2010-2021 JPEXS, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.jpexs.decompiler.flash.exporters.script;

import com.jpexs.decompiler.flash.EventListener;
import com.jpexs.decompiler.flash.SWF;
import com.jpexs.decompiler.flash.abc.ScriptPack;
import com.jpexs.decompiler.flash.configuration.Configuration;
import com.jpexs.decompiler.flash.exporters.settings.ScriptExportSettings;
import com.jpexs.helpers.Helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author JPEXS
 */
public class AS3ScriptExporter {

    private static final Logger logger = Logger.getLogger(AS3ScriptExporter.class.getName());

    public List<File> exportActionScript3(SWF swf, List<ScriptPack> as3scripts, ScriptExportSettings exportSettings, boolean parallel, EventListener evl) {
        final List<File> ret = new ArrayList<>();
        List<ScriptPack> packs = as3scripts != null ? as3scripts : swf.getAS3Packs();
        packs = packs.stream().filter(s -> s.getPath().endsWith("Message")).collect(Collectors.toList());

        List<String> ignoredClasses = new ArrayList<>();
        List<String> ignoredNss = new ArrayList<>();

        String flexClass = swf.getFlexMainClass(ignoredClasses, ignoredNss);

        int cnt = 1;
        List<ExportPackTask> tasks = new ArrayList<>();
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
            tasks.add(new ExportPackTask(cnt++, packs.size(), item.getClassPath(), item, item.getName(), exportSettings, parallel, evl));
        }

        ExecutorService executor = Executors.newFixedThreadPool(Configuration.getParallelThreadCount());
        List<Future<File>> futureResults = new ArrayList<>();
        for (ExportPackTask task : tasks) {
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
