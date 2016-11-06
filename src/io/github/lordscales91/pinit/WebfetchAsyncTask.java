package io.github.lordscales91.pinit;

import io.github.lordscales91.pinit.utils.CallbackReceiver;

import java.io.IOException;

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
		Document doc;
		String result="";
		try {
			doc = Jsoup.connect(params[0]).get();
			Elements images = doc.getElementsByTag("img");
			for (Element e : images) {
				result += e.attr("abs:src")+";";
			}
			result += doc.title();
		} catch (IOException e) {
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
