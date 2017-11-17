
module org.iproduct.demo.profiler.client {
    requires java.se;
    requires jdk.incubator.httpclient;
    requires gson; 
	exports org.iproduct.demo.profiler.client;
	exports org.iproduct.demo.profiler.client.model;
	opens org.iproduct.demo.profiler.client.model to gson;
}