package io.github.lordscales91.pinit;

import io.github.lordscales91.pinit.utils.CallbackReceiver;
import io.github.lordscales91.pinit.utils.FileUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKPin;
import com.pinterest.android.pdk.PDKResponse;

public class UploadPinAsyncTask extends AsyncTask<String, Void, PDKPin> {
	
	private static final String PINS_URL = "https://api.pinterest.com/v1/pins/";
	private String boardId;
	private String pinNote;
	private CallbackReceiver receiver;
	private String tag;
	private Exception lastException = null;
	

	public UploadPinAsyncTask(String boardId, String pinNote, CallbackReceiver receiver, String tag) {
		this.boardId = boardId;
		this.pinNote = pinNote;
		this.receiver = receiver;
		this.tag = tag;
	}

	@Override
	protected PDKPin doInBackground(String... params) {
		PDKPin pin = null;
		OkHttpClient client = new OkHttpClient();		
		File imgFile = new File(params[0]);		
		RequestBody body = new MultipartBody.Builder()
			.setType(MultipartBody.FORM)
			.addFormDataPart("board", boardId)
			.addFormDataPart("note", pinNote)
			.addFormDataPart("image", imgFile.getName(),
					RequestBody.create(MediaType.parse(FileUtils.getMimeType(imgFile)), imgFile))
			.build();
		String url = PINS_URL + "?access_token="+PDKClient.getInstance().getAccessToken();
		Request req = new Request.Builder().url(url).post(body).build();
		try {
			Response resp = client.newCall(req).execute();
			if(resp.isSuccessful() ||
					// Consider non-severe errors like 400 bad request.
					resp.code() < 500) {
				String json = resp.body().string();
				JSONObject jsobj = new JSONObject(json);
				PDKResponse apiResp = new PDKResponse(jsobj);
				if(apiResp.isValid()) {
					pin = apiResp.getPin();
				}
			}
			
		} catch (IOException e) {
			this.lastException = e;
		} catch (JSONException e) {
			this.lastException = e;
		}
		return pin;
	}
	
	@Override
	protected void onPostExecute(PDKPin result) {
		if(this.lastException == null) {
			this.receiver.receiveData(result, tag);
		} else {
			this.receiver.receiveData(this.lastException, tag);
		}
	}

}
