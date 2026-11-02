package ec.edu.ups.supermaxi.dao;

import ec.edu.ups.supermaxi.modelo.Proveedor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class ProveedorDAO implements IGenericDAO<Proveedor, Long> {

    private final EntityManager em;

    // Recibe el EntityManager desde JPAUtil
    public ProveedorDAO(EntityManager em) {
        this.em = em;
    }

    // 1. CREATE: Guardar un nuevo proveedor en la base de datos
    @Override
    public void crear(Proveedor proveedor) {
        try {
            em.getTransaction().begin();
            em.persist(proveedor); // Pasa la entidad a estado Managed
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al registrar el proveedor en BD: " + e.getMessage(), e);
        }
    }

    // 2. READ: Buscar un proveedor por su clave primaria (@Id)
    @Override
    public Proveedor buscarPorId(Long id) {
        return em.find(Proveedor.class, id);
    }

    // 2.1 READ (JPQL): Listar absolutamente todos los proveedores (activos e inactivos)
    @Override
    public List<Proveedor> listarTodos() {
        String jpql = "SELECT p FROM Proveedor p ORDER BY p.id ASC";
        TypedQuery<Proveedor> query = em.createQuery(jpql, Proveedor.class);
        return query.getResultList();
    }

    // 3. UPDATE: Actualizar los datos de un proveedor existente
    @Override
    public Proveedor actualizar(Proveedor proveedor) {
        try {
            em.getTransaction().begin();
            Proveedor proveedorActualizado = em.merge(proveedor); // Une la entidad al contexto de persistencia
            em.getTransaction().commit();
            return proveedorActualizado;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al actualizar el proveedor en BD: " + e.getMessage(), e);
        }
    }

    // 4. DELETE: Eliminar físicamente un proveedor de la base de datos por ID
    @Override
    public void eliminar(Long id) {
        try {
            em.getTransaction().begin();

            // 1. Eliminar primero los registros hijos en IngresoInventario para evitar el error de llave foránea (23503)
            em.createQuery("DELETE FROM IngresoInventario i WHERE i.proveedor.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();

            // 2. Buscar y eliminar el proveedor de forma segura
            Proveedor proveedor = em.find(Proveedor.class, id);
            if (proveedor != null) {
                em.remove(proveedor); // Pasa la entidad a estado Removed
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar el proveedor en BD: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // CONSULTAS ESPECÍFICAS CON JPQL PARA EL CASO DE ESTUDIO SUPERMAXI
    // =========================================================================

    // Buscar proveedor por su número de RUC (13 dígitos)
    public Proveedor buscarPorRuc(String ruc) {
        String jpql = "SELECT p FROM Proveedor p WHERE p.ruc = :ruc";
        TypedQuery<Proveedor> query = em.createQuery(jpql, Proveedor.class);
        query.setParameter("ruc", ruc);
        List<Proveedor> resultados = query.getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    // Listar únicamente los proveedores con estado activo = true
    public List<Proveedor> listarActivos() {
        String jpql = "SELECT p FROM Proveedor p WHERE p.activo = true ORDER BY p.razonSocial ASC";
        TypedQuery<Proveedor> query = em.createQuery(jpql, Proveedor.class);
        return query.getResultList();
    }

    // Buscar proveedores por categoría de suministro (ej. "Lácteos", "Cárnicos", "Abarrotes")
    public List<Proveedor> buscarPorCategoria(String categoria) {
        String jpql = "SELECT p FROM Proveedor p WHERE LOWER(p.categoriaSuministro) LIKE LOWER(:cat) AND p.activo = true";
        TypedQuery<Proveedor> query = em.createQuery(jpql, Proveedor.class);
        query.setParameter("cat", "%" + categoria + "%");
        return query.getResultList();
    }
}