package client;

import java.util.Iterator;
import java.util.ServiceLoader;

import provider.MessageProvider;
import renderer.MessageRenderer;
//import provider.impl.HelloWorldMessageProvider;
//import renderer.impl.StandardOutMessageRenderer;

public class HelloModularityClient {

    public static void main(String[] args) {
//      MessageProvider provider = new MessageProvider();
    	ServiceLoader<MessageProvider> loaderProvider = ServiceLoader.load(MessageProvider.class);
    	Iterator<MessageProvider> iterProvider = loaderProvider.iterator();
	    if (!iterProvider.hasNext()) throw new RuntimeException("No MessageProvider providers found!");
	    MessageProvider provider = iterProvider.next();

//	    MessageRenderer renderer = new StandardOutMessageRenderer();
	    ServiceLoader<MessageRenderer> loaderRenderer = ServiceLoader.load(MessageRenderer.class);
    	Iterator<MessageRenderer> iterRenderer = loaderRenderer.iterator();
	    if (!iterRenderer.hasNext()) throw new RuntimeException("No MessageRenderer providers found!");
	    MessageRenderer renderer = iterRenderer.next();

        renderer.setMessageProvider(provider);
        renderer.render();
    }
}
