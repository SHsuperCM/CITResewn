package shcm.shsupercm.fabric.citresewn.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.IOUtils;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.pack.CITParser;
import shcm.shsupercm.fabric.citresewn.pack.cits.CITItem;

import java.io.*;

public class CITResewnConfig {
    public boolean enabled = true;
    public boolean mute_errors = false;
    public boolean mute_warns = false;

    private static final File FILE = new File("config/citresewn.json");
    public static CITResewnConfig INSTANCE() {
        return CITResewn.INSTANCE.config;
    }

    public static CITResewnConfig read() {
        if (!FILE.exists())
            return new CITResewnConfig().write();

        CITParser.REGISTRY.put("test", CITItem::new);

        Reader reader = null;
        try {
            return new Gson().fromJson(reader = new FileReader(FILE), CITResewnConfig.class);
        } catch (Exception e) {
            CITResewn.LOG.error("Couldn't load config");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    public CITResewnConfig write() {
        Gson gson = new Gson();
        JsonWriter writer = null;
        try {
            writer = gson.newJsonWriter(new FileWriter(FILE));
            writer.setIndent("    ");

            gson.toJson(gson.toJsonTree(this, CITResewnConfig.class), writer);
        } catch (Exception e) {
            CITResewn.LOG.error("Couldn't save config");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return this;
    }
}
