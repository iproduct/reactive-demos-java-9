package provider.impl;

import provider.MessageProvider;

public class HelloWorldMessageProvider implements MessageProvider {
    @Override
    public String getMessage() {
        return "Hello Java 9 Modularity!!!";
    }
}
