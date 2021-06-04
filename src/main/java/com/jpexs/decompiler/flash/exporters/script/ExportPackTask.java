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
import com.jpexs.decompiler.flash.RunnableIOExResult;
import com.jpexs.decompiler.flash.abc.ClassPath;
import com.jpexs.decompiler.flash.abc.ScriptPack;
import com.jpexs.decompiler.flash.exporters.settings.ScriptExportSettings;
import com.jpexs.helpers.Helper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author JPEXS
 */
public class ExportPackTask implements Callable<File> {

    ScriptPack pack;

    String fileName;

    ScriptExportSettings exportSettings;

    ClassPath path;

    int index;

    int count;

    boolean parallel;

    long startTime;

    long stopTime;

    EventListener eventListener;

    public ExportPackTask(int index, int count, ClassPath path, ScriptPack pack, String fileName, ScriptExportSettings exportSettings, boolean parallel, EventListener evl) {
        this.pack = pack;
        this.fileName = fileName;
        this.exportSettings = exportSettings;
        this.path = path;
        this.index = index;
        this.count = count;
        this.parallel = parallel;
        this.eventListener = evl;
    }

    @Override
    public File call() throws IOException, InterruptedException {
        RunnableIOExResult<String> rio = new RunnableIOExResult<>() {
            @Override
            public void run() throws IOException, InterruptedException {
                ExportPackTask.this.startTime = System.currentTimeMillis();
                this.result = ExportPackTask.this.pack.export(ExportPackTask.this.exportSettings, ExportPackTask.this.parallel);
                ExportPackTask.this.stopTime = System.currentTimeMillis();
            }
        };

        rio.run();

        if (this.eventListener != null) {
            long time = this.stopTime - this.startTime;
            Pattern p = Pattern.compile("const protocolId:uint = ([0-9]+);");
            Arrays.stream(rio.result.split("\n"))
                    .map(p::matcher)
                    .filter(Matcher::find)
                    .findFirst()
                    .ifPresent(m -> {
                        int protocolId = Integer.parseInt(m.group(1));
                        int header = protocolId >> 6;
                        String hexHeader = "0x" + Integer.toHexString(header);
                        DTBProtocolManager.addProtocol(this.fileName, hexHeader);
                        this.eventListener.handleExportedEvent("script", this.index, this.count, this.fileName + " : " + hexHeader + ", " + Helper.formatTimeSec(time));
                    });
        }

        return null;
    }
}
