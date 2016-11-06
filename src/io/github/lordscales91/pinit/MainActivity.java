package io.github.lordscales91.pinit;

import io.github.lordscales91.pinit.utils.CallbackReceiver;
import io.github.lordscales91.pinit.utils.PlayServicesUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;
import com.pinterest.android.pdk.PDKUser;

public class MainActivity extends Activity implements CallbackReceiver {

	private static final String LOAD_URL = "load.url";
	private EditText edtUrl;
	private Button btnLoadUrl;
	private GridView gvImages;
	private String pinNote;
	private PDKUser user;
	private String[] images;
	private Button btnAuthorize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Fix SSLv3 problem. See: http://stackoverflow.com/a/36892715/3107765
		PlayServicesUtils.updateProvider(this);
		edtUrl = (EditText) findViewById(R.id.edtUrl);
		gvImages = (GridView) findViewById(R.id.gvImages);
		gvImages.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				gvImages_onItemClick(position);
			}
		});
		btnLoadUrl = (Button) findViewById(R.id.btnLoadUrl);
		btnLoadUrl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnLoad_onClick();
			}
		});
		btnAuthorize = (Button) findViewById(R.id.btnAuthorize);
		btnAuthorize.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				btnAuthorize_onClick();
			}
		});
		PDKClient.setDebugMode(true);
		PDKClient.configureInstance(this, "4860375270619622279");
		PDKClient.getInstance().onConnect(this); // Save the access token and authorize
		// Race condition here. At this point the token probably is not saved yet.
		// Therefore, the silent login will apparently fail
		if(this.user == null) {
			PDKClient.getInstance().silentlyLogin(new PDKCallback() {
				@Override
				public void onSuccess(PDKResponse response) {
					Log.d("PDKLogin", "Silent login success");
					onLoginSuccess(response, true);
				}

				@Override
				public void onFailure(PDKException exception) {
					onLoginFailure(exception, true);
				}
			});
		}
		handleImplicitIntent(getIntent());
	}

	private void handleImplicitIntent(Intent intent) {
		if(intent.getData() != null) {
			try {
				new URL(intent.getDataString());
				// It's a valid URL
				edtUrl.setText(intent.getDataString());
				btnLoad_onClick();
			} catch (MalformedURLException e) {}
			
		} else if(intent.hasExtra(Intent.EXTRA_TEXT)) {
			try {
				new URL(intent.getStringExtra(Intent.EXTRA_TEXT));
				// It's a valid URL
				edtUrl.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
				btnLoad_onClick();
			} catch (MalformedURLException e) {}
		}
	}

	protected void gvImages_onItemClick(int position) {
		Intent createpin = new Intent(this, CreatePinActivity.class);
		createpin.putExtra(LordConst.IMG_URL, images[position]);
		createpin.putExtra(LordConst.PIN_LINK, edtUrl.getText().toString());
		createpin.putExtra(LordConst.PIN_NOTE, pinNote);
		startActivity(createpin);
	}

	protected void btnAuthorize_onClick() {
		btnAuthorize.setEnabled(false);
		List<String> scopes = new ArrayList<String>();
		scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
		scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
		PDKClient.getInstance().login(this, scopes, new PDKCallback() {
			@Override
			public void onSuccess(PDKResponse response) {
				Log.d("PDKLogin", response.getData().toString());
				onLoginSuccess(response);
			}

			@Override
			public void onFailure(PDKException exception) {
				onLoginFailure(exception, false);
			}
		});
	}

	protected void onLoginSuccess(PDKResponse response, boolean silent) {
		user = response.getUser();
		btnAuthorize.setVisibility(View.GONE);
		if (silent) {
			Toast.makeText(this, getString(R.string.welcome_back).replace("{name}", user.getFirstName()),
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, getString(R.string.logged_in).replace("{name}", user.getFirstName()),
					Toast.LENGTH_LONG).show();
		}
	}

	protected void onLoginSuccess(PDKResponse response) {
		onLoginSuccess(response, false);
	}

	protected void onLoginFailure(PDKException exception, boolean silent) {
		Log.e(getClass().getSimpleName(), exception.getDetailMessage());
		if (silent) {
			if(!PDKClient.getInstance().isAuthenticated()) { // Extra check to deal with a race condition
				// make the button visible to let the user authorize the app
				btnAuthorize.setVisibility(View.VISIBLE);
				btnAuthorize.setEnabled(true);
			}
		} else {			
			Toast.makeText(this, "Unable to authorize app", Toast.LENGTH_LONG)
					.show();
		}
	}

	protected void btnLoad_onClick() {
		btnLoadUrl.setEnabled(false);
		gvImages.setAdapter(null); // Empty the gridview
		this.images = null;
		WebfetchAsyncTask t = new WebfetchAsyncTask(this, LOAD_URL);
		t.execute(edtUrl.getText().toString());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		PDKClient.getInstance().onOauthResponse(requestCode, resultCode, data);
	}

	@Override
	public void receiveData(Object data, String tag) {
		btnLoadUrl.setEnabled(true);
		if(data instanceof Exception) {
			Toast.makeText(this, "Error loading url!", Toast.LENGTH_LONG).show();
		} else {
			if (tag.equals(LOAD_URL)) {
				String[] imageData = data.toString().split(";");
				images = Arrays.copyOfRange(imageData, 0, imageData.length - 1);
				pinNote = imageData[imageData.length - 1];
				ImageAdapter adapter = new ImageAdapter(this, images);
				gvImages.setAdapter(adapter);
			}
		}
	}
}