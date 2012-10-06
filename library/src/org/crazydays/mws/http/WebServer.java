package org.crazydays.mws.http;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
    extends Thread
{
    private final static int BACKLOG = 5;

    private int port;

    private Map<String, HttpRequestHandler> handlers;

    private ServerSocket listenSocket;
    private BasicHttpProcessor processor;
    private HttpContext context;
    private HttpService service;
    private HttpRequestHandlerRegistry registry;

    public WebServer(int port)
    {
        this.port = port;
        this.handlers = new HashMap<String, HttpRequestHandler>();
    }

    public void addHandler(String path, HttpRequestHandler handler)
    {
        handlers.put(path, handler);
    }

    @Override
    public void interrupt()
    {
        teardownListenSocket();
        super.interrupt();
    }

    @Override
    public void run()
    {
        try {
            setupListenSocket();
            setupHttpServer();

            handleRequests();
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), e.toString());
        } finally {
            teardownListenSocket();
        }
    }

    private void setupListenSocket()
        throws IOException
    {
        listenSocket = new ServerSocket(port, BACKLOG);
        listenSocket.setReuseAddress(true);
    }

    private void teardownListenSocket()
    {
        if (listenSocket != null) {
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
        processor = new BasicHttpProcessor();
        processor.addInterceptor(new ResponseDate());
        processor.addInterceptor(new ResponseServer());
        processor.addInterceptor(new ResponseContent());
        processor.addInterceptor(new ResponseConnControl());

        service =
            new HttpService(processor, new DefaultConnectionReuseStrategy(),
                new DefaultHttpResponseFactory());

        context = new BasicHttpContext();

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
                Socket socket = listenSocket.accept();

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
        DefaultHttpServerConnection connection =
            new DefaultHttpServerConnection();
        connection.bind(socket, new BasicHttpParams());

        return connection;
    }

    private void handleHttpServerConnection(HttpServerConnection connection)
        throws IOException, HttpException
    {
        service.handleRequest(connection, context);
    }
}
