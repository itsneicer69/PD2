package ec.edu.ups.supermaxi.dao;

import ec.edu.ups.supermaxi.modelo.IngresoInventario;
import ec.edu.ups.supermaxi.modelo.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class IngresoInventarioDAO implements IGenericDAO<IngresoInventario, Long> {

    private final EntityManager em;

    public IngresoInventarioDAO(EntityManager em) {
        this.em = em;
    }

    // 1. CREATE: Registrar el ingreso de productos al inventario y sumar el stock automáticamente
    @Override
    public void crear(IngresoInventario ingreso) {
        try {
            em.getTransaction().begin();

            // Persistir el registro de ingreso de mercadería
            em.persist(ingreso);

            // Actualizar el stock actual del producto asociado en la misma transacción
            Producto producto = ingreso.getProducto();
            if (producto != null) {
                producto.setStockActual(producto.getStockActual() + ingreso.getCantidadIngresada());
                em.merge(producto);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al registrar el ingreso al inventario en BD: " + e.getMessage(), e);
        }
    }

    // 2. READ: Buscar un ingreso de inventario por su clave primaria (@Id)
    @Override
    public IngresoInventario buscarPorId(Long id) {
        return em.find(IngresoInventario.class, id);
    }

    // 2.1 READ (JPQL): Listar todos los ingresos ordenados por fecha (del más reciente al más antiguo)
    @Override
    public List<IngresoInventario> listarTodos() {
        String jpql = "SELECT i FROM IngresoInventario i ORDER BY i.fechaIngreso DESC";
        TypedQuery<IngresoInventario> query = em.createQuery(jpql, IngresoInventario.class);
        return query.getResultList();
    }

    // 3. UPDATE: Actualizar datos de un registro de ingreso existente
    @Override
    public IngresoInventario actualizar(IngresoInventario ingreso) {
        try {
            em.getTransaction().begin();
            IngresoInventario ingresoActualizado = em.merge(ingreso);
            em.getTransaction().commit();
            return ingresoActualizado;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al actualizar el ingreso de inventario en BD: " + e.getMessage(), e);
        }
    }

    // 4. DELETE: Eliminar un registro de ingreso y revertir la cantidad del stock del producto
    @Override
    public void eliminar(Long id) {
        try {
            em.getTransaction().begin();
            IngresoInventario ingreso = em.find(IngresoInventario.class, id);
            if (ingreso != null) {
                Producto producto = ingreso.getProducto();
                if (producto != null) {
                    int nuevoStock = Math.max(0, producto.getStockActual() - ingreso.getCantidadIngresada());
                    producto.setStockActual(nuevoStock);
                    em.merge(producto);
                }
                em.remove(ingreso);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar el ingreso de inventario en BD: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // CONSULTAS ESPECÍFICAS CON JPQL PARA BODEGA / INVENTARIO DE SUPERMAXI
    // =========================================================================

    // Listar el historial de ingresos de un proveedor específico
    public List<IngresoInventario> listarPorProveedor(Long proveedorId) {
        String jpql = "SELECT i FROM IngresoInventario i JOIN FETCH i.producto prod JOIN FETCH i.proveedor prov " +
                "WHERE prov.id = :provId ORDER BY i.fechaIngreso DESC";
        TypedQuery<IngresoInventario> query = em.createQuery(jpql, IngresoInventario.class);
        query.setParameter("provId", proveedorId);
        return query.getResultList();
    }

    // Listar el historial de ingresos de un producto específico
    public List<IngresoInventario> listarPorProducto(Long productoId) {
        String jpql = "SELECT i FROM IngresoInventario i JOIN FETCH i.producto prod " +
                "WHERE prod.id = :prodId ORDER BY i.fechaIngreso DESC";
        TypedQuery<IngresoInventario> query = em.createQuery(jpql, IngresoInventario.class);
        query.setParameter("prodId", productoId);
        return query.getResultList();
    }

    // Buscar ingresos por número de guía de remisión o lote
    public List<IngresoInventario> buscarPorNumeroGuia(String numeroGuia) {
        String jpql = "SELECT i FROM IngresoInventario i WHERE i.numeroGuiaRemision = :guia";
        TypedQuery<IngresoInventario> query = em.createQuery(jpql, IngresoInventario.class);
        query.setParameter("guia", numeroGuia);
        return query.getResultList();
    }
}