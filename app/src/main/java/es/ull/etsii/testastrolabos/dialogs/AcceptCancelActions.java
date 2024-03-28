package es.ull.etsii.testastrolabos.dialogs;

public interface AcceptCancelActions<T> extends AcceptAction<T>{
    void accept(T data);
    void cancel(T data);
}