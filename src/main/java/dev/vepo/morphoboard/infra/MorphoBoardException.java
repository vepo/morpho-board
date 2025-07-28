package dev.vepo.morphoboard.infra;

public class MorphoBoardException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MorphoBoardException(String message) {
        super(message);
    }

    public MorphoBoardException(String message, Throwable cause) {
        super(message, cause);
    }

    public MorphoBoardException(Throwable cause) {
        super(cause);
    }

}
