package shcm.shsupercm.fabric.citresewn.api;

@FunctionalInterface
public interface CITDisposable {
    String ENTRYPOINT = "citresewn:dispose";

    void dispose();
}
