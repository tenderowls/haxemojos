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

public final class NativeProgramVersion {

    private int major;
    private int minor;
    private int revision;
    private int releaseCandidate;
    private boolean isSnapshot;

    public NativeProgramVersion(String version) throws NativeProgramVersionException {

        if (version.matches("^\\d(\\.\\d+){1,2}(-SNAPSHOT|-rc\\d|-RC\\d)*")) {

            String[] parts = version.split("\\.");
            String lastPart = parts[parts.length - 1];

            releaseCandidate = parseIsReleaseCandidate(lastPart);
            isSnapshot = checkIsSnapshot(lastPart);
            major = Integer.parseInt(parts[0]);

            if (parts.length < 3) {
                if (isReleaseCandidate() || isSnapshot) {
                    String[] subParts = lastPart.split("-");
                    minor = Integer.parseInt(subParts[0]);
                } else {
                    minor = Integer.parseInt(lastPart);
                }
            } else {
                minor = Integer.parseInt(parts[1]);
                if (isReleaseCandidate() || isSnapshot) {
                    String[] subParts = lastPart.split("-");
                    revision = Integer.parseInt(subParts[0]);
                } else {
                    revision = Integer.parseInt(lastPart);
                }
            }
        } else {
            throw new NativeProgramVersionException("Missing version format " + version);
        }
    }

    public static boolean less(String a, String b) throws NativeProgramVersionException {
        NativeProgramVersion aVersion = new NativeProgramVersion(a);
        NativeProgramVersion bVersion = new NativeProgramVersion(b);
        return aVersion.compare(bVersion) == -1;
    }

    public static boolean greater(String a, String b) throws NativeProgramVersionException {
        NativeProgramVersion aVersion = new NativeProgramVersion(a);
        NativeProgramVersion bVersion = new NativeProgramVersion(b);
        return aVersion.compare(bVersion) == 1;
    }

    public static boolean equals(String a, String b) throws NativeProgramVersionException {
        NativeProgramVersion aVersion = new NativeProgramVersion(a);
        NativeProgramVersion bVersion = new NativeProgramVersion(b);
        return aVersion.compare(bVersion) == 0;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getRevision() {
        return revision;
    }

    public boolean isSnapshot() {
        return isSnapshot;
    }

    public boolean isReleaseCandidate() {
        return releaseCandidate > -1;
    }

    public int getReleaseCandidate() {
        return releaseCandidate;
    }

    private boolean checkIsSnapshot(String s) {
        return s.contains("-SNAPSHOT");
    }

    public int compare(NativeProgramVersion b) {

        int majorCompare = Integer.compare(major, b.major);

        if (majorCompare == 0) {

            int minorCompare = Integer.compare(minor, b.minor);

            if (minorCompare == 0) {

                int revisionCompare = Integer.compare(revision, b.revision);

                if (revisionCompare == 0) {

                    if (isSnapshot) {
                        return b.isSnapshot ? 0 : -1;
                    } else if (isReleaseCandidate()) {
                        if (b.isReleaseCandidate()) {
                            return Integer.compare(releaseCandidate, b.releaseCandidate);
                        } else if (b.isSnapshot) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else {
                        return b.isReleaseCandidate() || b.isSnapshot ? 1 : 0;
                    }

                } else {
                    return revisionCompare;
                }
            } else {
                return minorCompare;
            }

        } else {
            return majorCompare;
        }
    }

    public int compare(String b) throws NativeProgramVersionException {
        return compare(new NativeProgramVersion(b));
    }

    private int parseIsReleaseCandidate(String s) {
        s = s.toLowerCase();
        if (s.contains("-rc")) {
            String[] parts = s.split("-rc");
            return Integer.parseInt(parts[parts.length - 1]);
        } else {
            return -1;
        }
    }

    public static class NativeProgramVersionException extends Throwable {
        public NativeProgramVersionException(String msg) {
            super(msg);
        }
    }
}
