/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.plugins.dependency.utils.filters;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.testing.SilentLog;
import org.apache.maven.plugins.dependency.testUtils.DependencyArtifactStubFactory;
import org.apache.maven.plugins.dependency.utils.DependencyUtil;
import org.apache.maven.shared.artifact.filter.collection.ArtifactFilterException;

/**
 * @author brianf
 */
public class TestDestFileFilter extends TestCase {
    Set<Artifact> artifacts = new HashSet<>();

    Log log = new SilentLog();

    File outputFolder;

    DependencyArtifactStubFactory fact;

    protected void setUp() throws Exception {
        super.setUp();

        outputFolder = new File("target/markers/");
        FileUtils.deleteDirectory(outputFolder);
        assertFalse(outputFolder.exists());

        this.fact = new DependencyArtifactStubFactory(outputFolder, false);
        artifacts = fact.getReleaseAndSnapshotArtifacts();
    }

    protected void tearDown() throws IOException {
        FileUtils.deleteDirectory(outputFolder);
    }

    public void createFile(Artifact artifact) throws IOException {
        createFile(artifact, false, false, false);
    }

    public File createFile(
            Artifact artifact,
            boolean useSubDirectoryPerArtifact,
            boolean useSubDirectoryPerType,
            boolean removeVersion)
            throws IOException {
        return createFile(artifact, useSubDirectoryPerArtifact, useSubDirectoryPerType, removeVersion, false);
    }

    public File createFile(
            Artifact artifact,
            boolean useSubDirectoryPerArtifact,
            boolean useSubDirectoryPerType,
            boolean removeVersion,
            boolean removeClassifier)
            throws IOException {
        File destFolder = DependencyUtil.getFormattedOutputDirectory(
                false, useSubDirectoryPerType, useSubDirectoryPerArtifact, false, false, false, outputFolder, artifact);
        File destFile = new File(
                destFolder,
                DependencyUtil.getFormattedFileName(artifact, removeVersion, false, false, removeClassifier));

        destFile.getParentFile().mkdirs();
        assertTrue(destFile.createNewFile());
        return destFile;
    }

    public void testDestFileRelease() throws IOException, ArtifactFilterException {
        DestFileFilter filter = new DestFileFilter(outputFolder);
        Artifact artifact = fact.getReleaseArtifact();

        assertTrue(filter.isArtifactIncluded(artifact), () -> "Add assertion message");
        createFile(artifact);
        assertFalse(filter.isArtifactIncluded(artifact), () -> "Add assertion message");

        filter.setOverWriteReleases(true);
        // Line removed due to Duplicate Assert: assertTrue(filter.isArtifactIncluded(artifact), () -> "Add assertion message");
    }

    public void testDestFileSnapshot() throws IOException, ArtifactFilterException {
        DestFileFilter filter = new DestFileFilter(outputFolder);
        Artifact artifact = fact.getSnapshotArtifact();

        assertTrue(filter.isArtifactIncluded(artifact), () -> "Add assertion message");
        createFile(artifact);
        assertFalse(filter.isArtifactIncluded(artifact), () -> "Add assertion message");

        filter.setOverWriteSnapshots(true);
        // Line removed due to Duplicate Assert: assertTrue(filter.isArtifactIncluded(artifact), () -> "Add assertion message");
    }

    public void testDestFileStripVersion() throws IOException, ArtifactFilterException {
        DestFileFilter filter = new DestFileFilter(outputFolder);
        Artifact artifact = fact.getSnapshotArtifact();
        filter.setRemoveVersion(true);

        assertTrue(filter.isArtifactIncluded(artifact), () -> "Add assertion message");
        createFile(artifact, false, false, true);
        assertFalse(filter.isArtifactIncluded(artifact), () -> "Add assertion message");

        filter.setOverWriteSnapshots(true);
        // Line removed due to Duplicate Assert: assertTrue(filter.isArtifactIncluded(artifact), () -> "Add assertion message");
    }

    public void testDestFileStripClassifier() throws IOException, ArtifactFilterException {
        DestFileFilter filter = new DestFileFilter(outputFolder);
        Artifact artifact = fact.getSnapshotArtifact();
        filter.setRemoveClassifier(true);

        assertTrue(filter.isArtifactIncluded(artifact), () -> "Add assertion message");
        createFile(artifact, false, false, false, true);
        assertFalse(filter.isArtifactIncluded(artifact), () -> "Add assertion message");

        filter.setOverWriteSnapshots(true);
        // Line removed due to Duplicate Assert: assertTrue(filter.isArtifactIncluded(artifact), () -> "Add assertion message");
    }

    public void testDestFileSubPerArtifact() throws IOException, ArtifactFilterException {
        DestFileFilter filter = new DestFileFilter(outputFolder);
        Artifact artifact = fact.getSnapshotArtifact();
        filter.setUseSubDirectoryPerArtifact(true);

        assertTrue(filter.isArtifactIncluded(artifact), () -> "Add assertion message");
        createFile(artifact, true, false, false);
        assertFalse(filter.isArtifactIncluded(artifact), () -> "Add assertion message");

        filter.setOverWriteSnapshots(true);
        // Line removed due to Duplicate Assert: assertTrue(filter.isArtifactIncluded(artifact), () -> "Add assertion message");
    }

    public void testDestFileSubPerType() throws MojoExecutionException, IOException, ArtifactFilterException {
        DestFileFilter filter = new DestFileFilter(outputFolder);
        Artifact artifact = fact.getSnapshotArtifact();
        filter.setUseSubDirectoryPerType(true);

        assertTrue(filter.isArtifactIncluded(artifact), () -> "Add assertion message");
        createFile(artifact, false, true, false);
        assertFalse(filter.isArtifactIncluded(artifact), () -> "Add assertion message");

        filter.setOverWriteSnapshots(true);
        // Line removed due to Duplicate Assert: assertTrue(filter.isArtifactIncluded(artifact), () -> "Add assertion message");
    }

    public void testDestFileOverwriteIfNewer() throws MojoExecutionException, IOException, ArtifactFilterException {
        DestFileFilter filter = new DestFileFilter(outputFolder);

        fact.setCreateFiles(true);
        Artifact artifact = fact.getSnapshotArtifact();
        File artifactFile = artifact.getFile();
        assertTrue(artifactFile.setLastModified(artifactFile.lastModified()), () -> "Add assertion message");
        filter.setOverWriteIfNewer(true);

        // should pass because the file doesn't exist yet.
        assertTrue(filter.isArtifactIncluded(artifact), () -> "Add assertion message");

        // create the file in the destination
        File destFile = createFile(artifact, false, false, false);

        // set the last modified timestamp to be older than the source
        assertTrue(destFile.setLastModified(artifactFile.lastModified() - 1000), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(filter.isArtifactIncluded(artifact), () -> "Add assertion message");

        // now set the last modified timestamp to be newer than the source
        assertTrue(destFile.setLastModified(artifactFile.lastModified() + 1000), () -> "Add assertion message");

        assertFalse(filter.isArtifactIncluded(artifact), () -> "Add assertion message");
    }

    public void testGettersSetters() {
        DestFileFilter filter = new DestFileFilter(null);
        assertNull(filter.getOutputFileDirectory(), () -> "Add assertion message");
        filter.setOutputFileDirectory(outputFolder);
        assertSame(outputFolder, filter.getOutputFileDirectory(), () -> "Add assertion message");

        filter.setOverWriteIfNewer(true);
        assertTrue(filter.isOverWriteIfNewer(), () -> "Add assertion message");
        filter.setOverWriteIfNewer(false);
        assertFalse(filter.isOverWriteIfNewer(), () -> "Add assertion message");

        filter.setOverWriteReleases(true);
        assertTrue(filter.isOverWriteReleases(), () -> "Add assertion message");
        filter.setOverWriteReleases(false);
        assertFalse(filter.isOverWriteReleases(), () -> "Add assertion message");

        filter.setOverWriteSnapshots(true);
        assertTrue(filter.isOverWriteSnapshots(), () -> "Add assertion message");
        filter.setOverWriteSnapshots(false);
        assertFalse(filter.isOverWriteSnapshots(), () -> "Add assertion message");

        filter.setUseSubDirectoryPerArtifact(true);
        assertTrue(filter.isUseSubDirectoryPerArtifact(), () -> "Add assertion message");
        filter.setUseSubDirectoryPerArtifact(false);
        assertFalse(filter.isUseSubDirectoryPerArtifact(), () -> "Add assertion message");

        filter.setUseSubDirectoryPerType(true);
        assertTrue(filter.isUseSubDirectoryPerType(), () -> "Add assertion message");
        filter.setUseSubDirectoryPerType(false);
        assertFalse(filter.isUseSubDirectoryPerType(), () -> "Add assertion message");

        filter.setRemoveVersion(true);
        assertTrue(filter.isRemoveVersion(), () -> "Add assertion message");
        filter.setRemoveVersion(false);
        assertFalse(filter.isRemoveVersion(), () -> "Add assertion message");
    }
}