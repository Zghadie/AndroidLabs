package com.example.androidlabs;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<TodoItem> todoItems;
    private BaseAdapter todoAdapter;

    private EditText todoEditText;
    private Switch urgentSwitch;
    private Button addButton;
    private ListView todoListView;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;

    private static final String TAG = "TODO_DATABASE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoEditText = findViewById(R.id.todoEditText);
        urgentSwitch = findViewById(R.id.urgentSwitch);
        addButton = findViewById(R.id.addButton);
        todoListView = findViewById(R.id.todoListView);

        todoItems = new ArrayList<>();

        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

        todoAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return todoItems.size();
            }

            @Override
            public Object getItem(int position) {
                return todoItems.get(position);
            }

            @Override
            public long getItemId(int position) {
                return todoItems.get(position).getId();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View rowView = getLayoutInflater().inflate(
                        R.layout.todo_item,
                        parent,
                        false
                );

                TextView todoTextView =
                        rowView.findViewById(R.id.todoItemTextView);

                TodoItem currentItem = todoItems.get(position);
                todoTextView.setText(currentItem.getText());

                if (currentItem.isUrgent()) {
                    rowView.setBackgroundColor(Color.RED);
                    todoTextView.setTextColor(Color.WHITE);
                } else {
                    rowView.setBackgroundColor(Color.WHITE);
                    todoTextView.setTextColor(Color.BLACK);
                }

                return rowView;
            }
        };

        todoListView.setAdapter(todoAdapter);

        Cursor debugCursor = databaseHelper.getAllTodos();
        printCursor(debugCursor);
        debugCursor.close();

        loadTodosFromDatabase();

        addButton.setOnClickListener(v -> {
            String text = todoEditText.getText().toString().trim();
            boolean urgent = urgentSwitch.isChecked();

            if (!text.isEmpty()) {
                long newId = databaseHelper.insertTodo(text, urgent);

                if (newId != -1) {
                    TodoItem newItem = new TodoItem(newId, text, urgent);
                    todoItems.add(newItem);
                    todoAdapter.notifyDataSetChanged();
                }

                todoEditText.setText("");
                urgentSwitch.setChecked(false);
            }
        });

        todoListView.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(MainActivity.this);

            builder.setTitle(getString(R.string.delete_title));
            builder.setMessage(getString(R.string.selected_row) + position);

            builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                TodoItem itemToDelete = todoItems.get(position);

                databaseHelper.deleteTodo(itemToDelete.getId());

                todoItems.remove(position);
                todoAdapter.notifyDataSetChanged();
            });

            builder.setNegativeButton(getString(R.string.no), null);

            builder.create().show();

            return true;
        });
    }

    private void loadTodosFromDatabase() {
        Cursor cursor = databaseHelper.getAllTodos();

        int idColumn = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID);
        int textColumn = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TEXT);
        int urgentColumn =
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_URGENT);

        while (cursor.moveToNext()) {
            long id = cursor.getLong(idColumn);
            String text = cursor.getString(textColumn);
            boolean urgent = cursor.getInt(urgentColumn) == 1;

            todoItems.add(new TodoItem(id, text, urgent));
        }

        cursor.close();
        todoAdapter.notifyDataSetChanged();
    }

    private void printCursor(Cursor c) {
        Log.d(TAG, "Database version: " + db.getVersion());
        Log.d(TAG, "Number of columns: " + c.getColumnCount());

        for (String columnName : c.getColumnNames()) {
            Log.d(TAG, "Column name: " + columnName);
        }

        Log.d(TAG, "Number of results: " + c.getCount());

        while (c.moveToNext()) {
            String row = "";

            for (int i = 0; i < c.getColumnCount(); i++) {
                row += c.getColumnName(i)
                        + "="
                        + c.getString(i)
                        + " ";
            }

            Log.d(TAG, "Row: " + row);
        }

        c.moveToPosition(-1);
    }
}