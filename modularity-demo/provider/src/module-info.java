module provider {
    exports provider;
    provides provider.MessageProvider with provider.impl.HelloWorldMessageProvider;
}