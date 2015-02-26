package reboot.vnote;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import ddbb.Conexion;
import ddbb.Note;

public class NoteActivity extends Activity {

	EditText tittle, content;
	Note noteOpen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);

		tittle = (EditText) findViewById(R.id.et_title);
		content = (EditText) findViewById(R.id.et_content);
		Intent thisIntent = getIntent();

		if (thisIntent.hasExtra("id")) {
			noteOpen = FillFields(thisIntent.getExtras().getInt("id") + "");
		}

	}

	private Note FillFields(String id) {
		Conexion con = new Conexion(getApplicationContext(), "DBNotes.db",
				null, 1);
		SQLiteDatabase db = con.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM notes WHERE id =" + id, null);

		Note note_from_select = new Note(c.getShort(0), c.getString(1),
				c.getString(2));

		tittle.setText(note_from_select.getTitle());
		content.setText(note_from_select.getContent());
		return note_from_select;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.note, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.save:
			// metodoSave()
			return true;
		case R.id.speach:
			// metodoSpeach()
			return true;
		case R.id.listen:
			// metodoListen()
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void CheckTypeNote() {
		if (CheckFields() && noteOpen != null) {
			//UPDATE
		}else{
			//INSERT
		}
	}

	private boolean CheckFields() {
		String s1 = tittle.getText().toString().trim();
		String s2 = content.getText().toString().trim();

		if (s1.length() == 0 || s2.length() == 0) {
			Toast.makeText(this, "Someone fields is empty", Toast.LENGTH_LONG)
					.show();
			return false;
		} else {
			return true;
		}

	}

}
