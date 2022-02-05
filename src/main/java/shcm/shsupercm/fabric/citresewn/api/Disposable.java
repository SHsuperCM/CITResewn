package shcm.shsupercm.fabric.citresewn.api;

@FunctionalInterface
public interface Disposable {
    String ENTRYPOINT = "citresewn:dispose";

    void dispose();
}
