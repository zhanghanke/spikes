package com.novoda.todoapp.task.edit.view;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.novoda.data.SyncedData;
import com.novoda.notils.caster.Views;
import com.novoda.todoapp.R;
import com.novoda.todoapp.task.data.model.Task;
import com.novoda.todoapp.task.edit.displayer.TaskEditActionListener;
import com.novoda.todoapp.task.edit.displayer.TaskEditDisplayer;

public class TaskEditView extends LinearLayout implements TaskEditDisplayer {

    private TextView titleView;
    private TextView descriptionView;
    private Button saveButton; //TODO replace by FAB

    public TaskEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_task_edit_view, this);
        titleView = Views.findById(this, R.id.edit_task_title);
        descriptionView = Views.findById(this, R.id.edit_task_description);
        saveButton = Views.findById(this, R.id.edit_save_button);
    }

    @Override
    public void attach(final TaskEditActionListener taskActionListener) {
        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                taskActionListener.save(getTitle(), getDescription());
            }
        });
    }

    @Override
    public void detach(TaskEditActionListener taskActionListener) {
        saveButton.setOnClickListener(null);
    }

    @Override
    public void display(SyncedData<Task> syncedData) {
        final Task task = syncedData.data();
        titleView.setText(task.title().orNull());
        descriptionView.setText(task.description().orNull());
    }

    private Optional<String> getTitle() {
        return getText(titleView);
    }

    private Optional<String> getDescription() {
        return getText(descriptionView);
    }

    private Optional<String> getText(TextView editText) {
        if (titleView.getText().toString().isEmpty()) {
            return Optional.absent();
        } else {
            return Optional.of(editText.getText().toString());
        }
    }

    @Override
    public void showEmptyTaskError() {
        Snackbar.make(this, R.string.empty_task_message, Snackbar.LENGTH_LONG).show(); //TODO maybe this should be extracted
    }

}
