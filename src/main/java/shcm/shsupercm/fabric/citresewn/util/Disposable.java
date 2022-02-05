package shcm.shsupercm.fabric.citresewn.util;

@FunctionalInterface
public interface Disposable {
    String ENTRYPOINT = "citresewn:dispose";

    void dispose();
}
