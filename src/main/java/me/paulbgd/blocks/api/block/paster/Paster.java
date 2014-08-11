package me.paulbgd.blocks.api.block.paster;

/**
 * The official Pasters supplied by Blocks.
 */
public class Paster {

    /**
     * The Simple Paster, which tries to just paste all of the blocks
     */
    public static final BlockPaster SIMPLE_PASTER = new SimplePaster();
    /**
     * The "Async" Paster, which will paste the blocks in chunks.
     */
    public static final BlockPaster ASYNC_PASTER = new AsyncPaster() {
        @Override
        public void finish() {
            try {
                throw new IllegalAccessException("You cannot stop the main async paster!");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    };

}
