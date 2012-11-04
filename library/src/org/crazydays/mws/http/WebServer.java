package org.crazydays.mws.http;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import junit.framework.AssertionFailedError;

import android.util.Log;

import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

public class WebServer
{
    private final static int BACKLOG = 5;

    private final int port;

    private final Map<String, HttpRequestHandler> handlers;

    private ServerSocket listenSocket;
    private BasicHttpProcessor processor;
    private HttpContext context;
    private HttpService service;
    private HttpRequestHandlerRegistry registry;

    private final ServiceThread thread;

    public WebServer(int port)
    {
        this.port = port;
        this.handlers = new HashMap<String, HttpRequestHandler>();
        this.thread = new ServiceThread();
    }

    public void addHandler(String path, HttpRequestHandler handler)
    {
        handlers.put(path, handler);
    }

    public void start()
    {
        try {
            setupListenSocket();
            setupHttpServer();
            thread.start();
            // TODO: Thread.yield();
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), e.toString());
        }
    }

    public void stop()
    {
        thread.interrupt();

        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e(getClass().getSimpleName(), e.toString());
            throw new AssertionFailedError("Failed to stop MockWebServer");
        }
    }

    private void setupListenSocket()
        throws IOException
    {
        Log.i(getClass().getSimpleName(), "Setting up server socket");
        listenSocket = new ServerSocket(port, BACKLOG);
        listenSocket.setReuseAddress(true);
    }

    private void teardownListenSocket()
    {
        Log.i(getClass().getSimpleName(), "Closing web server");
        if (listenSocket != null) {
            Log.i(getClass().getSimpleName(), "Closing server socket");
            try {
                listenSocket.close();
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Unable to close listener: "
                    + e.toString());
            }
        }
    }

    private void setupHttpServer()
    {
        Log.i(getClass().getSimpleName(), "Setting up basic http processor");
        processor = new BasicHttpProcessor();
        processor.addInterceptor(new ResponseDate());
        processor.addInterceptor(new ResponseServer());
        processor.addInterceptor(new ResponseContent());
        processor.addInterceptor(new ResponseConnControl());

        Log.i(getClass().getSimpleName(), "Setting up http service");
        service =
            new HttpService(processor, new DefaultConnectionReuseStrategy(),
                new DefaultHttpResponseFactory());

        Log.i(getClass().getSimpleName(), "Setting up http context");
        context = new BasicHttpContext();

        Log.i(getClass().getSimpleName(), "Setting up http handler registry");
        registry = new HttpRequestHandlerRegistry();
        for (String path : handlers.keySet()) {
            Log.i(getClass().getSimpleName(), "Registering: " + path);
            registry.register(path, handlers.get(path));
        }

        service.setHandlerResolver(registry);
    }

    private void handleRequests()
    {
        do {
            try {
                Log.i(getClass().getSimpleName(), "Blocking");
                Socket socket = listenSocket.accept();

                Log.i(getClass().getSimpleName(), "Accepting connection");
                handleHttpServerConnection(buildHttpServerConnection(socket));
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), e.toString());
            } catch (HttpException e) {
                Log.e(getClass().getSimpleName(), e.toString());
            }
        } while (!listenSocket.isClosed());
    }

    private HttpServerConnection buildHttpServerConnection(Socket socket)
        throws IOException
    {
        Log.i(getClass().getSimpleName(), "Building server connection");
        DefaultHttpServerConnection connection =
            new DefaultHttpServerConnection();
        connection.bind(socket, new BasicHttpParams());

        return connection;
    }

    private void handleHttpServerConnection(HttpServerConnection connection)
        throws IOException, HttpException
    {
        Log.i(getClass().getSimpleName(), "Passing connection to handler");
        service.handleRequest(connection, context);
    }

    private class ServiceThread
        extends Thread
    {
        @Override
        public void interrupt()
        {
            Log.i(getClass().getSimpleName(), "Interrupted");
            teardownListenSocket();
            super.interrupt();
        }

        @Override
        public void run()
        {
            Log.i(getClass().getSimpleName(), "Run");
            handleRequests();
        }
    }
}
