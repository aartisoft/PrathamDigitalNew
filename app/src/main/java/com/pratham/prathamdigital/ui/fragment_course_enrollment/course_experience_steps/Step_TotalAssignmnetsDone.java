package com.pratham.prathamdigital.ui.fragment_course_enrollment.course_experience_steps;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.edittexts.IndieEditText;
import com.pratham.prathamdigital.custom.verticalstepperform.Step;

import static com.pratham.prathamdigital.ui.fragment_course_enrollment.Fragment_CourseExperience.sttService;

public class Step_TotalAssignmnetsDone extends Step<String> {
    private String hint;
    private IndieEditText assignDoneEditText;
    private ImageView assign_mic;
    private boolean isChecked = true;

    public Step_TotalAssignmnetsDone(String title, String hint, String nextButtonText) {
        super(title, hint, nextButtonText);
        this.hint = hint;
    }

    @Override
    protected View createStepContentLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View newWordsView = inflater.inflate(R.layout.step_assign_total, null, false);
        assignDoneEditText = newWordsView.findViewById(R.id.et_assign_ttl_desc);
        assign_mic = newWordsView.findViewById(R.id.assign_ttl_mic);
        assignDoneEditText.setHint(hint);
        assign_mic.setOnClickListener(v -> {
            isChecked = !isChecked;
            final int[] stateSet = {android.R.attr.state_checked * (isChecked ? 1 : -1)};
            assign_mic.setImageState(stateSet, true);
            sttService.startListening();
        });
        return newWordsView;
    }

    @Override
    protected void onStepOpened(boolean animated) {

    }

    @Override
    protected void onStepClosed(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {

    }

    @Override
    public String getStepData() {
        Editable text = assignDoneEditText.getText();
        if (text != null) {
            return text.toString();
        }
        return "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        String description = getStepData();
        return description == null || description.isEmpty()
                ? "Field cannot be empty!"
                : description;
    }

    @Override
    public void restoreStepData(String data) {
        if (data == null) {
            isChecked = !isChecked;
            final int[] stateSet = {android.R.attr.state_checked * (isChecked ? 1 : -1)};
            assign_mic.setImageState(stateSet, true);
            return;
        }
        if (assignDoneEditText != null) {
            assignDoneEditText.append(data);
        }
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        return new IsDataValid(true);
    }
}
