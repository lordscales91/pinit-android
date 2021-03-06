package io.github.lordscales91.pinit;

import io.github.lordscales91.pinit.utils.CallbackReceiver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pinterest.android.pdk.PDKBoard;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKPin;
import com.pinterest.android.pdk.PDKResponse;

public class CreatePinActivity extends Activity implements CallbackReceiver {

	private ImageView imgToPin;
	private EditText edtPinNote;
	private Spinner spBoards;
	private Button btnCreatePin;
	private String pinNote;
	private List<PDKBoard> boards;
	private String imgUrl = null;
	private String pinLink = null;
	private EditText edtPinUrl;
	private String imgPath;
	private static final int CREATE_BOARD = 1;
	private static final String UPLOAD_PIN = "upload.pin";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_pin);
		imgToPin = (ImageView) findViewById(R.id.imgToPin);
		edtPinNote = (EditText) findViewById(R.id.edtPinNote);
		edtPinUrl = (EditText) findViewById(R.id.edtPinUrl);
		spBoards = (Spinner) findViewById(R.id.spBoards);
		spBoards.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int i, long l) {
				spBoards_onItemSelected(i);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			}
		});
		btnCreatePin = (Button) findViewById(R.id.btnCreatePin);
		btnCreatePin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnCreatePin_onClick();
			}
		});
		fillViews();
	}

	protected void btnCreatePin_onClick() {
		int position = spBoards.getSelectedItemPosition() - 2;
		if (position < 0) {
			Toast.makeText(this, R.string.error_no_board, Toast.LENGTH_LONG)
					.show();
		} else {
			btnCreatePin.setEnabled(false);
			String board = boards.get(position).getUid();
			if(imgPath != null) {
				UploadPinAsyncTask t = new UploadPinAsyncTask(board, pinNote, this, UPLOAD_PIN);
				t.execute(imgPath);
			} else {
				String sourceUrl = pinLink;
				try {
					new URL(edtPinUrl.getText().toString());
					// This is a valid url
					sourceUrl = edtPinUrl.getText().toString();
				} catch (MalformedURLException e) {}
				PDKClient.getInstance().createPin(edtPinNote.getText().toString(),
						board, imgUrl, sourceUrl, new PDKCallback() {
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
		}
	}

	protected void onError(Exception exception) {
		btnCreatePin.setEnabled(true);
		Toast.makeText(this, R.string.error_pin, Toast.LENGTH_LONG).show();
	}

	protected void createPin_Success(PDKResponse response) {
		btnCreatePin.setEnabled(true);
		Toast.makeText(this, R.string.pin_created, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CREATE_BOARD) {
			if (resultCode == RESULT_OK) {
				PDKBoard b = new PDKBoard();
				b.setName(data.getStringExtra(LordConst.BOARD_NAME));
				b.setUid(data.getStringExtra(LordConst.BOARD_ID));
				boards.add(b);
				updateSpinner();
				// boards.size() + 2 = length of the spinner items.
				// boards.size() + 2 - 1 = last index.
				spBoards.setSelection(boards.size() + 1);
			} else {
				spBoards.setSelection(0);
			}
		}
	}

	private void fillViews() {
		Intent calling = getIntent();
		if (calling.hasExtra(LordConst.IMG_URL)) {
			imgUrl = calling.getStringExtra(LordConst.IMG_URL);
			((TextView) findViewById(R.id.tvUrl)).setText(imgUrl);
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.cacheInMemory(true).cacheOnDisk(true)
					.showImageOnLoading(R.drawable.ic_stub)
					.showImageForEmptyUri(R.drawable.ic_image)
					.showImageOnFail(R.drawable.ic_error)
					.considerExifParams(true).build();
			ImageLoader.getInstance().displayImage(imgUrl, imgToPin, options);
		} else if(calling.hasExtra(LordConst.IMG_PATH)) {
			edtPinUrl.setEnabled(false); // Don't allow to specify a link for an uploaded pin
			imgPath = calling.getStringExtra(LordConst.IMG_PATH);
			imgToPin.setImageURI(Uri.fromFile(new File(imgPath)));
		}
		if (calling.hasExtra(LordConst.PIN_LINK)) {
			edtPinUrl.setText(calling.getStringExtra(LordConst.PIN_LINK));
			pinLink = calling.getStringExtra(LordConst.PIN_LINK);
		}
		pinNote = calling.getStringExtra(LordConst.PIN_NOTE);
		if (pinNote == null || pinNote.isEmpty()) {
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

	private void updateSpinner() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		adapter.add(getString(R.string.select));
		adapter.add(getString(R.string.create_board) + " ...");
		for (PDKBoard b : boards) {
			adapter.add(b.getName());
		}
		spBoards.setAdapter(adapter);
	}

	protected void fillBoardsSpinner(PDKResponse response) {
		boards = response.getBoardList();
		updateSpinner();
		if (boards.size() > 0) {
			// Select the first board by default
			spBoards.setSelection(2);
		}
	}

	protected void spBoards_onItemSelected(int pos) {
		if (pos == 1) { // Create new board
			Intent createBoard = new Intent(this, CreateBoardActivity.class);
			startActivityForResult(createBoard, CREATE_BOARD);
		}
	}

	@Override
	public void receiveData(Object data, String tag) {
		if(data instanceof Exception) {
			onError((Exception) data);
		} else {
			if(tag.equals(UPLOAD_PIN)) {
				PDKPin pin = (PDKPin)data;
				Log.d(getClass().getSimpleName(), "created pin "+pin.getNote());
				createPin_Success(null);
			}
		}
	}
}
