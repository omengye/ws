package io.omengye.ws.service;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.springframework.stereotype.Service;

@Service
public class HttpClientService {

	public void sslCallBack(AsyncHttpClient asyncHttpClient, String url, final List<Response> responses, final CountDownLatch latch) {
    	asyncHttpClient.prepareGet(url).execute(new AsyncCompletionHandler<Response>(){
    	    @Override
    	    public Response onCompleted(Response response) throws Exception{
    	    	responses.add(response);
    	    	latch.countDown();
    	        return response;
    	    }
    	    
    	    @Override
    	    public void onThrowable(Throwable t){
    	    	t.fillInStackTrace();
    	    	latch.countDown();
    	    }
    	});
    }
	
	
}
