package tae.co.uk.ltd.mvp.model.exception;

public class LiveTrafficDisruptionException extends RuntimeException {

    public LiveTrafficDisruptionException() {
        super();
    }

    public LiveTrafficDisruptionException(String detailMessage) {
        super(detailMessage);
    }

    public LiveTrafficDisruptionException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public LiveTrafficDisruptionException(Throwable throwable) {
        super(throwable);
    }
}
