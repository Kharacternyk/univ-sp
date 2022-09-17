package lab;

public class SystemOutStringSink implements StringSink {
    public void feedString(String string) {
        System.out.println(string);
    }
}
