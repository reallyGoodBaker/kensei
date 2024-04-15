package top.rgb39.kensei_client;

public class KenseiListeners {
    final static Runnable EMPTY_LISTENER = () -> {};
    public static Runnable RENDER_LEVEL = EMPTY_LISTENER;
    public static Runnable CLIENT_READY = EMPTY_LISTENER;
}
