package es.ucm.fdi.pad.hahabit.ui.add;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.pad.hahabit.R;
import es.ucm.fdi.pad.hahabit.databinding.FragmentAddBinding;

public class AddFragment extends Fragment {

    private RadioGroup frequencyGroup;
    private LinearLayout layoutDiasSemana;
    private LinearLayout layoutIntervalo;
    private EditText editTextDias;

    // Botones de días de la semana
    private MaterialButton btnLunes, btnMartes, btnMiercoles, btnJueves;
    private MaterialButton btnViernes, btnSabado, btnDomingo;
    private List<MaterialButton> botonesSeleccionados;
    private FragmentAddBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddViewModel addViewModel =
                new ViewModelProvider(this).get(AddViewModel.class);

        binding = FragmentAddBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicializar la lista de botones seleccionados
        botonesSeleccionados = new ArrayList<>();

        // Inicializar vistas
        inicializarVistas();

        // Configurar listeners
        configurarListeners();

        final TextView textView = binding.textAdd;
        addViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }
    private void inicializarVistas() {
        // Usando binding para acceder a las vistas
        frequencyGroup = binding.frequencyGroup;
        layoutDiasSemana = binding.layoutDiasSemana;
        layoutIntervalo = binding.layoutIntervalo;
        editTextDias = binding.editTextDias;

        // Inicializar botones de días
        btnLunes = binding.btnLunes;
        btnMartes = binding.btnMartes;
        btnMiercoles = binding.btnMiercoles;
        btnJueves = binding.btnJueves;
        btnViernes = binding.btnViernes;
        btnSabado = binding.btnSabado;
        btnDomingo = binding.btnDomingo;
    }

    private void configurarListeners() {
        // Listener para cambiar entre vistas según el RadioButton seleccionado
        frequencyGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.weeklyButton) {
                    // Mostrar vista de días de la semana
                    layoutDiasSemana.setVisibility(View.VISIBLE);
                    layoutIntervalo.setVisibility(View.GONE);
                } else if (checkedId == R.id.daysButton) {
                    // Mostrar vista de intervalo de días
                    layoutDiasSemana.setVisibility(View.GONE);
                    layoutIntervalo.setVisibility(View.VISIBLE);
                }
            }
        });

        // Configurar listeners para los botones de días de la semana
        configurarBotonDia(btnLunes, "Lunes");
        configurarBotonDia(btnMartes, "Martes");
        configurarBotonDia(btnMiercoles, "Miércoles");
        configurarBotonDia(btnJueves, "Jueves");
        configurarBotonDia(btnViernes, "Viernes");
        configurarBotonDia(btnSabado, "Sábado");
        configurarBotonDia(btnDomingo, "Domingo");
    }

    private void configurarBotonDia(final MaterialButton boton, final String nombreDia) {
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (botonesSeleccionados.contains(boton)) {
                    // Deseleccionar - volver al color gris
                    botonesSeleccionados.remove(boton);
                    boton.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
                    ));
                } else {
                    // Seleccionar - cambiar a azul
                    botonesSeleccionados.add(boton);
                    boton.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), android.R.color.holo_blue_light)
                    ));
                }
            }
        });
    }

    // Método para obtener la configuración de frecuencia seleccionada
    public String obtenerConfiguracionFrecuencia() {
        int checkedId = frequencyGroup.getCheckedRadioButtonId();

        if (checkedId == R.id.weeklyButton) {
            // Días de la semana seleccionados
            if (botonesSeleccionados.isEmpty()) {
                return "No se han seleccionado días";
            }
            StringBuilder dias = new StringBuilder("Días: ");
            for (MaterialButton btn : botonesSeleccionados) {
                dias.append(btn.getText()).append(" ");
            }
            return dias.toString();
        } else if (checkedId == R.id.daysButton) {
            // Intervalo de días
            String numDias = editTextDias.getText().toString();
            if (numDias.isEmpty()) {
                return "No se ha ingresado un intervalo";
            }
            return "Cada " + numDias + " días";
        }

        return "No se ha seleccionado frecuencia";
    }

    // Método para obtener lista de días seleccionados (útil para guardar en BD)
    public List<String> obtenerDiasSeleccionados() {
        List<String> dias = new ArrayList<>();
        for (MaterialButton btn : botonesSeleccionados) {
            dias.add(btn.getText().toString());
        }
        return dias;
    }

    // Método para obtener el intervalo de días (útil para guardar en BD)
    public int obtenerIntervaloDias() {
        String texto = editTextDias.getText().toString();
        if (!texto.isEmpty()) {
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
