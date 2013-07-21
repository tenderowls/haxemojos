/**
 * Copyright (C) 2012 https://github.com/tenderowls/haxemojos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tenderowls.opensource.haxemojos.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

public class NativeProgramVersionTest {

    @Test
    public void versionTest0() {
        try {
            NativeProgramVersion version = new NativeProgramVersion("2.10");

            Assert.assertEquals(version.getMajor(), 2, "major");
            Assert.assertEquals(version.getMinor(), 10, "minor");
            Assert.assertEquals(version.getRevision(), 0, "revision");
            Assert.assertFalse(version.isReleaseCandidate(), "isReleaseCandidate");
            Assert.assertFalse(version.isSnapshot(), "isSnapshot");

        } catch (NativeProgramVersion.NativeProgramVersionException e) {
            Assert.fail();
        }
    }

    @Test
    public void versionTest1() {
        try {
            NativeProgramVersion version = new NativeProgramVersion("3.0");

            Assert.assertEquals(version.getMajor(), 3, "major");
            Assert.assertEquals(version.getMinor(), 0, "minor");
            Assert.assertEquals(version.getRevision(), 0, "revision");
            Assert.assertFalse(version.isReleaseCandidate(), "isReleaseCandidate");
            Assert.assertFalse(version.isSnapshot(), "isSnapshot");

        } catch (NativeProgramVersion.NativeProgramVersionException e) {
            Assert.fail();
        }
    }

    @Test
    public void versionTest2() {
        try {
            NativeProgramVersion version = new NativeProgramVersion("3.0.1");

            Assert.assertEquals(version.getMajor(), 3, "major");
            Assert.assertEquals(version.getMinor(), 0, "minor");
            Assert.assertEquals(version.getRevision(), 1, "revision");
            Assert.assertFalse(version.isReleaseCandidate(), "isReleaseCandidate");
            Assert.assertFalse(version.isSnapshot(), "isSnapshot");

        } catch (NativeProgramVersion.NativeProgramVersionException e) {
            Assert.fail();
        }
    }

    @Test
    public void versionTest3() {
        try {
            NativeProgramVersion version = new NativeProgramVersion("3.0.1-SNAPSHOT");

            Assert.assertEquals(version.getMajor(), 3, "major");
            Assert.assertEquals(version.getMinor(), 0, "minor");
            Assert.assertEquals(version.getRevision(), 1, "revision");
            Assert.assertFalse(version.isReleaseCandidate(), "isReleaseCandidate");
            Assert.assertTrue(version.isSnapshot(), "isSnapshot");

        } catch (NativeProgramVersion.NativeProgramVersionException e) {
            Assert.fail();
        }
    }

    @Test
    public void versionTest4() {
        try {
            NativeProgramVersion version = new NativeProgramVersion("3.0.1-rc1");

            Assert.assertTrue(version.isReleaseCandidate(), "isReleaseCandidate");
            Assert.assertEquals(version.getMajor(), 3, "major");
            Assert.assertEquals(version.getMinor(), 0, "minor");
            Assert.assertEquals(version.getRevision(), 1, "revision");
            Assert.assertEquals(version.getReleaseCandidate(), 1, "getReleaseCandidate");
            Assert.assertFalse(version.isSnapshot(), "isSnapshot");

        } catch (NativeProgramVersion.NativeProgramVersionException e) {
            Assert.fail();
        }
    }

    @Test
    public void formatTest0() {
        try {
            NativeProgramVersion version = new NativeProgramVersion("3-rc1");
            Assert.fail();
        } catch (NativeProgramVersion.NativeProgramVersionException e) {
        }
    }

    @Test
    public void formatTest1() {
        try {
            NativeProgramVersion version = new NativeProgramVersion("3.0.0-alpha1");
            Assert.fail();
        } catch (NativeProgramVersion.NativeProgramVersionException e) {
        }
    }

    @Test
    public void formatTest2() {
        try {
            NativeProgramVersion version = new NativeProgramVersion("3.0.0.0-SNAPSHOT");
            Assert.fail();
        } catch (NativeProgramVersion.NativeProgramVersionException e) {
        }
    }

    @Test
    public void compareTest0() {
        compareTestTemplate("3.0.0", "3.0.0", 0);
        compareTestTemplate("3.0.0", "3.0", 0);
        compareTestTemplate("2.10", "2.10", 0);
        compareTestTemplate("3.0.1-SNAPSHOT", "3.0.1-SNAPSHOT", 0);
        compareTestTemplate("3.0.1-rc1", "3.0.1-rc1", 0);
    }

    @Test
    public void compareTest1() {
        compareTestTemplate("3.0.0", "2.9.9", 1);
        compareTestTemplate("3.1.0", "3.0.1", 1);
        compareTestTemplate("3.0.2", "3.0.1", 1);
        compareTestTemplate("3.0.1", "3.0.1-SNAPSHOT", 1);
        compareTestTemplate("3.0.1", "3.0.1-rc1", 1);
        compareTestTemplate("3.0.1-rc2", "3.0.1-rc1", 1);
        compareTestTemplate("3.0.1-rc2", "3.0.1-SNAPSHOT", 1);
    }

    @Test
    public void compareTest2() {
        compareTestTemplate("2.0.0", "3.0.0", -1);
        compareTestTemplate("3.0.0", "3.1.0", -1);
        compareTestTemplate("3.0.0", "3.0.1", -1);
        compareTestTemplate("3.0.0-SNAPSHOT", "3.0.0", -1);
        compareTestTemplate("3.0.0-rc1", "3.0.0", -1);
        compareTestTemplate("3.0.0-SNAPSHOT", "3.0.0-rc1", -1);
        compareTestTemplate("3.0.0-rc1", "3.0.0-rc2", -1);
    }

    private void compareTestTemplate(String a, String b, int expected) {
        try {
            // compare
            NativeProgramVersion version1 = new NativeProgramVersion(a);
            NativeProgramVersion version2 = new NativeProgramVersion(b);
            Assert.assertEquals(version1.compare(version2), expected);
        } catch (NativeProgramVersion.NativeProgramVersionException e) {
            Assert.fail();
        }
    }

}
