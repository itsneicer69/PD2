package ec.edu.ups.supermaxi.dao;

import ec.edu.ups.supermaxi.modelo.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class ProductoDAO implements IGenericDAO<Producto, Long> {

    private final EntityManager em;

    public ProductoDAO(EntityManager em) {
        this.em = em;
    }

    // 1. CREATE: Registrar un nuevo producto en el catálogo de Supermaxi
    @Override
    public void crear(Producto producto) {
        try {
            em.getTransaction().begin();
            em.persist(producto);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al registrar el producto en BD: " + e.getMessage(), e);
        }
    }

    // 2. READ: Buscar producto por su clave primaria (@Id)
    @Override
    public Producto buscarPorId(Long id) {
        return em.find(Producto.class, id);
    }

    // 2.1 READ (JPQL): Listar todos los productos del catálogo
    @Override
    public List<Producto> listarTodos() {
        String jpql = "SELECT prod FROM Producto prod ORDER BY prod.nombre ASC";
        TypedQuery<Producto> query = em.createQuery(jpql, Producto.class);
        return query.getResultList();
    }

    // 3. UPDATE: Actualizar datos o stock de un producto existente
    @Override
    public Producto actualizar(Producto producto) {
        try {
            em.getTransaction().begin();
            Producto productoActualizado = em.merge(producto);
            em.getTransaction().commit();
            return productoActualizado;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al actualizar el producto en BD: " + e.getMessage(), e);
        }
    }

    // 4. DELETE: Eliminar físicamente un producto del catálogo
    @Override
    public void eliminar(Long id) {
        try {
            em.getTransaction().begin();
            Producto producto = em.find(Producto.class, id);
            if (producto != null) {
                em.remove(producto);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar el producto en BD: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // CONSULTAS ESPECÍFICAS CON JPQL PARA EL CATÁLOGO DE SUPERMAXI
    // =========================================================================

    // Buscar producto por código de barras / SKU
    public Producto buscarPorCodigoBarras(String codigoBarras) {
        String jpql = "SELECT prod FROM Producto prod WHERE prod.codigoBarras = :codigo";
        TypedQuery<Producto> query = em.createQuery(jpql, Producto.class);
        query.setParameter("codigo", codigoBarras);
        List<Producto> resultados = query.getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    // Listar todos los productos que abastece un proveedor específico (usando JOIN FETCH para evitar problema N+1)
    public List<Producto> listarPorProveedor(Long proveedorId) {
        String jpql = "SELECT prod FROM Producto prod JOIN FETCH prod.proveedor p WHERE p.id = :provId ORDER BY prod.nombre ASC";
        TypedQuery<Producto> query = em.createQuery(jpql, Producto.class);
        query.setParameter("provId", proveedorId);
        return query.getResultList();
    }
}