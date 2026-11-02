package ec.edu.ups.supermaxi.vista;

import ec.edu.ups.supermaxi.controlador.ProveedorControlador;
import ec.edu.ups.supermaxi.dao.IngresoInventarioDAO;
import ec.edu.ups.supermaxi.dao.ProductoDAO;
import ec.edu.ups.supermaxi.dao.ProveedorDAO;
import ec.edu.ups.supermaxi.modelo.IngresoInventario;
import ec.edu.ups.supermaxi.modelo.Producto;
import ec.edu.ups.supermaxi.modelo.Proveedor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {

    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(Main::iniciarAplicacion);
    }

    private static void iniciarAplicacion() {
        try {
            // Inicializar directamente la Unidad de Persistencia sin depender de JPAUtil
            emf = Persistence.createEntityManagerFactory("SupermaxiPU");
            em = emf.createEntityManager();

            ProveedorDAO proveedorDAO = new ProveedorDAO(em);
            ProductoDAO productoDAO = new ProductoDAO(em);
            IngresoInventarioDAO ingresoDAO = new IngresoInventarioDAO(em);

            ProveedorControlador proveedorControlador = new ProveedorControlador(proveedorDAO);

            cargarDatosSemilla(proveedorControlador, productoDAO, ingresoDAO);

            VentanaProveedor ventana = crearVentana(proveedorControlador);
            ventana.setVisible(true);

        } catch (Exception e) {
            System.err.println("Error al iniciar JPA: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Error al conectar con la Unidad de Persistencia JPA:\n" + e.getMessage(),
                    "Error de Inicialización",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static VentanaProveedor crearVentana(ProveedorControlador controlador) {
        VentanaProveedor ventana = new VentanaProveedor(controlador);
        ventana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarConexionJPA();
            }
        });
        return ventana;
    }

    private static void cerrarConexionJPA() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    private static void cargarDatosSemilla(ProveedorControlador controlador,
                                           ProductoDAO productoDAO,
                                           IngresoInventarioDAO ingresoDAO) {
        if (controlador.listarProveedoresActivos().isEmpty()) {
            controlador.registrarProveedor(
                    "1790012345001",
                    "PRONACA C.A.",
                    "Cárnicos, Aves y Embutidos",
                    "022999000",
                    "ventas@pronaca.com",
                    "Av. De los Shyris N36-120, Quito"
            );

            controlador.registrarProveedor(
                    "1790054321001",
                    "FLORALP S.A.",
                    "Lácteos y Derivados",
                    "062987654",
                    "pedidos@floralp.com.ec",
                    "Panamericana Norte Km 2, Ibarra"
            );

            Proveedor floralp = controlador.obtenerPorRuc("1790054321001");
            if (floralp != null) {
                Producto queso = new Producto(
                        "7861000123456",
                        "Queso Mozzarella Floralp 500g",
                        4.85,
                        20,
                        floralp
                );
                productoDAO.crear(queso);

                IngresoInventario ingreso = new IngresoInventario(
                        "GUIA-001-002-000154",
                        50,
                        3.40,
                        "Entrega refrigerada en bodega central Supermaxi",
                        queso,
                        floralp
                );
                ingresoDAO.crear(ingreso);
            }
        }
    }
}