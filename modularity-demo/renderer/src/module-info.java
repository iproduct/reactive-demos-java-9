module renderer {
    requires provider;
    exports renderer;
//    exports renderer.impl;
    provides renderer.MessageRenderer with renderer.impl.StandardOutMessageRenderer;
} 