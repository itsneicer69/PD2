package ec.edu.ups.supermaxi.modelo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "ingresos_inventario")
public class IngresoInventario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_guia_remision", nullable = false, length = 40)
    private String numeroGuiaRemision;

    @Column(name = "cantidad_ingresada", nullable = false)
    private int cantidadIngresada;

    @Column(name = "costo_unitario_compra", nullable = false)
    private double costoUnitarioCompra;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "observaciones", length = 200)
    private String observaciones;

    // Relación N:1 con Producto (Qué producto ingresa a bodega)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Relación N:1 con Proveedor (Qué proveedor entregó la mercadería)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    public IngresoInventario() {
    }

    public IngresoInventario(String numeroGuiaRemision, int cantidadIngresada, double costoUnitarioCompra,
                             String observaciones, Producto producto, Proveedor proveedor) {
        this.numeroGuiaRemision = numeroGuiaRemision;
        this.cantidadIngresada = cantidadIngresada;
        this.costoUnitarioCompra = costoUnitarioCompra;
        this.fechaIngreso = LocalDateTime.now();
        this.observaciones = observaciones;
        this.producto = producto;
        this.proveedor = proveedor;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroGuiaRemision() { return numeroGuiaRemision; }
    public void setNumeroGuiaRemision(String numeroGuiaRemision) { this.numeroGuiaRemision = numeroGuiaRemision; }

    public int getCantidadIngresada() { return cantidadIngresada; }
    public void setCantidadIngresada(int cantidadIngresada) { this.cantidadIngresada = cantidadIngresada; }

    public double getCostoUnitarioCompra() { return costoUnitarioCompra; }
    public void setCostoUnitarioCompra(double costoUnitarioCompra) { this.costoUnitarioCompra = costoUnitarioCompra; }

    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDateTime fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    @Override
    public String toString() {
        return "IngresoInventario{" +
                "id=" + id +
                ", guia='" + numeroGuiaRemision + '\'' +
                ", cantidad=" + cantidadIngresada +
                ", costoUnitario=" + costoUnitarioCompra +
                ", fecha=" + fechaIngreso +
                '}';
    }
}