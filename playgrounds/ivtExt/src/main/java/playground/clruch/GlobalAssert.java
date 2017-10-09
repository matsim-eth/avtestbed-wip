package playground.clruch;

public class GlobalAssert {
    public static void that(boolean status) {
        assert status;
        if (!status)
            throw new RuntimeException();
    }
}
