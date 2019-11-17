package com.ex.demo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.TreeTraverser;
import com.google.common.io.Files;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
 
public class FileDiffUtils {
 
    private static final TreeTraverser<File> TRAVERSER = Files.fileTreeTraverser();
 
    public static Map<String, List<String>> calculateIncrements(File refDir, File currentDir) throws IOException {
 
        Assert.isTrue(refDir.isDirectory(), "ref must be a valid directory");
        Assert.isTrue(currentDir.isDirectory(), "current must be a valid directory");
 
        List<String> changes = new ArrayList<String>();
        List<String> deletions = new ArrayList<String>();
        List<String> additions = new ArrayList<String>();
 
        Map<String, String> dMap = new HashMap<String, String>();
        TRAVERSER.breadthFirstTraversal(currentDir).forEach((file) -> {
            String subPath = FileUtil.subPath(currentDir.getAbsolutePath(), file);
			if (StrUtil.isNotBlank(subPath)) {
				dMap.putIfAbsent(subPath, FilenameUtils.normalize(file.getAbsolutePath(), true));
			}
        });
 
        Map<String, String> sMap = new HashMap<String, String>();
        TRAVERSER.breadthFirstTraversal(refDir).forEach((file) -> {
            String subPath = FileUtil.subPath(refDir.getAbsolutePath(), file);
			if (StrUtil.isNotBlank(subPath)) {
				sMap.putIfAbsent(subPath, FilenameUtils.normalize(file.getAbsolutePath(), true));
			}
        });
 
        Iterator<Entry<String, String>> iter = dMap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            String dKey = entry.getKey();
            String dPath = entry.getValue();
 
            if (!sMap.containsKey(dKey)) {
                additions.add(dPath);
            } else {
            	File dFile = new File(dPath);
                File sFile = new File(sMap.get(dKey));
                if (!equalFile(dFile, sFile)) {
                    changes.add(dPath);
                }
            }
            sMap.remove(dKey);
            iter.remove();
        }
 
        deletions = Lists.newArrayList(sMap.values());
        return ImmutableMap.of("changes", changes, "additions", additions, "deletions", deletions);
    }
 
    private static boolean equalFile(File dFile, File sFile) throws IOException {
        if (dFile.isDirectory() && sFile.isDirectory() && StrUtil.equals(dFile.getName(), sFile.getName())) {
            return true;
        }
        if (dFile.isFile() && sFile.isFile() && Files.equal(dFile, sFile)) {
            return true;
        }
        return false;
    }
}