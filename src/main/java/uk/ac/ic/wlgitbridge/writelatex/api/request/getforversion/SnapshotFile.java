package uk.ac.ic.wlgitbridge.writelatex.api.request.getforversion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import uk.ac.ic.wlgitbridge.bridge.RawFile;
import uk.ac.ic.wlgitbridge.writelatex.api.request.base.JSONSource;

/**
 * Created by Winston on 06/11/14.
 */
public class SnapshotFile extends RawFile implements JSONSource {

    private String path;
    private byte[] contents;

    public SnapshotFile(JsonElement json) {
        fromJSON(json);
    }

    @Override
    public void fromJSON(JsonElement json) {
        JsonArray jsonArray = json.getAsJsonArray();
        contents = jsonArray.get(0).getAsString().getBytes();
        path = jsonArray.get(1).getAsString();
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public byte[] getContents() {
        return contents;
    }

    /* Mock server */

    public SnapshotFile(String contents, String path) {
        this.path = path;
        if (contents != null) {
            this.contents = contents.getBytes();
        } else {
            this.contents = new byte[0];
        }
    }

    public JsonElement toJson() {
        JsonArray jsonThis = new JsonArray();
        jsonThis.add(new JsonPrimitive(new String(contents)));
        jsonThis.add(new JsonPrimitive(path));
        return jsonThis;
    }

}
