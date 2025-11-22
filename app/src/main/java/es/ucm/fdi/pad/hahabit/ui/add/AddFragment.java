package es.ucm.fdi.pad.hahabit.ui.add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import es.ucm.fdi.pad.hahabit.data.Habit;
import es.ucm.fdi.pad.hahabit.databinding.FragmentAddBinding;

public class AddFragment extends Fragment {

    private EditText titleTxt;

    // botones de area
    private Button cookingBtn;
    private Button sportsBtn;
    private Button studyBtn;
    private Button othersBtn;

    // botones de type
    private Button standardBtn;
    private Button listBtn;
    private Button timerBtn;

    // botones de radio
    private RadioGroup frequencyGroup;

    // tiempo
    private EditText timeTxt;

    // boton de añadir
    private Button addBtn;

    private Habit habit;

    private FragmentAddBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddViewModel addViewModel =
                new ViewModelProvider(this).get(AddViewModel.class);

        binding = FragmentAddBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Ejemplo de acceso a las vistas
        titleTxt = binding.titleText;
        cookingBtn = binding.cookingButton;
        sportsBtn = binding.sportsButton;
        studyBtn = binding.studyButton;
        othersBtn = binding.othersButton;
        standardBtn = binding.standarButton;
        listBtn = binding.listButton;
        timerBtn = binding.timerButton;
        //frequencyGroup = binding.frequencyGroup;
        timeTxt = binding.timeText;

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getArea(){
        cookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                habit.setArea("cooking");
            }
        });
        sportsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                habit.setArea("sports");
            }
        });
        studyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                habit.setArea("study");
            }
        });
    }
}
