package io.github.lordscales91.pinit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pinterest.android.pdk.PDKBoard;
import com.pinterest.android.pdk.PDKCallback;
import com.pinterest.android.pdk.PDKClient;
import com.pinterest.android.pdk.PDKException;
import com.pinterest.android.pdk.PDKResponse;

public class CreateBoardActivity extends Activity {

    private EditText edtBoardName;
    private EditText edtBoardDescription;
    private Button btnCreateBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_board);
        edtBoardName = (EditText)findViewById(R.id.edtBoardName);
        edtBoardDescription = (EditText)findViewById(R.id.edtBoardDescription);
        btnCreateBoard = (Button)findViewById(R.id.btnCreateBoard);
        btnCreateBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCreateBoard_onClick();
            }
        });
    }

    protected void btnCreateBoard_onClick() {
        String boardName = edtBoardName.getText().toString();
        if(boardName == null || boardName.isEmpty()) {
            Toast.makeText(this, "Supply a name", Toast.LENGTH_LONG).show();
        }  else {
            String boardDescription = edtBoardDescription.getText().toString();
            if(boardDescription==null || boardDescription.isEmpty()) {
                boardDescription = "Created by Pin It! For Android";
            }
            btnCreateBoard.setEnabled(false);
            PDKClient.getInstance().createBoard(boardName, boardDescription,
                    new PDKCallback() {
                        @Override
                        public void onSuccess(PDKResponse response) {
                            onCreateBoardSuccess(response);
                        }

                        @Override
                        public void onFailure(PDKException exception) {
                            onError(exception);
                        }
                    });
        }
    }

    protected void onError(PDKException exception) {
        btnCreateBoard.setEnabled(true);
        Toast.makeText(this, exception.getDetailMessage(), Toast.LENGTH_LONG).show();
    }

    protected void onCreateBoardSuccess(PDKResponse response) {
        btnCreateBoard.setEnabled(true);
        Intent data = new Intent();
        PDKBoard board = response.getBoard();
        data.putExtra(LordConst.BOARD_NAME, board.getName());
        data.putExtra(LordConst.BOARD_ID, board.getUid());
        setResult(RESULT_OK, data);
        finish();
    }
}
