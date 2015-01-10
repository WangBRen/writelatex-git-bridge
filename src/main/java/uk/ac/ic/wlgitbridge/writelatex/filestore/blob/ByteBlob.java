package uk.ac.ic.wlgitbridge.writelatex.filestore.blob;

import uk.ac.ic.wlgitbridge.writelatex.filestore.node.AttachmentNode;
import uk.ac.ic.wlgitbridge.writelatex.model.db.PersistentStoreAPI;

/**
 * Created by Winston on 14/11/14.
 */
public class ByteBlob extends Blob {

    private final byte[] contents;

    public ByteBlob(byte[] contents) {
        this.contents = contents;
    }

    @Override
    public byte[] getContents() {
        return contents;
    }

    @Override
    public void updatePersistentStore(PersistentStoreAPI persistentStore, AttachmentNode node) {
        persistentStore.addFileNodeBlob(node.getProjectName(), node.getFilePath(), node.isChanged(), contents);
    }

}