package reboot.vnote;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ddbb.Conexion;
import ddbb.Note;

public class MainActivity extends Activity {

	ListView lvNotes;
	ArrayList<Note> list = new ArrayList<Note>();
	Note nota;
	SQLiteDatabase db;
	TextView tvNotes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		RelativeLayout ventana = (RelativeLayout) findViewById(R.id.main_activity);
		ventana.setBackgroundColor(Color.BLACK);
		tvNotes = (TextView) findViewById(R.id.tv_notes);
		lvNotes = (ListView) findViewById(R.id.lv_notes);

		Conexion con = new Conexion(getApplicationContext(), "DBNotes.db",
				null, 1);
		db = con.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM notes", null);

		if (c.moveToFirst()) {
			do {
				nota = new Note(c.getShort(0), c.getString(1), c.getString(2));
				list.add(nota);
			} while (c.moveToNext());
		}

		ArrayAdapter<Note> adapt = new ArrayAdapter<Note>(
				getApplicationContext(),
				android.R.layout.simple_expandable_list_item_1, list);
		lvNotes.setAdapter(adapt);

		// ActionBar and back button.
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; goto parent activity.
			this.finish();
			return true;
		case R.id.search:
			// metodoSearch()
			return true;
		case R.id.edit:
			// metodoEdit()
			return true;
		case R.id.delete:
			// metodoDelete()
			return true;
		case R.id.add:
			metodoAdd();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void metodoAdd() {
		// Intent newSerie =new Intent(MainActivity.this, NuevaSerie.class);
		// startActivity(newSerie);
	}
}
