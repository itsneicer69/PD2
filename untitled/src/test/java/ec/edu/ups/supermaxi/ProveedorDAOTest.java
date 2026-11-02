package ec.edu.ups.supermaxi;

import ec.edu.ups.supermaxi.dao.ProveedorDAO;
import ec.edu.ups.supermaxi.modelo.Proveedor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProveedorDAOTest {

    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static ProveedorDAO proveedorDAO;
    private static Long idProveedorPrueba;
    private static final String RUC_PRUEBA = "1799988877001";

    @BeforeAll
    public static void setUpClass() {
        // Inicializa directamente sin depender de JPAUtil
        emf = Persistence.createEntityManagerFactory("SupermaxiPU");
        em = emf.createEntityManager();
        proveedorDAO = new ProveedorDAO(em);

        // Limpiar el proveedor de prueba si quedó de una ejecución anterior
        Proveedor previo = proveedorDAO.buscarPorRuc(RUC_PRUEBA);
        if (previo != null) {
            proveedorDAO.eliminar(previo.getId());
        }
    }

    @AfterAll
    public static void tearDownClass() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    @Order(1)
    @DisplayName("1. CREATE: Debería registrar un nuevo proveedor en Supermaxi")
    public void testCrearProveedor() {
        Proveedor nuevo = new Proveedor(
                RUC_PRUEBA,
                "LA FAVORITA DISTRIBUIDORA S.A.",
                "Abarrotes y Despensa",
                "022444555",
                "contacto@favorita.com",
                "Av. General Enríquez, Sangolquí"
        );

        proveedorDAO.crear(nuevo);

        assertNotNull(nuevo.getId(), "El ID generado por JPA no debe ser nulo tras persistir");
        idProveedorPrueba = nuevo.getId();
    }

    @Test
    @Order(2)
    @DisplayName("2. READ: Debería buscar el proveedor por ID y por RUC con JPQL")
    public void testBuscarProveedor() {
        Proveedor porId = proveedorDAO.buscarPorId(idProveedorPrueba);
        assertNotNull(porId, "El proveedor debe existir al buscar por ID");
        assertEquals("LA FAVORITA DISTRIBUIDORA S.A.", porId.getRazonSocial());

        Proveedor porRuc = proveedorDAO.buscarPorRuc(RUC_PRUEBA);
        assertNotNull(porRuc, "El proveedor debe existir al buscar por RUC mediante JPQL");
        assertEquals(idProveedorPrueba, porRuc.getId());
    }

    @Test
    @Order(3)
    @DisplayName("3. READ (JPQL): Debería listar los proveedores activos")
    public void testListarProveedoresActivos() {
        List<Proveedor> activos = proveedorDAO.listarActivos();
        assertFalse(activos.isEmpty(), "La lista de proveedores activos no debe estar vacía");
        assertTrue(activos.stream().anyMatch(p -> RUC_PRUEBA.equals(p.getRuc())),
                "El proveedor de prueba debe figurar entre los activos");
    }

    @Test
    @Order(4)
    @DisplayName("4. UPDATE: Debería actualizar el teléfono y dirección del proveedor")
    public void testActualizarProveedor() {
        Proveedor proveedor = proveedorDAO.buscarPorId(idProveedorPrueba);
        assertNotNull(proveedor);

        proveedor.setTelefono("023888999");
        proveedor.setDireccion("Parque Industrial Sur, Quito");

        Proveedor actualizado = proveedorDAO.actualizar(proveedor);

        assertEquals("023888999", actualizado.getTelefono());
        assertEquals("Parque Industrial Sur, Quito", actualizado.getDireccion());
    }

    @Test
    @Order(5)
    @DisplayName("5. DELETE: Debería eliminar físicamente el proveedor de la base de datos")
    public void testEliminarProveedor() {
        proveedorDAO.eliminar(idProveedorPrueba);

        Proveedor eliminado = proveedorDAO.buscarPorId(idProveedorPrueba);
        assertNull(eliminado, "El proveedor ya no debe existir en la base de datos tras ser eliminado");
    }
}