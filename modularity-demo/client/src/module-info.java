import provider.MessageProvider;
import renderer.MessageRenderer;

module client {
    requires provider;
    requires renderer;
	uses MessageProvider;
	uses MessageRenderer;
	exports client;
}