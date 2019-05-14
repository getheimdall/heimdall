/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==========================LICENSE_END===================================
 */
package br.com.conductor.heimdall.gateway.util;

import com.netflix.zuul.FilterLoader;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Custom FilterFileManager created to be able to add directories dynamically to be scanned for new scripts.
 *
 * @author Marcelo Aguiar Rodrigues
 * @see com.netflix.zuul.FilterFileManager
 */
@Slf4j
public class HeimdallFilterFileManager {

    private static final Logger LOG = LoggerFactory.getLogger(HeimdallFilterFileManager.class);
    private Set<String> aDirectories;
    private int pollingIntervalSeconds;
    private Thread poller;
    private boolean bRunning = true;
    private static FilenameFilter FILENAME_FILTER;
    private static HeimdallFilterFileManager INSTANCE;

    private HeimdallFilterFileManager() {
    }

    public static void setFilenameFilter(FilenameFilter filter) {
        FILENAME_FILTER = filter;
    }

    /**
     * Initialized the GroovyFileManager.
     *
     * @param pollingIntervalSeconds the polling interval in Seconds
     * @param directories            Any number of paths to directories to be polled may be specified
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void init(int pollingIntervalSeconds, Set<String> directories) throws Exception, IllegalAccessException, InstantiationException {
        if (INSTANCE == null) INSTANCE = new HeimdallFilterFileManager();
        INSTANCE.aDirectories = directories;
        INSTANCE.pollingIntervalSeconds = pollingIntervalSeconds;
        INSTANCE.manageFiles();
        INSTANCE.startPoller();
    }

    public static HeimdallFilterFileManager getInstance() {
        return INSTANCE;
    }

    /**
     * Shuts down the poller
     */
    public static void shutdown() {
        INSTANCE.stopPoller();
    }

    void stopPoller() {
        bRunning = false;
    }

    void startPoller() {
        poller = new Thread("HeimdallGroovyFilterFileManagerPoller") {
            public void run() {
                while (bRunning) {
                    try {
                        sleep(pollingIntervalSeconds * 1000);
                        manageFiles();
                    } catch (Exception e) {
                    	log.error(e.getMessage(), e);
                    }
                }
            }
        };
        poller.setDaemon(true);
        poller.start();
    }

    /**
     * Returns the directory File for a path. A Runtime Exception is thrown if the directory is in valid
     *
     * @param sPath
     * @return a File representing the directory path
     */
    public File getDirectory(String sPath) {
        File directory = new File(sPath);
        if (!directory.isDirectory()) {
            URL resource = HeimdallFilterFileManager.class.getClassLoader().getResource(sPath);
            try {
                directory = new File(resource.toURI());
            } catch (Exception e) {
                LOG.error("Error accessing directory in classloader. path=" + sPath, e);
            }
            if (!directory.isDirectory()) {
                throw new RuntimeException(directory.getAbsolutePath() + " is not a valid directory");
            }
        }
        return directory;
    }

    /**
     * Returns a List<File> of all Files from all polled directories
     *
     * @return
     */
    List<File> getFiles() {
        List<File> list = new ArrayList<>();
        for (String sDirectory : aDirectories) {
            if (sDirectory != null) {
                File directory = getDirectory(sDirectory);
                File[] aFiles = directory.listFiles(FILENAME_FILTER);
                if (aFiles != null) {
                    list.addAll(Arrays.asList(aFiles));
                }
            }
        }
        return list;
    }

    /**
     * puts files into the FilterLoader. The FilterLoader will only addd new or changed filters
     *
     * @param aFiles a List<File>
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    void processGroovyFiles(List<File> aFiles) throws Exception, InstantiationException, IllegalAccessException {
        for (File file : aFiles) {
            FilterLoader.getInstance().putFilter(file);
        }
    }

    void manageFiles() throws Exception, IllegalAccessException, InstantiationException {
        List<File> aFiles = getFiles();
        processGroovyFiles(aFiles);
    }

    /**
     * Addes a new directory to be scanned
     *
     * @param directory path to the directory
     */
    public void addNewDirectory(String directory) {
        aDirectories.add(directory);
    }

    /**
     * Removes a directory from the path
     *
     * @param directory directory to be removed
     */
    public void removeDirectory(String directory) {
        aDirectories.remove(directory);
    }
}

