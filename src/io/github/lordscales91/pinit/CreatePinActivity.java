package io.github.lordscales91.pinit;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.pinterest.android.pdk.PDKBoard;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;

public class CreatePinActivity extends Activity {

	private ImageView imgToPin;
	private EditText edtPinNote;
	private Spinner spBoards;
	private Button btnCreatePin;
	private String pinNote;
	private List<PDKBoard> boards;
	private String imgUrl;
	private String pinLink;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_pin);
		imgToPin = (ImageView)findViewById(R.id.imgToPin);
		edtPinNote = (EditText)findViewById(R.id.edtPinNote);
		spBoards = (Spinner)findViewById(R.id.spBoards);
		btnCreatePin = (Button)findViewById(R.id.btnCreatePin);
		btnCreatePin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createPin();
			}
		});
		fillViews();
	}

	protected void createPin() {
		btnCreatePin.setEnabled(false);
		String board = boards.get(spBoards.getSelectedItemPosition()).getUid();
		PDKClient.getInstance().createPin(edtPinNote.getText().toString(), board, imgUrl, pinLink,
				new PDKCallback() {
			@Override
			public void onSuccess(PDKResponse response) {
				createPin_Success(response);
			}
			@Override
			public void onFailure(PDKException exception) {
				onError(exception);
			}
		});
	}

	protected void onError(Exception exception) {
		btnCreatePin.setEnabled(true);
		Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
	}

	protected void createPin_Success(PDKResponse response) {
		btnCreatePin.setEnabled(true);
		Toast.makeText(this, R.string.pin_created, Toast.LENGTH_LONG).show();
	}

	private void fillViews() {
		Intent calling = getIntent();
		if(calling.hasExtra(LordConst.IMG_URL)) {
			imgUrl = calling.getStringExtra(LordConst.IMG_URL);
			((TextView)findViewById(R.id.tvUrl)).setText(imgUrl);
			ImageLoader.getInstance().displayImage(imgUrl, imgToPin);
		}
		if(calling.hasExtra(LordConst.PIN_LINK)) {
			pinLink = calling.getStringExtra(LordConst.PIN_LINK);
		}
		pinNote = calling.getStringExtra(LordConst.PIN_NOTE);
		if(pinNote == null || pinNote.isEmpty()) {
			pinNote = "Pinned by Pin It! for Android";
		}
		edtPinNote.setText(pinNote);
		PDKClient.getInstance().getMyBoards("id,name", new PDKCallback() {
			@Override
			public void onSuccess(PDKResponse response) {
				fillBoardsSpinner(response);
			}
			@Override
			public void onFailure(PDKException exception) {
				onError(exception);
			}
		});
	}

	protected void fillBoardsSpinner(PDKResponse response) {
		boards = response.getBoardList();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		for(PDKBoard b:boards) {
			adapter.add(b.getName());
		}
		spBoards.setAdapter(adapter);
	}
}
