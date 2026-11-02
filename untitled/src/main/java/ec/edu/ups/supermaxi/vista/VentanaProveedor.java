package ec.edu.ups.supermaxi.vista;

import ec.edu.ups.supermaxi.controlador.ProveedorControlador;
import ec.edu.ups.supermaxi.modelo.Proveedor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class VentanaProveedor extends JFrame {

    private final ProveedorControlador controlador;

    // Campos del formulario
    private JTextField txtId;
    private JTextField txtRuc;
    private JTextField txtRazonSocial;
    private JComboBox<String> cbxCategoria;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JTextField txtDireccion;
    private JCheckBox chkActivo;
    private JTextField txtBuscarRuc;

    // Tabla de datos
    private JTable tablaProveedores;
    private DefaultTableModel modeloTabla;

    public VentanaProveedor(ProveedorControlador controlador) {
        this.controlador = controlador;
        inicializarComponentes();
        cargarTablaProveedores();
    }

    private void inicializarComponentes() {
        setTitle("Supermaxi - Gestión de Proveedores (CRUD JPA)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 640);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(panelPrincipal);

        // =====================================================================
        // 1. PANEL SUPERIOR: FORMULARIO DE REGISTRO Y EDICIÓN
        // =====================================================================
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(new TitledBorder("Datos del Proveedor - Catálogo Supermaxi"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(10);
        txtId.setEditable(false);
        txtId.setBackground(new Color(230, 230, 230));

        txtRuc = new JTextField(15);
        txtRazonSocial = new JTextField(20);

        cbxCategoria = new JComboBox<>(new String[]{
                "Lácteos y Derivados",
                "Cárnicos, Aves y Embutidos",
                "Abarrotes y Despensa",
                "Frutas, Verduras y Legumbres",
                "Bebidas y Licores",
                "Limpieza y Hogar",
                "Cuidado Personal",
                "Panadería y Pastelería"
        });

        txtTelefono = new JTextField(15);
        txtCorreo = new JTextField(20);
        txtDireccion = new JTextField(25);
        chkActivo = new JCheckBox("Proveedor Activo", true);

        // Fila 0: ID y RUC
        agregarCampo(panelFormulario, gbc, 0, 0, "ID:", txtId);
        agregarCampo(panelFormulario, gbc, 2, 0, "RUC (13 dígitos):", txtRuc);

        // Fila 1: Razón Social y Categoría
        agregarCampo(panelFormulario, gbc, 0, 1, "Razón Social:", txtRazonSocial);
        agregarCampo(panelFormulario, gbc, 2, 1, "Categoría Suministro:", cbxCategoria);

        // Fila 2: Teléfono y Correo
        agregarCampo(panelFormulario, gbc, 0, 2, "Teléfono:", txtTelefono);
        agregarCampo(panelFormulario, gbc, 2, 2, "Correo Electrónico:", txtCorreo);

        // Fila 3: Dirección y Estado
        agregarCampo(panelFormulario, gbc, 0, 3, "Dirección Bodega:", txtDireccion);
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        panelFormulario.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.4;
        panelFormulario.add(chkActivo, gbc);

        // =====================================================================
        // 2. PANEL DE BOTONES (CREATE, UPDATE, DELETE, LIMPIAR)
        // =====================================================================
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));

        JButton btnRegistrar = new JButton("Registrar (Create)");
        JButton btnActualizar = new JButton("Actualizar (Update)");
        JButton btnBajaLogica = new JButton("Desactivar (Baja Lógica)");
        JButton btnEliminar = new JButton("Eliminar BD (Delete)");
        JButton btnLimpiar = new JButton("Limpiar Formulario");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnBajaLogica);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel panelNorte = new JPanel(new BorderLayout(5, 5));
        panelNorte.add(panelFormulario, BorderLayout.CENTER);
        panelNorte.add(panelBotones, BorderLayout.SOUTH);
        panelPrincipal.add(panelNorte, BorderLayout.NORTH);

        // =====================================================================
        // 3. PANEL CENTRAL: BUSCADOR POR RUC Y TABLA DE PROVEEDORES (READ)
        // =====================================================================
        JPanel panelCentro = new JPanel(new BorderLayout(5, 5));
        panelCentro.setBorder(new TitledBorder("Listado de Proveedores en Base de Datos"));

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtBuscarRuc = new JTextField(15);
        JButton btnBuscarRuc = new JButton("Buscar por RUC");
        JButton btnListarActivos = new JButton("Mostrar Todos los Activos");

        panelBusqueda.add(new JLabel("Filtrar por RUC:"));
        panelBusqueda.add(txtBuscarRuc);
        panelBusqueda.add(btnBuscarRuc);
        panelBusqueda.add(btnListarActivos);
        panelCentro.add(panelBusqueda, BorderLayout.NORTH);

        String[] columnas = {"ID", "RUC", "Razón Social", "Categoría", "Teléfono", "Correo", "Dirección", "Activo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProveedores = new JTable(modeloTabla);
        tablaProveedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProveedores.setRowHeight(24);
        panelCentro.add(new JScrollPane(tablaProveedores), BorderLayout.CENTER);

        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        // =====================================================================
        // 4. ASIGNACIÓN DE EVENTOS
        // =====================================================================
        tablaProveedores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarProveedorSeleccionado();
            }
        });

        btnRegistrar.addActionListener(e -> accionRegistrar());
        btnActualizar.addActionListener(e -> accionActualizar());
        btnBajaLogica.addActionListener(e -> accionDesactivar());
        btnEliminar.addActionListener(e -> accionEliminarFisico());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnBuscarRuc.addActionListener(e -> accionBuscarPorRuc());
        btnListarActivos.addActionListener(e -> cargarTablaProveedores());
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int col, int fila, String texto, JComponent componente) {
        gbc.gridx = col;
        gbc.gridy = fila;
        gbc.weightx = 0.1;
        panel.add(new JLabel(texto), gbc);

        gbc.gridx = col + 1;
        gbc.weightx = 0.4;
        panel.add(componente, gbc);
    }

    // =========================================================================
    // MÉTODOS QUE INVOCAN AL CONTROLADOR
    // =========================================================================

    private void accionRegistrar() {
        try {
            controlador.registrarProveedor(
                    txtRuc.getText(),
                    txtRazonSocial.getText(),
                    cbxCategoria.getSelectedItem().toString(),
                    txtTelefono.getText(),
                    txtCorreo.getText(),
                    txtDireccion.getText()
            );
            JOptionPane.showMessageDialog(this,
                    "Proveedor registrado correctamente en el catálogo de Supermaxi.",
                    "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            cargarTablaProveedores();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error al Registrar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionActualizar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione primero un proveedor de la tabla para poder actualizarlo.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Long id = Long.parseLong(txtId.getText());
            controlador.actualizarProveedor(
                    id,
                    txtRuc.getText(),
                    txtRazonSocial.getText(),
                    cbxCategoria.getSelectedItem().toString(),
                    txtTelefono.getText(),
                    txtCorreo.getText(),
                    txtDireccion.getText(),
                    chkActivo.isSelected()
            );
            JOptionPane.showMessageDialog(this,
                    "Proveedor actualizado exitosamente.",
                    "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            cargarTablaProveedores();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error al Actualizar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionDesactivar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un proveedor de la tabla para desactivarlo.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Desea dar de baja lógica a este proveedor?",
                "Confirmar Desactivación", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                Long id = Long.parseLong(txtId.getText());
                controlador.desactivarProveedor(id);
                JOptionPane.showMessageDialog(this,
                        "El proveedor ha sido desactivado.",
                        "Baja Lógica", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarTablaProveedores();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void accionEliminarFisico() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un proveedor de la tabla para eliminarlo de la base de datos.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar físicamente este proveedor de la base de datos?\n" +
                        "(Se eliminarán también sus productos e ingresos asociados en cascada)",
                "Confirmar Eliminación Física", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                Long id = Long.parseLong(txtId.getText());
                controlador.eliminarProveedorFisico(id);
                JOptionPane.showMessageDialog(this,
                        "Proveedor eliminado definitivamente de la base de datos.",
                        "Eliminado", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarTablaProveedores();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error al Eliminar", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void accionBuscarPorRuc() {
        try {
            Proveedor p = controlador.obtenerPorRuc(txtBuscarRuc.getText());
            modeloTabla.setRowCount(0);
            if (p != null) {
                agregarFila(p);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se encontró ningún proveedor con el RUC: " + txtBuscarRuc.getText(),
                        "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error de Búsqueda", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarTablaProveedores() {
        modeloTabla.setRowCount(0);
        List<Proveedor> proveedores = controlador.listarProveedoresActivos();
        for (Proveedor p : proveedores) {
            agregarFila(p);
        }
    }

    private void agregarFila(Proveedor p) {
        modeloTabla.addRow(new Object[]{
                p.getId(),
                p.getRuc(),
                p.getRazonSocial(),
                p.getCategoriaSuministro(),
                p.getTelefono(),
                p.getCorreo(),
                p.getDireccion(),
                p.isActivo() ? "Sí" : "No"
        });
    }

    private void cargarProveedorSeleccionado() {
        int fila = tablaProveedores.getSelectedRow();
        if (fila >= 0) {
            txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
            txtRuc.setText(modeloTabla.getValueAt(fila, 1).toString());
            txtRazonSocial.setText(modeloTabla.getValueAt(fila, 2).toString());
            cbxCategoria.setSelectedItem(modeloTabla.getValueAt(fila, 3).toString());
            txtTelefono.setText(modeloTabla.getValueAt(fila, 4).toString());
            txtCorreo.setText(modeloTabla.getValueAt(fila, 5).toString());
            txtDireccion.setText(modeloTabla.getValueAt(fila, 6).toString());
            chkActivo.setSelected("Sí".equals(modeloTabla.getValueAt(fila, 7).toString()));
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtRuc.setText("");
        txtRazonSocial.setText("");
        cbxCategoria.setSelectedIndex(0);
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtDireccion.setText("");
        chkActivo.setSelected(true);
        txtBuscarRuc.setText("");
        tablaProveedores.clearSelection();
    }
}