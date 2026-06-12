package com.example.androidlabs;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoEditText = findViewById(R.id.todoEditText);
        urgentSwitch = findViewById(R.id.urgentSwitch);
        addButton = findViewById(R.id.addButton);
        todoListView = findViewById(R.id.todoListView);

        todoItems = new ArrayList<>();

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
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View rowView = getLayoutInflater().inflate(R.layout.todo_item, parent, false);

                TextView todoTextView = rowView.findViewById(R.id.todoItemTextView);

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

        addButton.setOnClickListener(v -> {
            String text = todoEditText.getText().toString();
            boolean urgent = urgentSwitch.isChecked();

            TodoItem newItem = new TodoItem(text, urgent);
            todoItems.add(newItem);

            todoEditText.setText("");
            urgentSwitch.setChecked(false);

            todoAdapter.notifyDataSetChanged();
        });

        todoListView.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle(getString(R.string.delete_title));
            builder.setMessage(getString(R.string.selected_row) + position);

            builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                todoItems.remove(position);
                todoAdapter.notifyDataSetChanged();
            });

            builder.setNegativeButton(getString(R.string.no), null);

            builder.create().show();

            return true;
        });
    }
}