package es.ucm.fdi.pad.hahabit.ui.add;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Map;

import es.ucm.fdi.pad.hahabit.R;
import es.ucm.fdi.pad.hahabit.data.Habit;
import es.ucm.fdi.pad.hahabit.databinding.FragmentAddBinding;

public class AddFragment extends Fragment {

    private FragmentAddBinding binding;
    private AddViewModel addViewModel;

    //botones con el día de la semana
    private Map<MaterialButton, Integer> weekDayBtns = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        addViewModel =
                new ViewModelProvider(this).get(AddViewModel.class);
        binding = FragmentAddBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        inicializarBotonesSemanales();
        configurarListeners();

        //esto de aqui es para que el boton del radioGroup cuente como marcado al iniciar
        binding.frequencyGroup.check(R.id.weeklyButton);
        addViewModel.setTypeFrequency(0);

        return root;
    }

    private void inicializarBotonesSemanales() {
        weekDayBtns.put(binding.btnLunes, 1);
        weekDayBtns.put(binding.btnMartes, 2);
        weekDayBtns.put(binding.btnMiercoles, 3);
        weekDayBtns.put(binding.btnJueves, 4);
        weekDayBtns.put(binding.btnViernes, 5);
        weekDayBtns.put(binding.btnSabado, 6);
        weekDayBtns.put(binding.btnDomingo, 7);
    }

    private void configurarListeners() {

        // Area
        binding.cookingButton.setOnClickListener(v ->
                {addViewModel.setArea("Cocinar");
                    marcarBotonArea(binding.cookingButton, 0xFF78BC61);});
        binding.sportsButton.setOnClickListener(v ->
                {addViewModel.setArea("Deporte");
                    marcarBotonArea(binding.sportsButton, 0xFFFF686B);});
        binding.studyButton.setOnClickListener(v ->
                {addViewModel.setArea("Estudio");
                    marcarBotonArea(binding.studyButton, 0xFF00A6FB);});
        binding.othersButton.setOnClickListener(v ->
                {addViewModel.setArea("Otros");
                    marcarBotonArea(binding.othersButton, 0xFFF8BD4F);});

        // Tipo
        binding.standarButton.setOnClickListener(v ->
                {addViewModel.setType("Estandar");
                    marcarBotonTipo(binding.standarButton);});
        binding.listButton.setOnClickListener(v ->
                {addViewModel.setType("Lista");
                    marcarBotonTipo(binding.listButton);});
        binding.timerButton.setOnClickListener(v ->
                {addViewModel.setType("Temporizador");
                    marcarBotonTipo(binding.timerButton);});

        // Frecuncia semanal o intervalo
        binding.frequencyGroup.setOnCheckedChangeListener((group, checkedId) -> {

            // Por dias de la semana
                if (checkedId == R.id.weeklyButton) {

                    // Se muestra la vista
                    binding.layoutDiasSemana.setVisibility(View.VISIBLE);
                    binding.layoutIntervalo.setVisibility(View.GONE);

                    // Se guarda el tipo de frecuencia
                    addViewModel.setTypeFrequency(0);
                }

                // Por intervalo de dias
                else if (checkedId == R.id.daysButton) {

                    // Se muestra la vista
                    binding.layoutDiasSemana.setVisibility(View.GONE);
                    binding.layoutIntervalo.setVisibility(View.VISIBLE);

                    // Se guarda el tipo de fecuencia
                    addViewModel.setTypeFrequency(1);
                }
        });

        // Botones de los días de la semana
        for (MaterialButton btn : weekDayBtns.keySet()) {
            btn.setOnClickListener(v -> toggleBotonDia(btn));
        }

        // Boton de añadir habito
        binding.addButton.setOnClickListener(v -> {
            setStrings();
            if (validate()) {

                // Se guarda
                Habit habit = addViewModel.buildHabit();

                System.out.println("Hábito creado: " + habit.getTitle() + " - " + habit.getDaysFrequency());
                addViewModel.insertHabit(habit);



                Toast.makeText(requireContext(), "Hábito creado correctamente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //  Seleccionar/deseleccionar dia de la semana y avisar al ViewModel

    // Metodo para marcar el boton de area seleccionado
    private void marcarBotonArea(MaterialButton seleccionado, int colorOriginal) {

        binding.cookingButton.setBackgroundTintList(ColorStateList.valueOf(0xFF78BC61));
        binding.sportsButton.setBackgroundTintList(ColorStateList.valueOf(0xFFFF686B));
        binding.studyButton.setBackgroundTintList(ColorStateList.valueOf(0xFF00A6FB));
        binding.othersButton.setBackgroundTintList(ColorStateList.valueOf(0xFFF8BD4F));

        // Oscurecer el seleccionado
        seleccionado.setBackgroundTintList(ColorStateList.valueOf(oscurecerColor(colorOriginal)));
    }

    // Marcar botón de tipo seleccionado
    private void marcarBotonTipo(MaterialButton seleccionado) {

        binding.standarButton.setBackgroundTintList(ColorStateList.valueOf(0xFFFFFFFF));
        binding.standarButton.setIconTint(ColorStateList.valueOf(0xFF333333));
        binding.listButton.setBackgroundTintList(ColorStateList.valueOf(0xFFFFFFFF));
        binding.listButton.setIconTint(ColorStateList.valueOf(0xFF333333));
        binding.timerButton.setBackgroundTintList(ColorStateList.valueOf(0xFFFFFFFF));
        binding.timerButton.setIconTint(ColorStateList.valueOf(0xFF333333));


        seleccionado.setBackgroundTintList(ColorStateList.valueOf(0xFF00A6FB));
        seleccionado.setIconTint(ColorStateList.valueOf(0xFFFFFFFF));
    }

    // Metodo para oscurecer el color de los botones
    private int oscurecerColor(int color) {
        float factor = 0.5f;
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * factor);
        int g = (int) (((color >> 8) & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    private void toggleBotonDia(MaterialButton btn) {

        // Se obtine a que día pertenece el boton
        int dia = weekDayBtns.get(btn);

        // se guarda en el model
        addViewModel.setWeekDays(dia);

        // Model devuelve la lista de días de la semana seleccionados
        boolean seleccionado = addViewModel.getSelectedWeekDays().getValue().contains(dia);

        // Cambia de color dependiendo de si está seleccionado
        if (seleccionado) {
            btn.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), android.R.color.holo_blue_light)
            ));
        } else {
            btn.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
            ));
        }
    }

    private void setStrings(){

        // Titulo
        addViewModel.setTitle(binding.titleText.getText().toString());

        // Tiempo
        /*String timeText = binding.timeText.getText().toString(); // "14:30"
        String[] strings = timeText.split(":");
        int hour = Integer.parseInt(strings[0]);
        int minute = Integer.parseInt(strings[1]);
        addViewModel.setTime(hour, minute);*/

        String intervalText = binding.editTextDias.getText().toString().trim();
        int interval = 0; // valor por defecto

        if (!intervalText.isEmpty()) {
            try {
                interval = Integer.parseInt(intervalText);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Introduce un número válido", Toast.LENGTH_SHORT).show();
                return; // salimos de la función para no continuar con datos inválidos
            }
        }

        // ahora sí podemos pasarlo al ViewModel
        addViewModel.setIntervalDays(interval);
    }

    private boolean validate() {
        // Titulo
        String title = addViewModel.getTitle().getValue();
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, añada un título", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Área
        String area = addViewModel.getArea().getValue();
        if (area == null || area.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, seleccione un área", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Tipo
        String type = addViewModel.getType().getValue();
        if (type == null || type.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, seleccione un tipo", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Frecuencia
        Integer typeFreq = addViewModel.getTypeFrequency().getValue();
        if (typeFreq == null) {
            Toast.makeText(requireContext(), "Por favor, seleccione una frecuencia", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Si es por intervalo de días
        if (typeFreq == 1) {
            Integer interval = addViewModel.getIntervalDays().getValue();
            if (interval == null) {
                Toast.makeText(requireContext(), "Por favor, añada un intérvalo de días", Toast.LENGTH_SHORT).show();
                return false;
            }
            else if(interval < 0){
                Toast.makeText(requireContext(), "Por favor, añada un intérvalo de días positivo", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // Hora
        /*String time = binding.timeText.getText().toString().trim();
        if (time.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, añada una hora", Toast.LENGTH_SHORT).show();
            return false;
        }*/

        return true;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
