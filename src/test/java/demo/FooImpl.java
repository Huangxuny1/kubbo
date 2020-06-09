package demo;

public class FooImpl implements IFoo{

    @Override
    public String say(String words) {
        return "hello " + words;
    }
}
