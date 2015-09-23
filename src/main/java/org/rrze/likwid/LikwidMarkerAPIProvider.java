package org.rrze.likwid;

import java.io.*;

public class LikwidMarkerAPIProvider {
    private final static ILikwidMarker likwidMarker;

    static {
        ILikwidMarker _likwidMarker = DummyLikwidMarker.INSTANCE;
        try {
            if (System.getProperty("LIKWID_PERFMON") != null) {
                String path = extractLib();
                System.load(path);
                LikwidMarkerAPI api = new LikwidMarkerAPI();
                _likwidMarker = new NativeLikwidMarker(api);
                System.err.println("Likwid markers are enabled.");
            }
        } catch (UnsatisfiedLinkError e) {
            System.err.printf("Cannot load likwid-api library. Markers are disabled. (%s)%n", e);
        }
        likwidMarker = _likwidMarker;
    }

    public static ILikwidMarker getInstance() {
        return likwidMarker;
    }

    private static String extractLib() {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"), "likwid-api");
        tmpDir.mkdirs();
        File libFile = new File(tmpDir, "liblikwid-api.so");
        libFile.deleteOnExit();
        InputStream in = LikwidMarkerAPI.class.getResourceAsStream("/liblikwid-api.so");
        OutputStream out = null;
        try {
            out = new FileOutputStream(libFile);
            copyStream(in, out);
        } catch (Exception e) {
            System.err.printf("Cannot extract library to: %s (%s)%n", libFile.getAbsolutePath(), e.getMessage());
        } finally {
            if (out != null) try {
                out.close();
            } catch (IOException ignored) {
            }
        }

        return libFile.getAbsolutePath();
    }

    private static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[4096];
        int size = 0;
        while (size >= 0) {
            size = in.read(buf);
            if (size > 0) out.write(buf, 0, size);
        }
    }
}
