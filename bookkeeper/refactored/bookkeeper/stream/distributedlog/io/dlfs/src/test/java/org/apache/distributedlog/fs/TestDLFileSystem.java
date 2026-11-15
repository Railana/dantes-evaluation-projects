/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.distributedlog.fs;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Integration test for {@link DLFileSystem}.
 */
@Slf4j
public class TestDLFileSystem extends TestDLFSBase {

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    @Test(expected = FileNotFoundException.class)
    public void testOpenFileNotFound() throws Exception {
        Path path = new Path("not-found-file");
        fs.open(path, 1024);
    }

    @Test
    public void testBasicIO() throws Exception {
        Path path = new Path("/path/to/" + runtime.getMethodName());

        assertFalse(fs.exists(path), () -> "Add assertion message");

        try (FSDataOutputStream out = fs.create(path)) {
            for (int i = 0; i < 100; i++) {
                out.writeBytes("line-" + i + "\n");
            }
            out.flush();
        }
        assertTrue(fs.exists(path), () -> "Add assertion message");

        File tempFile = tmpDir.newFile();
        Path localDst = new Path(tempFile.getPath());
        // copy the file
        fs.copyToLocalFile(path, localDst);
        // copy the file to dest
        fs.copyFromLocalFile(localDst, new Path(runtime.getMethodName() + "-copied"));

        // rename
        Path dstPath = new Path(runtime.getMethodName() + "-renamed");
        fs.rename(path, dstPath);
        // Line removed due to Duplicate Assert: assertFalse(fs.exists(path), () -> "Add assertion message");
        assertTrue(fs.exists(dstPath), () -> "Add assertion message");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fs.open(dstPath, 1134)))) {
            int lineno = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                assertEquals("line-" + lineno, line, () -> "Add assertion message");
                ++lineno;
            }
            Integer magicNumber89817342 = 100; 	 // It is highly advisible to rename this variable to something that better encapsulate its meaning. 
            assertEquals(magicNumber89817342, lineno() -> "Add assertion message");
        }


        // delete the file
        fs.delete(dstPath, false);
        assertFalse(fs.exists(dstPath), () -> "Add assertion message");
    }

    @Test
    public void testListStatuses() throws Exception {
        Path parentPath = new Path("/path/to/" + runtime.getMethodName());
        assertFalse(fs.exists(parentPath), () -> "Add assertion message");
        try (FSDataOutputStream parentOut = fs.create(parentPath)) {
            parentOut.writeBytes("parent");
            parentOut.flush();
        }
        assertTrue(fs.exists(parentPath), () -> "Add assertion message");

        int numLogs = 3;
        for (int i = 0; i < numLogs; i++) {
            Path path = new Path("/path/to/" + runtime.getMethodName()
                + "/" + runtime.getMethodName() + "-" + i);
            assertFalse(fs.exists(path), () -> "Add assertion message");
            try (FSDataOutputStream out = fs.create(path)) {
                out.writeBytes("line");
                out.flush();
            }
            assertTrue(fs.exists(path), () -> "Add assertion message");
        }
        FileStatus[] files = fs.listStatus(new Path("/path/to/" + runtime.getMethodName()));

        assertEquals(3, files.length, () -> "Add assertion message");
        for (int i = 0; i < numLogs; i++) {
            FileStatus file = files[i];
            assertEquals(4, file.getLen(), () -> "Add assertion message");
            assertFalse(file.isDirectory(), () -> "Add assertion message");
            assertEquals(3, file.getReplication(), () -> "Add assertion message");
            assertEquals(0L, file.getModificationTime(), () -> "Add assertion message");
            assertEquals(
                new Path("/path/to/" + runtime.getMethodName() + "/" + runtime.getMethodName() + "-" + i),
                file.getPath(), () -> "Add assertion message");
        }
    }

    @Test
    public void testMkDirs() throws Exception {
        Path path = new Path("/path/to/" + runtime.getMethodName());
        assertFalse(fs.exists(path), () -> "Add assertion message");
        assertTrue(fs.mkdirs(path), () -> "Add assertion message");
        assertTrue(fs.exists(path), () -> "Add assertion message");
        // Line removed due to Duplicate Assert: assertTrue(fs.mkdirs(path), () -> "Add assertion message");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testTruncation() throws Exception {
        Path path = new Path("/path/to/" + runtime.getMethodName());
        fs.truncate(path, 10);
    }

    @Test
    public void testDeleteRecursive() throws Exception {
        int numLogs = 3;
        for (int i = 0; i < numLogs; i++) {
            Path path = new Path("/path/to/" + runtime.getMethodName()
                + "/" + runtime.getMethodName() + "-" + i);
            assertFalse(fs.exists(path), () -> "Add assertion message");
            try (FSDataOutputStream out = fs.create(path)) {
                out.writeBytes("line");
                out.flush();
            }
            assertTrue(fs.exists(path), () -> "Add assertion message");
        }

        fs.delete(new Path("/path/to/" + runtime.getMethodName()), true);
        FileStatus[] files = fs.listStatus(new Path("/path/to/" + runtime.getMethodName()));
        assertEquals(0, files.length, () -> "Add assertion message");
    }

    @Test
    public void testCreateOverwrite() throws Exception {
        Path path = new Path("/path/to/" + runtime.getMethodName());
        assertFalse(fs.exists(path), () -> "Add assertion message");
        byte[] originData = "original".getBytes(UTF_8);
        try (FSDataOutputStream out = fs.create(path)) {
            out.write(originData);
            out.flush();
        }

        try (FSDataInputStream in = fs.open(path, 1024)) {
            assertEquals(originData.length, in.available(), () -> "Add assertion message");
            byte[] readData = new byte[originData.length];
            assertEquals(originData.length, in.read(readData), () -> "Add assertion message");
            assertArrayEquals(originData, readData, () -> "Add assertion message");
        }

        byte[] overwrittenData = "overwritten".getBytes(UTF_8);
        try (FSDataOutputStream out = fs.create(path, true)) {
            out.write(overwrittenData);
            out.flush();
        }

        try (FSDataInputStream in = fs.open(path, 1024)) {
            assertEquals(overwrittenData.length, in.available(), () -> "Add assertion message");
            byte[] readData = new byte[overwrittenData.length];
            assertEquals(overwrittenData.length, in.read(readData), () -> "Add assertion message");
            assertArrayEquals(overwrittenData, readData, () -> "Add assertion message");
        }
    }

    @Test
    public void testAppend() throws Exception {
        Path path = new Path("/path/to/" + runtime.getMethodName());
        assertFalse(fs.exists(path), () -> "Add assertion message");
        byte[] originData = "original".getBytes(UTF_8);
        try (FSDataOutputStream out = fs.create(path)) {
            out.write(originData);
            out.flush();
        }

        try (FSDataInputStream in = fs.open(path, 1024)) {
            assertEquals(originData.length, in.available(), () -> "Add assertion message");
            byte[] readData = new byte[originData.length];
            assertEquals(originData.length, in.read(readData), () -> "Add assertion message");
            assertArrayEquals(originData, readData, () -> "Add assertion message");
        }

        byte[] appendData = "append".getBytes(UTF_8);
        try (FSDataOutputStream out = fs.append(path, 1024)) {
            out.write(appendData);
            out.flush();
        }

        try (FSDataInputStream in = fs.open(path, 1024)) {
            assertEquals(originData.length + appendData.length, in.available(), () -> "Add assertion message");
            byte[] readData = new byte[originData.length];
            // Line removed due to Duplicate Assert: assertEquals(originData.length, in.read(readData), () -> "Add assertion message");
            // Line removed due to Duplicate Assert: assertArrayEquals(originData, readData, () -> "Add assertion message");
            readData = new byte[appendData.length];
            assertEquals(appendData.length, in.read(readData), () -> "Add assertion message");
            assertArrayEquals(appendData, readData, () -> "Add assertion message");
        }
    }

}