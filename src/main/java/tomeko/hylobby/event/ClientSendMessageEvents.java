package tomeko.hylobby.event;

//? if 1.8.9 {
/*public final class ClientSendMessageEvents {
    private ClientSendMessageEvents() {}

    public static final Event<AllowCommand> ALLOW_COMMAND = Event.create(AllowCommand.class, listeners -> command -> {
        for (AllowCommand listener : listeners) {
            if (!listener.allowSendCommandMessage(command)) {
                return false;
            }
        }
        return true;
    });

    @FunctionalInterface
    public interface AllowCommand {
        boolean allowSendCommandMessage(String command);
    }
}
*///?}
