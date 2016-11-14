package io.github.lordscales91.pinit;

import io.github.lordscales91.pinit.utils.CallbackReceiver;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;

public class WebfetchAsyncTask extends AsyncTask<String, Void, String> {
	
	private CallbackReceiver receiver;
	private String tag;
	private Exception lastException = null;

	public WebfetchAsyncTask(CallbackReceiver receiver, String tag) {
		this.receiver = receiver;
		this.tag = tag;
	}

	@Override
	protected String doInBackground(String... params) {
		String result="";
		try {
			OkHttpClient client = new OkHttpClient();
			Request req = new Request.Builder().url(params[0]).build();
			Response resp = client.newCall(req).execute();
			if("image".equals(resp.body().contentType().type())) {
				// A direct URL to a image is passed
				result += resp.request().url().toString()+";Pinned with Pin It! For Android";
				resp.close();
			} else {
				Document doc = Jsoup.parse(resp.body().string(),
						resp.request().url().toString()); // Handle redirects
				Elements images = doc.getElementsByTag("img");
				for (Element e : images) {
					result += e.attr("abs:src")+";";
				}
				result += doc.title();
			}
			
		} catch (IOException e) {
			this.lastException = e;
		} catch (Exception e) {
			this.lastException = e;
		}
		return result;
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(lastException == null) {
			this.receiver.receiveData(result, this.tag);			
		} else {
			this.receiver.receiveData(lastException, this.tag);
		}
	}

}
