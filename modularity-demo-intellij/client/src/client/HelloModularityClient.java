package client;

import provider.impl.HelloWorldMessageProvider;
import renderer.impl.StandardOutMessageRenderer;

public class HelloModularityClient {

    public static void main(String[] args) {
        HelloWorldMessageProvider provider = new HelloWorldMessageProvider();
        StandardOutMessageRenderer renderer = new StandardOutMessageRenderer();
        renderer.setMessageProvider(provider);
        renderer.render();
    }
}
