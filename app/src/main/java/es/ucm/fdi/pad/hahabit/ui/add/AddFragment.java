package es.ucm.fdi.pad.hahabit.ui.add;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import es.ucm.fdi.pad.hahabit.notifications.HabitReminderManager;

public class AddFragment extends Fragment {

    private FragmentAddBinding binding;
    private AddViewModel addViewModel;

    //botones con el día de la semana
    private Map<MaterialButton, Integer> weekDayBtns = new HashMap<>();

    // Launcher para solicitar permisos de notificación
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Habit pendingHabit = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        addViewModel =
                new ViewModelProvider(this).get(AddViewModel.class);
        binding = FragmentAddBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicializar el launcher para permisos
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted && pendingHabit != null) {
                        // Permiso concedido, programar recordatorio
                        HabitReminderManager reminderManager = new HabitReminderManager(requireContext());
                        reminderManager.scheduleReminder(pendingHabit);
                        pendingHabit = null;
                    } else if (!isGranted) {
                        Toast.makeText(requireContext(), "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show();
                    }
                }
        );

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
    private void resetUI() { //clase para resetear la vista y que se borren los valores al crear un habito
        // Título
        binding.titleText.setText("");

        // Área (colores por defecto)
        binding.cookingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.verde_cocinar)));
        binding.sportsButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.rojo_deporte)));
        binding.studyButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.azul_estudio)));
        binding.othersButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.amarillo_otros)));

        // Tipo (reset)
        binding.standarButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.surface)));
        binding.standarButton.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_primary)));
        binding.listButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.surface)));
        binding.listButton.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_primary)));
        binding.timerButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.surface)));
        binding.timerButton.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_primary)));

        //aqui reseteo las horas
        binding.layoutTimer.setVisibility(View.GONE);
        binding.editTextHours.setText("");
        binding.editTextMinutes.setText("");
        binding.editTextSeconds.setText("");
        // Frecuencia (por defecto semanal)
        binding.frequencyGroup.check(R.id.weeklyButton);

        // Días de la semana
        for (MaterialButton btn : weekDayBtns.keySet()) {
            btn.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.surface)
            ));
        }

        // Intervalo
        binding.editTextDias.setText("");

        // Recordatorio
        binding.reminderCheckbox.setChecked(false);
        binding.layoutReminder.setVisibility(View.GONE);
        binding.timePicker.setHour(9);
        binding.timePicker.setMinute(0);
    }
    private void configurarListeners() {

        // Area
        binding.cookingButton.setOnClickListener(v ->
                {addViewModel.setArea("Cocinar");
                    marcarBotonArea(binding.cookingButton, ContextCompat.getColor(requireContext(), R.color.verde_cocinar));});
        binding.sportsButton.setOnClickListener(v ->
                {addViewModel.setArea("Deporte");
                    marcarBotonArea(binding.sportsButton, ContextCompat.getColor(requireContext(), R.color.rojo_deporte));});
        binding.studyButton.setOnClickListener(v ->
                {addViewModel.setArea("Estudio");
                    marcarBotonArea(binding.studyButton, ContextCompat.getColor(requireContext(), R.color.azul_estudio));});
        binding.othersButton.setOnClickListener(v ->
                {addViewModel.setArea("Otros");
                    marcarBotonArea(binding.othersButton, ContextCompat.getColor(requireContext(), R.color.amarillo_otros));});

        // Tipo
        binding.standarButton.setOnClickListener(v -> {
            addViewModel.setType("normal");
            marcarBotonTipo(binding.standarButton);
            // Ocultar el layout del temporizador
            binding.layoutTimer.setVisibility(View.GONE);
        });

        binding.listButton.setOnClickListener(v -> {
            addViewModel.setType("list");
            marcarBotonTipo(binding.listButton);
            // Ocultar el layout del temporizador
            binding.layoutTimer.setVisibility(View.GONE);
        });

        binding.timerButton.setOnClickListener(v -> {
            addViewModel.setType("timer");
            marcarBotonTipo(binding.timerButton);
            // Mostrar el layout del temporizador
            binding.layoutTimer.setVisibility(View.VISIBLE);
        });



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

        // Checkbox de recordatorio
        binding.reminderCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            addViewModel.setReminderEnabled(isChecked);
            if (isChecked) {
                binding.layoutReminder.setVisibility(View.VISIBLE);
            } else {
                binding.layoutReminder.setVisibility(View.GONE);
            }
        });

        // Boton de añadir habito
        binding.addButton.setOnClickListener(v -> {
            setStrings();
            if (validate()) {

                // Se guarda
                Habit habit = addViewModel.buildHabit();

                System.out.println("Hábito creado: " + habit.getTitle() + " - " + habit.getDaysFrequency());
                addViewModel.insertHabit(habit);

                // Programar notificación si está habilitada
                if (habit.isReminderEnabled()) {
                    scheduleReminderWithPermission(habit);
                }

                Toast.makeText(requireContext(), "Hábito creado correctamente", Toast.LENGTH_SHORT).show();

                addViewModel.reset();
                resetUI();
            }
        });
    }

    //  Seleccionar/deseleccionar dia de la semana y avisar al ViewModel

    // Metodo para marcar el boton de area seleccionado
    private void marcarBotonArea(MaterialButton seleccionado, int colorOriginal) {

        binding.cookingButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.verde_cocinar)));
        binding.sportsButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.rojo_deporte)));
        binding.studyButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.azul_estudio)));
        binding.othersButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.amarillo_otros)));

        // Oscurecer el seleccionado
        seleccionado.setBackgroundTintList(ColorStateList.valueOf(oscurecerColor(colorOriginal)));
    }

    // Marcar botón de tipo seleccionado
    private void marcarBotonTipo(MaterialButton seleccionado) {

        binding.standarButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.surface)));
        binding.standarButton.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_primary)));
        binding.listButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.surface)));
        binding.listButton.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_primary)));
        binding.timerButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.surface)));
        binding.timerButton.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_primary)));


        seleccionado.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_primary)));
        seleccionado.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_on_color)));
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
                    ContextCompat.getColor(requireContext(), R.color.color_primary)
            ));
        } else {
            btn.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.surface)
            ));
        }
    }

    private void setStrings(){

        // Titulo
        addViewModel.setTitle(binding.titleText.getText().toString());

        // Intervalo de días
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

        // Tiempo del temporizador
        String hoursText = binding.editTextHours.getText().toString().trim();
        String minutesText = binding.editTextMinutes.getText().toString().trim();
        String secondsText = binding.editTextSeconds.getText().toString().trim();

        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        if (!hoursText.isEmpty()) {
            try {
                hours = Integer.parseInt(hoursText);
            } catch (NumberFormatException e) {
                // Mantener en 0 si no es válido
            }
        }

        if (!minutesText.isEmpty()) {
            try {
                minutes = Integer.parseInt(minutesText);
            } catch (NumberFormatException e) {
                // Mantener en 0 si no es válido
            }
        }

        if (!secondsText.isEmpty()) {
            try {
                seconds = Integer.parseInt(secondsText);
            } catch (NumberFormatException e) {
                // Mantener en 0 si no es válido
            }
        }

        addViewModel.setTimerHours(hours);
        addViewModel.setTimerMinutes(minutes);
        addViewModel.setTimerSeconds(seconds);

        // Hora del recordatorio
        if (binding.reminderCheckbox.isChecked()) {
            int hour = binding.timePicker.getHour();
            int minute = binding.timePicker.getMinute();
            String timeText = String.format("%02d:%02d", hour, minute);
            addViewModel.setReminderTime(timeText);
        }
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

        // Si es tipo temporizador, validar que se haya configurado el tiempo
        if (type.equals("timer")) {
            Integer hours = addViewModel.getTimerHours().getValue();
            Integer minutes = addViewModel.getTimerMinutes().getValue();
            Integer seconds = addViewModel.getTimerSeconds().getValue();

            if ((hours == null || hours == 0) && (minutes == null || minutes == 0) && (seconds == null || seconds == 0)) {
                Toast.makeText(requireContext(), "Por favor, configure el tiempo del temporizador", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Validar que los minutos y segundos no sean mayores a 59
            if (minutes != null && minutes > 59) {
                Toast.makeText(requireContext(), "Los minutos no pueden ser mayores a 59", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (seconds != null && seconds > 59) {
                Toast.makeText(requireContext(), "Los segundos no pueden ser mayores a 59", Toast.LENGTH_SHORT).show();
                return false;
            }
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

        // No necesitamos validar la hora del recordatorio porque el TimePicker siempre tiene un valor válido

        return true;
    }


    private void scheduleReminderWithPermission(Habit habit) {
        // Para Android 13+ (API 33+), necesitamos solicitar permiso POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permiso ya concedido
                HabitReminderManager reminderManager = new HabitReminderManager(requireContext());
                reminderManager.scheduleReminder(habit);
            } else {
                // Solicitar permiso
                pendingHabit = habit;
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            // Para versiones anteriores a Android 13, no se necesita permiso runtime
            HabitReminderManager reminderManager = new HabitReminderManager(requireContext());
            reminderManager.scheduleReminder(habit);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
