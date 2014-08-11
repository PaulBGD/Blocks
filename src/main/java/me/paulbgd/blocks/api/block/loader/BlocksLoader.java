package me.paulbgd.blocks.api.block.loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import me.paulbgd.blocks.api.block.Blocks;

/**
 * A class used to load and save Blocks objects.
 * <p/>
 * {@link me.paulbgd.blocks.api.block.Blocks}
 */
public interface BlocksLoader {

    /**
     * Gets the name of this BlocksLoader. Used in files.
     *
     * @return the name
     */
    public String getName();

    /**
     * Loads a Blocks object from the specified InputStream.
     *
     * @param inputStream the input stream to load from
     * @return new blocks object
     * @throws IOException if there's an error loading it
     */
    public Blocks load(InputStream inputStream) throws IOException;

    /**
     * Saves a set of blocks to a said OutputStream.
     *
     * @param blocks       the blocks to save
     * @param outputStream the output stream to save to
     * @throws IOException if there's an error saving
     */
    public void save(Blocks blocks, OutputStream outputStream) throws IOException;

}
